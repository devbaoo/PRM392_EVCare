# 🚀 Quick Start Guide - EVCare Home UI

## Chạy ngay sau 3 bước!

### Bước 1: Mở Project

```
1. Mở Android Studio
2. File -> Open
3. Chọn folder: PRM392_EVCare
4. Đợi Gradle sync xong
```

### Bước 2: Kiểm tra Dependencies

File `app/build.gradle.kts` cần có:

```kotlin
dependencies {
    // Glide for image loading
    implementation("com.github.bumptech.glide:glide:4.15.1")

    // Retrofit for API
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // Material Design
    implementation("com.google.android.material:material:1.10.0")
}
```

### Bước 3: Run App

```
1. Kết nối Android device hoặc start emulator
2. Click Run button (hoặc Shift+F10)
3. Chọn device
4. Đợi app cài đặt và chạy
```

## ✨ Kết quả mong đợi

### Home Screen sẽ hiển thị:

1. ✅ Header với gradient background
2. ✅ "Đặt lịch bảo dưỡng" button
3. ✅ Search radius selector (5km, 10km, 15km...)
4. ✅ Danh sách Service Centers với:
   - Hình ảnh service center
   - Open/Closed badge
   - Rating star badge
   - Tên và mô tả
   - Địa chỉ đầy đủ
   - Khoảng cách (km)
   - Giờ hoạt động
   - Số điện thoại
   - Số lượng staff

## 🎯 Test Cases

### Test 1: Xem danh sách Service Centers

1. Mở app
2. Login (nếu chưa)
3. Trang Home sẽ tự động load danh sách
4. ✅ Kết quả: Hiển thị 2 service centers từ API

### Test 2: Click vào Service Center

1. Tap vào bất kỳ card nào
2. ✅ Kết quả: Toast hiển thị "Selected: [Tên service center]"

### Test 3: Thay đổi Search Radius

1. Tap vào Spinner "Search Radius"
2. Chọn radius khác (5km, 10km, etc.)
3. ✅ Kết quả: Danh sách sẽ reload với radius mới

### Test 4: Scroll danh sách

1. Scroll lên xuống
2. ✅ Kết quả: Smooth scrolling, không lag

### Test 5: Kiểm tra Status Badge

1. Xem badge góc trái card
2. ✅ Màu xanh = "Open"
3. ✅ Màu đỏ = "Closed"

### Test 6: Kiểm tra Rating

1. Xem badge góc phải card
2. ✅ Hiển thị số sao (4.5 hoặc N/A)

## 📊 Expected API Data

### Service Center 1: EVCare HCM Center

```
Name: EVCare HCM Center
Address: 123 Lê Lợi, Bến Thành, Quận 1, Hồ Chí Minh
Phone: 02837651234
Rating: 0.0 (1 review)
Staff: 3 Staff
Hours: 07:00 - 23:15
Status: Open (depends on current time)
```

### Service Center 2: EVCare HCM Tân Bình

```
Name: EVCare HCM Tân Bình
Address: 210 Cộng Hòa, 4, Tân Bình, Hồ Chí Minh
Phone: 02837651234
Rating: 0.0 (0 reviews)
Staff: 3 Staff
Hours: 07:00 - 21:00
Status: Open (depends on current time)
```

## 🐛 Common Issues & Solutions

### Problem: "No service centers found"

**Solutions:**

1. Kiểm tra internet connection
2. Check API endpoint trong `ApiService.java`
3. Verify location permissions
4. Check logcat cho error details

### Problem: Images không hiển thị

**Solutions:**

1. Kiểm tra INTERNET permission
2. Verify Glide dependency
3. Check image URLs trong API response
4. Clear app cache và retry

### Problem: App crash khi load

**Solutions:**

1. Check logcat stack trace
2. Verify all model classes match API response
3. Ensure Retrofit & Gson properly configured
4. Check ProGuard rules if using release build

### Problem: Layout bị vỡ

**Solutions:**

1. Clean Project (Build -> Clean Project)
2. Rebuild (Build -> Rebuild Project)
3. Invalidate Caches (File -> Invalidate Caches)
4. Check XML syntax errors

## 🔍 Debug Tips

### Enable Logging

Trong `ServiceCenterAdapter.java`, thêm logs:

```java
@Override
public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    ServiceCenter sc = serviceCenters.get(position);
    Log.d("ServiceCenter", "Binding: " + sc.getName());
    Log.d("ServiceCenter", "Rating: " + sc.getRating().getAverage());
    Log.d("ServiceCenter", "Staff: " + sc.getStaff().size());
    // ... rest of binding code
}
```

### Check API Response

Trong `HomeActivity.java`:

```java
@Override
public void onResponse(Call<NearbyServiceCentersResponse> call,
                      Response<NearbyServiceCentersResponse> response) {
    if (response.isSuccessful() && response.body() != null) {
        Log.d(TAG, "API Response: " + response.body().getData().size() + " centers");
        // ... rest of code
    }
}
```

## 📱 Device Requirements

### Minimum

- Android 7.0 (API 24)
- 2GB RAM
- Screen size: 5" or larger

### Recommended

- Android 10+ (API 29+)
- 4GB RAM
- Screen size: 6" or larger
- Internet: 4G/WiFi

## 🎨 Visual Reference

### Card Component Breakdown

```
╔═══════════════════════════════════╗
║ [180dp Height Image]              ║
║ 🟢 Open         ⭐ 4.5           ║
╠═══════════════════════════════════╣
║ EVCare HCM Center                 ║ ← 18sp Bold
║ Trung tâm bảo dưỡng EV...        ║ ← 13sp Regular
╠═══════════════════════════════════╣
║ 📍 123 Lê Lợi, Bến Thành...     ║
║ 🧭 2.8 km away                   ║
║ 🕐 07:00 - 23:15 (Open)          ║
║ 📞 028-3765-1234  👥 3 Staff    ║
╚═══════════════════════════════════╝
     8dp margin     16dp padding
```

## ✅ Checklist trước khi submit

- [ ] App build thành công
- [ ] Không có compile errors
- [ ] Không có runtime crashes
- [ ] API call thành công
- [ ] Images load được
- [ ] All UI elements hiển thị đúng
- [ ] Status badge đúng màu
- [ ] Rating hiển thị chính xác
- [ ] Address format đúng
- [ ] Operating hours đúng
- [ ] Click handlers work
- [ ] Smooth scrolling
- [ ] Code đã format đẹp
- [ ] Comments đầy đủ
- [ ] No hardcoded strings (use strings.xml)

## 🎓 Learning Points

### 1. RecyclerView với ViewHolder Pattern

```java
static class ViewHolder extends RecyclerView.ViewHolder {
    // Efficient view caching
}
```

### 2. Glide Image Loading

```java
Glide.with(context)
    .load(url)
    .placeholder(...)
    .error(...)
    .into(imageView);
```

### 3. Material CardView

```xml
<MaterialCardView
    app:cardCornerRadius="16dp"
    app:cardElevation="4dp"
    app:strokeWidth="1dp">
```

### 4. Data Binding từ API

```java
serviceCenter.getName() → TextView
serviceCenter.getRating().getAverage() → Rating display
serviceCenter.getStaff().size() → Staff count
```

## 🚀 Next Steps

Sau khi hoàn thành Home UI:

1. ✅ Implement Service Center Detail screen
2. ✅ Add booking functionality
3. ✅ Integrate Google Maps
4. ✅ Add search & filter
5. ✅ Implement favorites
6. ✅ Add user reviews & ratings

---

**Happy Coding! 🎉**

Nếu có vấn đề, check:

1. Logcat
2. Stack trace
3. API response
4. Layout inspector
