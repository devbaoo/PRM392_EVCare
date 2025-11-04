# UI Design Summary - EVCare Home Screen

## Tổng quan

Tôi đã thiết kế lại UI cho trang Home của ứng dụng EVCare để hiển thị danh sách các Service Centers dựa trên API response.

## Những thay đổi chính

### 1. Layout Item Service Center (`item_service_center.xml`)

Đã thiết kế lại card hiển thị thông tin service center với các thành phần:

#### Hình ảnh & Badge

- **Service Center Image**: Hiển thị hình ảnh từ API (180dp height)
- **Gradient Overlay**: Tạo hiệu ứng gradient phía dưới hình ảnh
- **Status Badge**: Chip hiển thị trạng thái Open/Closed (góc trên bên trái)
- **Rating Badge**: Hiển thị điểm đánh giá với icon sao (góc trên bên phải)

#### Thông tin chi tiết

- **Tên trung tâm**: Hiển thị đậm, 18sp
- **Mô tả**: Hiển thị description từ API (max 2 dòng)
- **Địa chỉ**: Icon location + địa chỉ đầy đủ (street, ward, district, city)
- **Khoảng cách**: Icon directions + khoảng cách tính bằng km
- **Giờ hoạt động**: Icon clock + giờ mở cửa hôm nay
- **Số điện thoại**: Icon phone + số điện thoại
- **Số lượng nhân viên**: Icon people + số lượng staff

### 2. Service Center Adapter (`ServiceCenterAdapter.java`)

Đã cập nhật adapter để bind data từ API:

#### Các tính năng chính:

- **Load hình ảnh**: Sử dụng Glide để load ảnh từ URL
- **Hiển thị rating**: Format điểm rating 1 chữ số thập phân
- **Tính toán giờ hoạt động**: Lấy giờ hoạt động theo ngày hiện tại
- **Kiểm tra trạng thái**: Tự động kiểm tra Open/Closed dựa trên giờ hiện tại
- **Format khoảng cách**: Hiển thị khoảng cách với 1 chữ số thập phân
- **Đếm nhân viên**: Hiển thị số lượng staff từ mảng staff

### 3. Service Center Model (`ServiceCenter.java`)

Đã thêm helper method:

```java
// Lấy thông tin giờ hoạt động theo ngày
public DaySchedule getScheduleForDay(String day)
```

### 4. Home Activity (`HomeActivity.java`)

Thay đổi layout manager:

```java
// Từ GridLayoutManager(2) -> LinearLayoutManager
recyclerViewServiceCenters.setLayoutManager(new LinearLayoutManager(this));
```

### 5. Drawable Resources

Tạo mới các drawable:

#### `gradient_overlay.xml`

Gradient từ trong suốt sang đen cho overlay ảnh

#### `badge_background.xml`

Background cho rating badge

### 6. String Resources

Đã thêm các string resources mới cho:

- Home screen text
- Service center details
- Navigation labels
- Common text

## API Integration

### API Endpoint

```
https://dolphin-app-pwai8.ondigitalocean.app/api/service-centers/nearby/search
?lat=10.762622
&lng=106.660172
&radius=5
```

### Data Mapping

Adapter tự động map các field từ API response:

- `name` → Tên trung tâm
- `description` → Mô tả
- `address` → Địa chỉ đầy đủ
- `contact.phone` → Số điện thoại
- `rating.average` → Điểm đánh giá
- `operatingHours` → Giờ hoạt động
- `staff` → Danh sách nhân viên
- `images[0].url` → Hình ảnh chính
- `distance` → Khoảng cách (nếu có)

## Design Principles

### Material Design 3

- Sử dụng MaterialCardView với corner radius 16dp
- Elevation 4dp cho shadow nhẹ
- Stroke border 1dp màu xám nhạt

### Typography

- Title: 18sp, Bold
- Description: 13sp, Regular
- Info text: 13sp, Regular
- Badge text: 12sp, Bold

### Colors

- Primary: #6366F1 (Indigo)
- Success (Open): #4CAF50 (Green)
- Error (Closed): #F44336 (Red)
- Rating star: #FFC107 (Amber)
- Text primary: #212121
- Text secondary: #757575

### Spacing

- Card margin: 8dp
- Content padding: 16dp
- Item spacing: 8-12dp

## Cách sử dụng

1. **Load Service Centers**: HomeActivity tự động gọi API và load danh sách
2. **Click vào card**: Mở màn hình chi tiết service center
3. **Filter theo radius**: Thay đổi spinner để điều chỉnh bán kính tìm kiếm
4. **Auto refresh**: Location update sẽ tự động load lại danh sách

## Các tính năng nổi bật

### ✅ Responsive Design

- Card tự động điều chỉnh chiều cao theo nội dung
- Text ellipsize khi quá dài

### ✅ Smart Status Display

- Tự động kiểm tra giờ hoạt động theo ngày trong tuần
- Hiển thị badge Open/Closed với màu phù hợp

### ✅ Image Loading

- Lazy loading với Glide
- Placeholder khi đang load
- Error fallback khi load thất bại

### ✅ Performance

- ViewHolder pattern cho RecyclerView
- Efficient data binding
- Smooth scrolling

## Screenshots

(Sẽ có khi chạy ứng dụng)

## Next Steps

1. Thêm animation khi scroll
2. Pull-to-refresh để reload
3. Skeleton loading state
4. Filter và sort options
5. Save favorite service centers
6. Booking flow integration
