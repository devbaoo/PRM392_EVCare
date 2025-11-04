# Debug API Response

Để fix lỗi `IllegalStateException`, cần xem actual API response.

## Cách 1: Thêm logging vào BookingHistoryActivity

Thêm code này vào `onResponse` của `BookingHistoryActivity.java`:

```java
@Override
public void onResponse(Call<BookingResponse> call, Response<BookingResponse> response) {
    // ADD THIS LINE TO SEE RAW RESPONSE
    Log.d("API_RESPONSE", "Raw response: " + response.raw().toString());
    Log.d("API_RESPONSE", "Response code: " + response.code());

    if (response.body() != null) {
        Log.d("API_RESPONSE", "Response body: " + response.body().toString());
    } else {
        Log.d("API_RESPONSE", "Response body is null");
        try {
            String errorBody = response.errorBody().string();
            Log.d("API_RESPONSE", "Error body: " + errorBody);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // ... rest of your code
}
```

## Cách 2: Test API bằng Postman/curl

```bash
curl -X GET "https://dolphin-app-pwai8.ondigitalocean.app/api/booking/my-bookings" \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

## Cách 3: Xem Logcat chi tiết

Trong Android Studio Logcat, filter với:

- Tag: `Retrofit` hoặc `OkHttp`
- Level: `Verbose`

Sẽ thấy full request/response JSON
