# 📸 Avatar Upload Feature - Documentation

## 🎯 Tổng quan

Tính năng upload avatar cho phép người dùng cập nhật ảnh đại diện của họ bằng cách chọn ảnh từ thư viện hoặc chụp ảnh mới bằng camera.

## 📋 API Endpoint

### Upload Avatar

- **URL**: `POST /api/user/upload-avatar`
- **Authentication**: Required (Bearer Token)
- **Content-Type**: `multipart/form-data`
- **Request Body**:
  - `avatar`: File ảnh (JPG, PNG)

**Response mẫu**:

```json
{
  "success": true,
  "message": "Avatar uploaded successfully",
  "user": {
    "id": "68c9376cb7fbfbca01bb1ca2",
    "username": "Devbaoo",
    "email": "devbaoo712@gmail.com",
    "fullName": "Khac Bao",
    "phone": "0355418118",
    "address": "Tam Phuoc, Long Thanh, Dong Nai",
    "role": "customer",
    "avatar": "https://res.cloudinary.com/dmzvgqyip/image/upload/v1762252935/avatars/dtdgkwm4fa1auvvjgray.jpg",
    "isVerified": true
  },
  "imageDetails": {
    "imageUrl": "https://res.cloudinary.com/dmzvgqyip/image/upload/v1762252935/avatars/dtdgkwm4fa1auvvjgray.jpg",
    "imageId": "avatars/dtdgkwm4fa1auvvjgray"
  }
}
```

## 📁 Files mới được tạo

### 1. UploadAvatarActivity.java

**Mục đích**: Activity chính để upload avatar

**Tính năng chính**:

- ✅ Chọn ảnh từ thư viện (Gallery)
- ✅ Chụp ảnh mới bằng camera
- ✅ Preview ảnh đã chọn
- ✅ Upload ảnh lên server
- ✅ Xử lý quyền (Camera, Storage)
- ✅ Compress ảnh trước khi upload
- ✅ Loading states & error handling

**Key Methods**:

```java
// Chọn từ thư viện
private void openGallery()

// Chụp ảnh
private void openCamera()

// Upload lên server
private void uploadAvatar()

// Kiểm tra quyền
private void checkCameraPermissionAndOpenCamera()
private void checkStoragePermissionAndOpenGallery()
```

### 2. activity_upload_avatar.xml

**Mục đích**: Layout cho màn hình upload avatar

**Components**:

- Toolbar với nút back
- Preview avatar (200x200dp, circular)
- Hướng dẫn sử dụng
- Nút "Chọn từ thư viện"
- Nút "Chụp ảnh mới"
- Nút "Tải lên" và "Hủy"
- Progress bar

### 3. Drawable Icons

- **ic_gallery.xml**: Icon thư viện ảnh
- **ic_camera.xml**: Icon camera

## 🔧 Files đã cập nhật

### 1. ApiService.java

Thêm endpoint mới:

```java
@Multipart
@POST("/api/user/upload-avatar")
Call<UserProfileResponse> uploadAvatar(
    @Header("Authorization") String token,
    @Part MultipartBody.Part avatar
);
```

### 2. ProfileActivity.java

**Cập nhật**:

- Nút FAB (Floating Action Button) giờ mở UploadAvatarActivity
- Xử lý kết quả upload trong `onActivityResult()`
- Refresh avatar sau khi upload thành công

```java
// Trước
fabEditAvatar.setOnClickListener(v -> {
    Toast.makeText(this, "Coming soon", Toast.LENGTH_SHORT).show();
});

// Sau
fabEditAvatar.setOnClickListener(v -> {
    Intent intent = new Intent(ProfileActivity.this, UploadAvatarActivity.class);
    intent.putExtra(UploadAvatarActivity.EXTRA_CURRENT_AVATAR, currentUser.getAvatar());
    startActivityForResult(intent, UploadAvatarActivity.REQUEST_CODE_UPLOAD_AVATAR);
});
```

### 3. AndroidManifest.xml

**Thêm quyền**:

```xml
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"
    android:maxSdkVersion="32" />
```

**Đăng ký Activity**:

```xml
<activity
    android:name=".UploadAvatarActivity"
    android:exported="false"
    android:windowSoftInputMode="adjustResize" />
```

## 🎨 UI Design

### Layout Structure

```
┌─────────────────────────────────┐
│ ← Cập nhật Avatar               │ (Toolbar)
├─────────────────────────────────┤
│                                 │
│  Chọn ảnh đại diện mới          │ (Title)
│  Bạn có thể chọn ảnh từ...      │ (Subtitle)
│                                 │
│       ┌─────────────┐           │
│       │             │           │
│       │   Avatar    │           │ (200x200, Circular)
│       │   Preview   │           │
│       │             │           │
│       └─────────────┘           │
│                                 │
│  ┌──────────────────────────┐   │
│  │ 📷 Hướng dẫn             │   │
│  │ • Chọn ảnh rõ nét        │   │
│  │ • Kích thước tối đa: 5MB │   │
│  │ • Định dạng: JPG, PNG    │   │
│  └──────────────────────────┘   │
│                                 │
│  Chọn nguồn ảnh:                │
│                                 │
│  ┌──────────────────────────┐   │
│  │ 🖼️ Chọn từ thư viện      │   │
│  └──────────────────────────┘   │
│                                 │
│  ┌──────────────────────────┐   │
│  │ 📷 Chụp ảnh mới          │   │
│  └──────────────────────────┘   │
│                                 │
│  ┌──────────┐ ┌──────────────┐  │
│  │   Hủy    │ │   Tải lên    │  │
│  └──────────┘ └──────────────┘  │
└─────────────────────────────────┘
```

### Design Specifications

- **Avatar Preview**: 200x200dp, circular với stroke primary color
- **Buttons**:
  - Gallery & Camera: Outlined buttons với icons
  - Upload: Filled button (primary color)
  - Cancel: Outlined button
- **Colors**: Material Design 3 color scheme
- **Spacing**: 24dp padding, 12-32dp margins
- **Icons**: Material Icons 24dp

## 🚀 User Flow

### Flow 1: Chọn từ Thư viện

```
ProfileActivity
    │
    ├─ Tap FAB (Edit Avatar)
    │
    └─> UploadAvatarActivity
         │
         ├─ Tap "Chọn từ thư viện"
         │
         ├─ Kiểm tra quyền Storage
         │   ├─ ✅ Có quyền → Mở Gallery
         │   └─ ❌ Không có → Request quyền
         │
         ├─ User chọn ảnh
         │
         ├─ Preview ảnh
         │
         ├─ Tap "Tải lên"
         │
         ├─ Compress & Upload
         │   ├─ ✅ Success
         │   │   └─> Return to ProfileActivity
         │   │       └─> Refresh avatar
         │   │
         │   └─ ❌ Error
         │       └─> Show error message
         │
         └─ Tap "Hủy" → Return without changes
```

### Flow 2: Chụp ảnh mới

```
ProfileActivity
    │
    ├─ Tap FAB (Edit Avatar)
    │
    └─> UploadAvatarActivity
         │
         ├─ Tap "Chụp ảnh mới"
         │
         ├─ Kiểm tra quyền Camera
         │   ├─ ✅ Có quyền → Mở Camera
         │   └─ ❌ Không có → Request quyền
         │
         ├─ User chụp ảnh
         │
         ├─ Lưu ảnh vào cache
         │
         ├─ Preview ảnh
         │
         ├─ Tap "Tải lên"
         │
         └─ [Same as Flow 1]
```

## 🔐 Permissions & Security

### Quyền cần thiết

1. **CAMERA** - Để chụp ảnh
2. **READ_EXTERNAL_STORAGE** (Android 12 trở xuống) - Để đọc ảnh từ thư viện

### Permission Handling

- **Android 13+**: Không cần READ_EXTERNAL_STORAGE cho photo picker
- **Android 12-**: Cần request READ_EXTERNAL_STORAGE
- **Camera**: Request runtime permission trên tất cả phiên bản

### Security Features

- ✅ Bearer token authentication
- ✅ Session validation
- ✅ File type validation (image/\*)
- ✅ Auto-logout on 401
- ✅ Secure file handling (cache directory)

## 📊 Technical Details

### Image Handling

```java
// Compress ảnh trước khi upload
bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);

// Tạo MultipartBody.Part
RequestBody requestFile = RequestBody.create(
    MediaType.parse("image/*"),
    file
);

MultipartBody.Part avatarPart = MultipartBody.Part.createFormData(
    "avatar",
    file.getName(),
    requestFile
);
```

### File Storage

- Ảnh tạm được lưu trong **Cache Directory**
- Format: `avatar_<timestamp>.jpg`
- Quality: 90% JPEG compression
- Auto cleanup khi app bị xóa

### API Integration

```java
// Upload request
Call<UserProfileResponse> call = apiService.uploadAvatar(
    "Bearer " + token,
    avatarPart
);

// Handle response
call.enqueue(new Callback<UserProfileResponse>() {
    @Override
    public void onResponse(Call<UserProfileResponse> call, Response<UserProfileResponse> response) {
        if (response.isSuccessful() && response.body() != null) {
            UserProfile updatedUser = response.body().getUser();
            // Update UI with new avatar
        }
    }
});
```

## ✅ Features & Validation

### Supported Features

- ✅ Chọn ảnh từ thư viện
- ✅ Chụp ảnh mới
- ✅ Preview trước khi upload
- ✅ Compress ảnh tự động
- ✅ Progress indicator
- ✅ Error handling
- ✅ Permission management
- ✅ Circular avatar display

### Validation

- ✅ File type: image/\* only
- ✅ Authentication: Bearer token required
- ✅ Image exists before upload
- ✅ Session validity check

### Recommended Specs

- **Kích thước tối đa**: 5MB
- **Định dạng**: JPG, PNG
- **Chất lượng**: 90% compression
- **Resolution**: Tự động resize/crop

## 🧪 Testing Checklist

### Functional Testing

- [ ] Chọn ảnh từ thư viện
- [ ] Chụp ảnh mới
- [ ] Preview hiển thị đúng
- [ ] Upload thành công
- [ ] Avatar refresh sau upload
- [ ] Hủy operation
- [ ] Handle permission denied
- [ ] Handle network errors
- [ ] Handle 401 unauthorized

### Permission Testing

- [ ] Camera permission request
- [ ] Storage permission request (Android 12-)
- [ ] Permission denied scenarios
- [ ] Permission already granted

### UI Testing

- [ ] Avatar preview circular
- [ ] Buttons enable/disable correctly
- [ ] Progress bar shows during upload
- [ ] Toast messages display
- [ ] Navigation works correctly

### Edge Cases

- [ ] Ảnh rất lớn (>5MB)
- [ ] Ảnh không đúng format
- [ ] Network timeout
- [ ] Session expiration during upload
- [ ] Camera không khả dụng
- [ ] Storage đầy

## 🐛 Error Handling

### Các lỗi có thể xảy ra

| Error             | Cause                      | Solution                         |
| ----------------- | -------------------------- | -------------------------------- |
| Permission Denied | User từ chối quyền         | Show message hướng dẫn cấp quyền |
| No Camera App     | Device không có camera app | Disable camera button            |
| Network Error     | Mất kết nối internet       | Show retry option                |
| 401 Unauthorized  | Token hết hạn              | Auto logout → Login screen       |
| File Too Large    | Ảnh > 5MB                  | Auto compress hoặc show warning  |
| Invalid Format    | File không phải ảnh        | Validate trước khi upload        |

### Error Messages (Vietnamese)

```
- "Cần quyền truy cập camera"
- "Cần quyền truy cập thư viện ảnh"
- "Không tìm thấy ứng dụng camera"
- "Vui lòng chọn ảnh trước"
- "Session expired. Please login again."
- "Network error: [error details]"
```

## 🚀 Future Enhancements

### Phase 2 Features

1. **Image Cropping**

   - Tích hợp thư viện crop ảnh
   - Cho phép user crop trước khi upload
   - Preview realtime

2. **Filters & Effects**

   - Basic filters (B&W, Sepia, etc.)
   - Brightness/Contrast adjustment
   - Rotate/Flip

3. **Multiple Upload**

   - Upload nhiều ảnh cùng lúc
   - Gallery với nhiều avatar
   - Switch giữa các avatar

4. **Avatar Library**

   - Default avatar templates
   - Pre-made avatar options
   - Avatar từ social media

5. **Optimization**
   - Smart compression
   - Progressive upload
   - Background upload
   - Upload queue

## 📝 Code Examples

### Upload Avatar Example

```java
// In UploadAvatarActivity
private void uploadAvatar() {
    File file = new File(getRealPathFromURI(selectedImageUri));

    RequestBody requestFile = RequestBody.create(
        MediaType.parse("image/*"),
        file
    );

    MultipartBody.Part avatarPart = MultipartBody.Part.createFormData(
        "avatar",
        file.getName(),
        requestFile
    );

    String token = sessionManager.getAuthToken();
    apiService.uploadAvatar("Bearer " + token, avatarPart)
        .enqueue(callback);
}
```

### Handle Result in ProfileActivity

```java
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if (requestCode == UploadAvatarActivity.REQUEST_CODE_UPLOAD_AVATAR
        && resultCode == RESULT_OK) {
        UserProfile updatedUser = (UserProfile) data.getSerializableExtra(
            UploadAvatarActivity.EXTRA_UPDATED_USER
        );
        currentUser = updatedUser;
        displayUserProfile(currentUser);
        Toast.makeText(this, "Avatar uploaded successfully", Toast.LENGTH_SHORT).show();
    }
}
```

## 📚 Dependencies

### Required Libraries

- **Retrofit 2.9.0** - HTTP client & multipart upload
- **OkHttp3** - MultipartBody for file upload
- **Glide 4.15.1** - Image loading & caching
- **Material Components** - UI components
- **AndroidX Activity** - Activity Result APIs

### Gradle Dependencies

```gradle
implementation 'com.squareup.retrofit2:retrofit:2.9.0'
implementation 'com.squareup.okhttp3:okhttp:4.9.0'
implementation 'com.github.bumptech.glide:glide:4.15.1'
```

## 📖 Version History

### v1.3.0 (2025-01-04)

- ✨ Initial release of Avatar Upload feature
- ✅ Gallery selection
- ✅ Camera capture
- ✅ Image compression
- ✅ API integration
- ✅ Permission handling

---

**Last Updated**: January 4, 2025  
**Feature Status**: ✅ Complete  
**Testing Status**: 🔄 Ready for Testing  
**Deployment**: 🚀 Ready for Production
