package com.example.prm392_evcare;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.prm392_evcare.api.ApiClient;
import com.example.prm392_evcare.api.ApiService;
import com.example.prm392_evcare.models.UserProfile;
import com.example.prm392_evcare.models.UserProfileResponse;
import com.example.prm392_evcare.utils.SessionManager;
import com.google.android.material.button.MaterialButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UploadAvatarActivity extends AppCompatActivity {
    
    private static final String TAG = "UploadAvatarActivity";
    public static final String EXTRA_CURRENT_AVATAR = "extra_current_avatar";
    public static final String EXTRA_UPDATED_USER = "extra_updated_user";
    public static final int REQUEST_CODE_UPLOAD_AVATAR = 2001;
    
    private SessionManager sessionManager;
    private ApiService apiService;
    
    // Views
    private ImageView ivPreview;
    private MaterialButton btnSelectGallery;
    private MaterialButton btnTakePhoto;
    private MaterialButton btnUpload;
    private MaterialButton btnCancel;
    private ProgressBar progressBar;
    
    private Uri selectedImageUri;
    private String currentAvatarUrl;
    
    // Activity Result Launchers
    private ActivityResultLauncher<Intent> galleryLauncher;
    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<String> cameraPermissionLauncher;
    private ActivityResultLauncher<String> storagePermissionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_avatar);

        // Initialize session manager and API
        sessionManager = new SessionManager(this);
        apiService = ApiClient.getClient().create(ApiService.class);
        
        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Cập nhật Avatar");
        }
        
        // Get current avatar from intent
        if (getIntent().hasExtra(EXTRA_CURRENT_AVATAR)) {
            currentAvatarUrl = getIntent().getStringExtra(EXTRA_CURRENT_AVATAR);
        }
        
        // Initialize views
        initializeViews();
        
        // Setup activity result launchers
        setupActivityResultLaunchers();
        
        // Set up click listeners
        setupClickListeners();
        
        // Load current avatar if available
        loadCurrentAvatar();
    }
    
    private void initializeViews() {
        ivPreview = findViewById(R.id.ivPreview);
        btnSelectGallery = findViewById(R.id.btnSelectGallery);
        btnTakePhoto = findViewById(R.id.btnTakePhoto);
        btnUpload = findViewById(R.id.btnUpload);
        btnCancel = findViewById(R.id.btnCancel);
        progressBar = findViewById(R.id.progressBar);
        
        // Disable upload button initially
        btnUpload.setEnabled(false);
    }
    
    private void setupActivityResultLaunchers() {
        // Gallery launcher
        galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    displaySelectedImage();
                }
            }
        );
        
        // Camera launcher
        cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Bundle extras = result.getData().getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    if (imageBitmap != null) {
                        selectedImageUri = saveBitmapToFile(imageBitmap);
                        displaySelectedImage();
                    }
                }
            }
        );
        
        // Camera permission launcher
        cameraPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if (isGranted) {
                    openCamera();
                } else {
                    Toast.makeText(this, "Cần quyền truy cập camera", Toast.LENGTH_SHORT).show();
                }
            }
        );
        
        // Storage permission launcher (for Android 12 and below)
        storagePermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if (isGranted) {
                    openGallery();
                } else {
                    Toast.makeText(this, "Cần quyền truy cập thư viện ảnh", Toast.LENGTH_SHORT).show();
                }
            }
        );
    }
    
    private void setupClickListeners() {
        btnSelectGallery.setOnClickListener(v -> checkStoragePermissionAndOpenGallery());
        
        btnTakePhoto.setOnClickListener(v -> checkCameraPermissionAndOpenCamera());
        
        btnUpload.setOnClickListener(v -> {
            if (selectedImageUri != null) {
                uploadAvatar();
            } else {
                Toast.makeText(this, "Vui lòng chọn ảnh trước", Toast.LENGTH_SHORT).show();
            }
        });
        
        btnCancel.setOnClickListener(v -> finish());
    }
    
    private void loadCurrentAvatar() {
        if (currentAvatarUrl != null && !currentAvatarUrl.isEmpty()) {
            Glide.with(this)
                .load(currentAvatarUrl)
                .placeholder(R.drawable.ic_person)
                .error(R.drawable.ic_person)
                .transition(DrawableTransitionOptions.withCrossFade())
                .circleCrop()
                .into(ivPreview);
        }
    }
    
    private void checkStoragePermissionAndOpenGallery() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+ doesn't need READ_EXTERNAL_STORAGE permission for picking images
            openGallery();
        } else {
            // For Android 12 and below
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                storagePermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        }
    }
    
    private void checkCameraPermissionAndOpenCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            openCamera();
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }
    
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        galleryLauncher.launch(intent);
    }
    
    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            cameraLauncher.launch(intent);
        } else {
            Toast.makeText(this, "Không tìm thấy ứng dụng camera", Toast.LENGTH_SHORT).show();
        }
    }
    
    private Uri saveBitmapToFile(Bitmap bitmap) {
        try {
            File file = new File(getCacheDir(), "avatar_" + System.currentTimeMillis() + ".jpg");
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            fos.close();
            return Uri.fromFile(file);
        } catch (IOException e) {
            Log.e(TAG, "Error saving bitmap: " + e.getMessage());
            return null;
        }
    }
    
    private void displaySelectedImage() {
        if (selectedImageUri != null) {
            Glide.with(this)
                .load(selectedImageUri)
                .placeholder(R.drawable.ic_person)
                .error(R.drawable.ic_person)
                .transition(DrawableTransitionOptions.withCrossFade())
                .circleCrop()
                .into(ivPreview);
            
            // Enable upload button
            btnUpload.setEnabled(true);
        }
    }
    
    private void uploadAvatar() {
        progressBar.setVisibility(View.VISIBLE);
        btnUpload.setEnabled(false);
        btnSelectGallery.setEnabled(false);
        btnTakePhoto.setEnabled(false);
        
        String token = sessionManager.getAuthToken();
        if (token == null || token.isEmpty()) {
            Toast.makeText(this, "Session expired. Please login again.", Toast.LENGTH_SHORT).show();
            navigateToLogin();
            return;
        }
        
        try {
            // Create file from URI
            File file = new File(getRealPathFromURI(selectedImageUri));
            
            // Create RequestBody
            RequestBody requestFile = RequestBody.create(
                MediaType.parse("image/*"),
                file
            );
            
            // Create MultipartBody.Part
            MultipartBody.Part avatarPart = MultipartBody.Part.createFormData(
                "avatar", 
                file.getName(), 
                requestFile
            );
            
            // Make API call
            Call<UserProfileResponse> call = apiService.uploadAvatar("Bearer " + token, avatarPart);
            call.enqueue(new Callback<UserProfileResponse>() {
                @Override
                public void onResponse(@NonNull Call<UserProfileResponse> call, 
                                     @NonNull Response<UserProfileResponse> response) {
                    progressBar.setVisibility(View.GONE);
                    btnUpload.setEnabled(true);
                    btnSelectGallery.setEnabled(true);
                    btnTakePhoto.setEnabled(true);
                    
                    if (response.isSuccessful() && response.body() != null) {
                        UserProfileResponse profileResponse = response.body();
                        if (profileResponse.isSuccess()) {
                            Toast.makeText(UploadAvatarActivity.this, 
                                profileResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            
                            // Return updated user to ProfileActivity
                            Intent resultIntent = new Intent();
                            resultIntent.putExtra(EXTRA_UPDATED_USER, profileResponse.getUser());
                            setResult(RESULT_OK, resultIntent);
                            finish();
                        } else {
                            Toast.makeText(UploadAvatarActivity.this, 
                                "Failed to upload avatar", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(UploadAvatarActivity.this, 
                            "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                        
                        if (response.code() == 401) {
                            navigateToLogin();
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<UserProfileResponse> call, @NonNull Throwable t) {
                    progressBar.setVisibility(View.GONE);
                    btnUpload.setEnabled(true);
                    btnSelectGallery.setEnabled(true);
                    btnTakePhoto.setEnabled(true);
                    Log.e(TAG, "Error uploading avatar: " + t.getMessage());
                    Toast.makeText(UploadAvatarActivity.this, 
                        "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
            
        } catch (Exception e) {
            progressBar.setVisibility(View.GONE);
            btnUpload.setEnabled(true);
            btnSelectGallery.setEnabled(true);
            btnTakePhoto.setEnabled(true);
            Log.e(TAG, "Error preparing upload: " + e.getMessage());
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    
    private String getRealPathFromURI(Uri uri) {
        if (uri.getScheme().equals("file")) {
            return uri.getPath();
        }
        
        // For content:// URIs, copy to cache
        try {
            File file = new File(getCacheDir(), "avatar_upload_" + System.currentTimeMillis() + ".jpg");
            FileOutputStream fos = new FileOutputStream(file);
            
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            fos.close();
            
            return file.getAbsolutePath();
        } catch (IOException e) {
            Log.e(TAG, "Error getting real path: " + e.getMessage());
            return null;
        }
    }
    
    private void navigateToLogin() {
        sessionManager.clearSession();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
    
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
