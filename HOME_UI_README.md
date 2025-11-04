# EVCare - Service Center Home UI

## 🎯 Tổng quan

Trang Home hiển thị danh sách các Service Centers gần người dùng dựa trên vị trí GPS và bán kính tìm kiếm.

## ✅ Các tính năng đã hoàn thành

### 1. UI Components

- ✅ Card hiển thị Service Center với đầy đủ thông tin
- ✅ Image loading với Glide
- ✅ Status badge (Open/Closed) động
- ✅ Rating display với icon sao
- ✅ Thông tin địa chỉ đầy đủ
- ✅ Khoảng cách từ vị trí hiện tại
- ✅ Giờ hoạt động theo ngày
- ✅ Số điện thoại
- ✅ Số lượng nhân viên

### 2. Data Integration

- ✅ API integration với endpoint nearby search
- ✅ Model mapping cho tất cả fields
- ✅ Distance calculation
- ✅ Operating hours parsing
- ✅ Image URL handling

### 3. User Experience

- ✅ Smooth scrolling với RecyclerView
- ✅ Material Design 3 styling
- ✅ Click handling cho từng card
- ✅ Loading states
- ✅ Error states

## 📱 Cấu trúc Files

```
app/src/main/
├── java/com/example/prm392_evcare/
│   ├── HomeActivity.java (✅ Updated)
│   ├── adapters/
│   │   └── ServiceCenterAdapter.java (✅ Updated)
│   └── models/
│       └── ServiceCenter.java (✅ Updated)
├── res/
│   ├── layout/
│   │   ├── activity_home.xml (Existing)
│   │   └── item_service_center.xml (✅ Redesigned)
│   ├── drawable/
│   │   ├── gradient_overlay.xml (✅ New)
│   │   └── badge_background.xml (✅ New)
│   ├── values/
│   │   ├── strings.xml (✅ Updated)
│   │   ├── colors.xml (Existing)
│   │   └── arrays.xml (Existing)
```

## 🚀 Cách chạy

### Prerequisites

1. Android Studio (latest version)
2. Android SDK 24+
3. Internet connection (for API & images)
4. Location permission granted

### Build & Run

```bash
# 1. Mở project trong Android Studio
File -> Open -> chọn folder PRM392_EVCare

# 2. Sync Gradle
File -> Sync Project with Gradle Files

# 3. Run on device/emulator
Run -> Run 'app'
```

### Test với API

1. App sẽ tự động gọi API với location mặc định:

   - Lat: 10.762622
   - Lng: 106.660172
   - Radius: 10km

2. Kết quả sẽ hiển thị danh sách Service Centers

## 🎨 Design Specifications

### Card Layout

```
┌─────────────────────────────┐
│ [Image 180dp]               │ ← Service center image
│ Status Badge | Rating Badge │ ← Overlays
├─────────────────────────────┤
│ Service Center Name         │ ← 18sp Bold
│ Description                 │ ← 13sp Regular
├─────────────────────────────┤
│ 📍 Full Address            │
│ 🧭 Distance                │
│ 🕐 Operating Hours         │
│ 📞 Phone | 👥 Staff        │
└─────────────────────────────┘
```

### Colors

- **Open Status**: Green (#4CAF50)
- **Closed Status**: Red (#F44336)
- **Rating Star**: Amber (#FFC107)
- **Card Background**: White
- **Card Border**: Light Gray (#E0E0E0)

### Typography

- **Title**: 18sp, Bold, #212121
- **Body**: 13sp, Regular, #757575
- **Info**: 12sp, Regular, #424242

## 📊 API Response Mapping

### Endpoint

```
GET /api/service-centers/nearby/search
?lat={latitude}
&lng={longitude}
&radius={radius}
```

### Response Structure → UI Mapping

```json
{
  "data": [
    {
      "name": "..." → Card Title
      "description": "..." → Card Subtitle
      "address": {
        "street": "...",
        "ward": "...",
        "district": "...",
        "city": "..."
      } → Full Address Line
      "contact": {
        "phone": "..." → Phone Display
      },
      "rating": {
        "average": 4.5 → Rating Badge
      },
      "operatingHours": {
        "monday": {
          "open": "07:00",
          "close": "23:00",
          "isOpen": true
        }
      } → Operating Hours + Status
      "staff": [...] → Staff Count
      "images": [
        {"url": "..."} → Card Image
      ]
    }
  ]
}
```

## 🔧 Customization

### Thay đổi số lượng cột

In `HomeActivity.java`:

```java
// Current: 1 column (LinearLayoutManager)
recyclerViewServiceCenters.setLayoutManager(new LinearLayoutManager(this));

// Change to: 2 columns
recyclerViewServiceCenters.setLayoutManager(new GridLayoutManager(this, 2));
```

### Thay đổi bán kính mặc định

In `arrays.xml`:

```xml
<array name="search_radius">
    <item>5 km</item>
    <item>10 km</item>  <!-- Default -->
    <item>15 km</item>
    ...
</array>
```

### Thay đổi placeholder image

In `ServiceCenterAdapter.java`:

```java
Glide.with(context)
    .load(imageUrl)
    .placeholder(R.drawable.your_placeholder) // Change here
    .error(R.drawable.your_error_image)      // Change here
    .into(holder.ivServiceCenterImage);
```

## 🐛 Troubleshooting

### Issue: Images không load

**Solution**: Kiểm tra internet permission trong `AndroidManifest.xml`

```xml
<uses-permission android:name="android.permission.INTERNET" />
```

### Issue: Không có data

**Solution**:

1. Kiểm tra API endpoint có đúng không
2. Kiểm tra network connection
3. Check logcat cho error message

### Issue: Layout bị lỗi

**Solution**:

1. Clean & Rebuild project
2. Invalidate Caches and Restart
3. Sync Gradle files

## 📝 TODO / Future Enhancements

### Planned Features

- [ ] Pull-to-refresh
- [ ] Infinite scroll/pagination
- [ ] Skeleton loading animation
- [ ] Filter by services
- [ ] Sort options (distance, rating, name)
- [ ] Favorite/bookmark service centers
- [ ] Share service center info
- [ ] Navigate to maps
- [ ] Call directly from card
- [ ] Booking integration

### Performance Improvements

- [ ] Image caching strategy
- [ ] Lazy loading
- [ ] ViewBinding instead of findViewById
- [ ] Data binding

### UI Enhancements

- [ ] Shimmer effect while loading
- [ ] Item enter animations
- [ ] Empty state illustration
- [ ] Error state illustration
- [ ] Swipe actions (call, navigate, bookmark)

## 📱 Screenshots

(Add screenshots here when running)

## 👥 Contributors

- UI Design: [Your Name]
- Implementation Date: November 4, 2025

## 📄 License

EVCare - Electric Vehicle Care Management System
