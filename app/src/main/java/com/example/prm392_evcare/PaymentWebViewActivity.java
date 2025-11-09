package com.example.prm392_evcare;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class PaymentWebViewActivity extends AppCompatActivity {

    public static final String EXTRA_PAYMENT_URL = "payment_url";
    public static final String EXTRA_BOOKING_ID = "booking_id";

    private WebView webView;
    private ProgressBar progressBar;
    private String bookingId;
    private boolean handled = false;

    // Domains / Deep Links
    private static final String DOMAIN = "yourdomain.com"; // TODO replace
    private static final String SUCCESS_PATH = "/payment/result";
    private static final String CANCEL_PATH = "/payment/cancel";
    private static final String DEEP_LINK_SCHEME = "evcare";
    private static final String DEEP_LINK_HOST = "payment";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_webview);

        setupToolbar();

        webView = findViewById(R.id.webView);
        progressBar = findViewById(R.id.progressBar);

        String paymentUrl = getIntent().getStringExtra(EXTRA_PAYMENT_URL);
        bookingId = getIntent().getStringExtra(EXTRA_BOOKING_ID);

        if (paymentUrl == null) {
            Toast.makeText(this, "Không tìm thấy link thanh toán", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        configureWebView();
        webView.loadUrl(paymentUrl);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Thanh toán đặt cọc");
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void configureWebView() {
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(false);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                progressBar.setVisibility(View.VISIBLE);
                super.onPageStarted(view, url, favicon);
                handlePossibleRedirect(url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                progressBar.setVisibility(View.GONE);
                super.onPageFinished(view, url);
                // Heuristic: check page title/text for outcome keywords in Vietnamese
                if (!handled) {
                    view.evaluateJavascript("document.title", value -> {
                        String title = value != null ? value.replace("\"", "") : "";
                        detectOutcomeFromText(title);
                    });
                    view.evaluateJavascript("document.body && document.body.innerText ? document.body.innerText : ''", value -> {
                        String text = value != null ? value.replace("\\n", " ").replace("\"", "") : "";
                        detectOutcomeFromText(text);
                    });
                }
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString();
                handlePossibleRedirect(url);
                return false; // allow WebView to load
            }
        });
    }

    private void handlePossibleRedirect(String url) {
        try {
            Uri uri = Uri.parse(url);
            String host = uri.getHost();
            String path = uri.getPath();
            String scheme = uri.getScheme();

            if (handled) return;
            if (host == null) host = "";
            if (path == null) path = "";

            // Success / Cancel detection via web domain
            if (DOMAIN.equalsIgnoreCase(host)) {
                if (SUCCESS_PATH.equalsIgnoreCase(path)) {
                    handled = true; onPaymentSuccess(uri.getQueryParameter("status"));
                    return;
                } else if (CANCEL_PATH.equalsIgnoreCase(path)) {
                    handled = true; onPaymentCancel();
                    return;
                } else if ("/".equals(path) || path.isEmpty()) {
                    // If it navigates user back to home, consider flow done
                    handled = true; onPaymentCancel();
                    return;
                }
            }

            // Deep link scheme detection (if PayOS redirected directly)
            if (DEEP_LINK_SCHEME.equalsIgnoreCase(scheme) && DEEP_LINK_HOST.equalsIgnoreCase(host)) {
                String status = uri.getQueryParameter("status");
                if ("success".equalsIgnoreCase(status) || "paid".equalsIgnoreCase(status)) {
                    handled = true; onPaymentSuccess(status); return;
                } else if ("cancel".equalsIgnoreCase(status)) {
                    handled = true; onPaymentCancel(); return;
                } else if ("failed".equalsIgnoreCase(status)) {
                    handled = true; onPaymentFailed(); return;
                }
            }

            // Generic status in query params no matter the domain/path
            String statusQ = uri.getQueryParameter("status");
            if (!handled && statusQ != null) {
                if (statusQ.equalsIgnoreCase("success") || statusQ.equalsIgnoreCase("paid") || statusQ.equalsIgnoreCase("completed")) {
                    handled = true; onPaymentSuccess(statusQ); return;
                }
                if (statusQ.equalsIgnoreCase("cancel") || statusQ.equalsIgnoreCase("cancelled") || statusQ.equalsIgnoreCase("canceled")) {
                    handled = true; onPaymentCancel(); return;
                }
                if (statusQ.equalsIgnoreCase("failed") || statusQ.equalsIgnoreCase("fail") || statusQ.equalsIgnoreCase("error")) {
                    handled = true; onPaymentFailed(); return;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void detectOutcomeFromText(String text) {
        if (handled || text == null) return;
        String lower = text.toLowerCase();
        if (lower.contains("thanh toán thành công") || lower.contains("đã thanh toán") || lower.contains("giao dịch thành công")) {
            handled = true; onPaymentSuccess("success"); return;
        }
        if (lower.contains("thanh toán thất bại") || lower.contains("giao dịch không thể hoàn thành") || lower.contains("thất bại")) {
            handled = true; onPaymentFailed(); return;
        }
        if (lower.contains("đã hủy") || lower.contains("hủy thanh toán") || lower.contains("hủy giao dịch")) {
            handled = true; onPaymentCancel(); return;
        }
    }

    private void onPaymentSuccess(String status) {
        navigateDirectToHistory();
    }

    private void onPaymentCancel() {
        navigateDirectToHistory();
    }

    private void onPaymentFailed() {
        navigateDirectToHistory();
    }

    private void navigateDirectToHistory() {
        // Launch booking history without wiping entire task so Back can work.
        // Pass a flag so BookingHistoryActivity can customize its back behavior
        // (e.g., go to HomeActivity instead of returning to the confirmation/payment flow).
        android.content.Intent i = new android.content.Intent(this, BookingHistoryActivity.class);
        i.putExtra("fromPaymentFlow", true);
        startActivity(i);
        // Finish this payment screen so user can't return to WebView.
        finish();
    }
}