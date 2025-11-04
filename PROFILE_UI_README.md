# 👤 Profile Screen UI - EVCare

## 🎯 Tổng quan

Trang Profile hiển thị đầy đủ thông tin tài khoản người dùng từ API `/api/user/profile`.

## ✅ Tính năng đã hoàn thành

### 1. UI Components

#### Header Section (Collapsing Toolbar)

- ✅ Gradient background đẹp mắt
- ✅ Avatar tròn với border trắng
- ✅ Verified badge (dấu tick xanh) cho tài khoản đã xác thực
- ✅ Edit avatar FAB button
- ✅ Full name hiển thị lớn, đậm
- ✅ Role chip (Customer/Admin/Technician)
- ✅ Collapsing effect khi scroll

#### Personal Information Card

- ✅ Username với icon
- ✅ Email với icon
- ✅ Phone number với icon
- ✅ Address với icon
- ✅ Modern card design với divider

#### Account Actions Card

- ✅ Edit Profile button (outlined)
- ✅ Change Password button (outlined)
- ✅ Logout button (filled, red)
- ✅ Logout confirmation dialog

#### Account Info Card

- ✅ Member Since date (formatted)
- ✅ User ID (shortened display)

### 2. Data Integration

- ✅ API endpoint integration
- ✅ Bearer token authentication
- ✅ Model mapping (UserProfile, UserProfileResponse)
- ✅ Avatar loading với Glide
- ✅ Date formatting
- ✅ Role display name mapping
- ✅ Error handling

### 3. Navigation

- ✅ Bottom navigation integration
- ✅ Navigate from Home to Profile
- ✅ Navigate back to Home
- ✅ Toolbar back button

## 📱 Cấu trúc Files

```
app/src/main/
├── java/com/example/prm392_evcare/
│   ├── ProfileActivity.java (✅ New)
│   ├── HomeActivity.java (✅ Updated - added navigation)
│   ├── api/
│   │   └── ApiService.java (✅ Updated - added getUserProfile)
│   └── models/
│       ├── UserProfile.java (✅ New)
│       └── UserProfileResponse.java (✅ New)
└── res/
    ├── layout/
    │   └── activity_profile.xml (✅ New)
    └── AndroidManifest.xml (✅ Updated - registered ProfileActivity)
```

## 🎨 Design Specifications

### Layout Structure

```
┌──────────────────────────────┐
│  ┌────────────────────────┐  │ ← Collapsing Header
│  │   Gradient Background  │  │   280dp height
│  │                        │  │
│  │     ┌─────────┐       │  │
│  │     │ Avatar  │       │  │   120dp circle
│  │     │ 🔵 FAB  │       │  │
│  │     └─────────┘       │  │
│  │                        │  │
│  │     Full Name          │  │   24sp Bold White
│  │   [Customer Chip]      │  │
│  └────────────────────────┘  │
├──────────────────────────────┤
│ ┌──────────────────────────┐ │ ← Personal Info Card
│ │ 👤 Username              │ │
│ │ ✉️  Email                │ │
│ │ 📞 Phone                 │ │
│ │ 📍 Address               │ │
│ └──────────────────────────┘ │
├──────────────────────────────┤
│ ┌──────────────────────────┐ │ ← Actions Card
│ │ [Edit Profile]           │ │
│ │ [Change Password]        │ │
│ │ [🚪 Logout]              │ │
│ └──────────────────────────┘ │
├──────────────────────────────┤
│ ┌──────────────────────────┐ │ ← Account Info Card
│ │ Member Since: 16/09/2025 │ │
│ │ User ID: 68c9376c...     │ │
│ └──────────────────────────┘ │
└──────────────────────────────┘
```

### Color Scheme

- **Header Background**: Gradient (#6366F1 → #8B5CF6 → #EC4899)
- **Avatar Border**: White (4dp)
- **Verified Badge**: Green (#4CAF50)
- **Role Chip**: Semi-transparent white
- **Card Background**: White
- **Edit/Change Password Button**: Primary outline
- **Logout Button**: Red (#F44336)

### Typography

- **Full Name**: 24sp, Bold, White
- **Role Chip**: 14sp, White
- **Section Title**: 18sp, Bold, #212121
- **Field Label**: 12sp, #757575
- **Field Value**: 16sp, #212121

## 📊 API Integration

### Endpoint

```
GET /api/user/profile
Authorization: Bearer {token}
```

### Response → UI Mapping

```json
{
  "user": {
    "id": "..." → User ID (shortened)
    "username": "..." → Username field
    "email": "..." → Email field
    "fullName": "..." → Header name + Username if null
    "phone": "..." → Phone field
    "address": "..." → Address field
    "role": "..." → Role chip (mapped to display name)
    "avatar": "..." → Avatar image (Glide)
    "isVerified": true → Verified badge (show/hide)
    "createdAt": "..." → Member Since (formatted dd/MM/yyyy)
  }
}
```

### Role Display Mapping

```java
customer → Customer
admin → Administrator
technician → Technician
staff → Staff
```

## 🔧 Features

### 1. Avatar Display

- Circular crop with Glide
- 120dp size
- White 4dp border
- Edit FAB overlay
- Placeholder if no avatar

### 2. Verified Badge

- Green check icon
- Only visible if `isVerified: true`
- Positioned at top-right of avatar

### 3. Collapsing Toolbar

- Parallax scrolling effect
- Collapses on scroll up
- Expands on scroll down
- Toolbar pins at top when collapsed

### 4. Date Formatting

- Input: `2025-09-16T10:09:48.518Z`
- Output: `16/09/2025`
- SimpleDateFormat parsing

### 5. Logout Flow

```
1. Click Logout button
2. Show confirmation dialog
3. If confirmed:
   - Clear session
   - Navigate to Login
   - Clear back stack
```

## 🚀 Usage

### From Home Screen

```java
// HomeActivity.java
bottomNavigation.setOnItemSelectedListener(item -> {
    if (itemId == R.id.nav_profile) {
        navigateToProfile();
        return true;
    }
});

private void navigateToProfile() {
    Intent intent = new Intent(this, ProfileActivity.class);
    startActivity(intent);
}
```

### Load Profile Data

```java
// ProfileActivity.java
private void loadUserProfile() {
    String token = sessionManager.getToken();
    apiService.getUserProfile("Bearer " + token)
        .enqueue(new Callback<UserProfileResponse>() {
            @Override
            public void onResponse(Call call, Response response) {
                if (response.isSuccessful()) {
                    displayUserProfile(response.body().getUser());
                }
            }
        });
}
```

## ⚙️ Configuration

### Glide Dependencies

Đã có trong `build.gradle.kts`:

```kotlin
implementation("com.github.bumptech.glide:glide:4.15.1")
```

### Permissions

Đã có trong `AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.INTERNET" />
```

## 🐛 Error Handling

### 1. Network Error

- Show Toast với error message
- Keep loading state visible

### 2. 401 Unauthorized

- Navigate to Login screen
- Clear session

### 3. Missing Avatar

- Show default placeholder icon
- No crash

### 4. Null Fields

- Phone: Show "Not provided"
- Address: Show "Not provided"
- Avatar: Show placeholder

## 📝 Future Enhancements

### Planned Features

- [ ] Edit profile functionality
- [ ] Change password functionality
- [ ] Upload/change avatar
- [ ] View booking history
- [ ] View favorite service centers
- [ ] Account statistics
- [ ] Notification settings
- [ ] App preferences

### UI Improvements

- [ ] Skeleton loading animation
- [ ] Pull-to-refresh
- [ ] Smooth transitions
- [ ] Avatar zoom preview
- [ ] Share profile option
- [ ] QR code for profile

## 🎯 Testing Checklist

### UI Tests

- [ ] Avatar loads correctly
- [ ] Verified badge shows when isVerified=true
- [ ] All fields display correct data
- [ ] Collapsing toolbar works smoothly
- [ ] Bottom navigation highlights correct item
- [ ] Logout dialog appears
- [ ] Logout clears session and navigates

### API Tests

- [ ] Profile loads on screen open
- [ ] Loading indicator shows/hides
- [ ] Error handling for network failure
- [ ] Error handling for 401
- [ ] Token sent in Authorization header

### Navigation Tests

- [ ] Navigate from Home to Profile
- [ ] Navigate from Profile to Home
- [ ] Back button works
- [ ] Bottom nav switches correctly

## 📸 Screenshots

(Add screenshots when running app)

## 👥 Contributors

- Profile UI Design: November 4, 2025

## 📄 License

EVCare - Electric Vehicle Care Management System
