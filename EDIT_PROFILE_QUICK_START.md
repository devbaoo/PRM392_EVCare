# 🚀 Quick Start Guide - Profile Update Feature

## ⚡ Quick Overview

Users can now edit their profile information (username, full name, phone, address) directly from the Profile screen.

## 📱 How to Use (User Perspective)

1. **Open Profile**
   - Tap "Tài khoản" tab in bottom navigation
2. **Edit Profile**
   - Tap "Chỉnh sửa" button (blue button)
3. **Update Information**
   - Edit any of these fields:
     - Tên đăng nhập (Username)
     - Họ và tên (Full Name)
     - Số điện thoại (Phone - 9-10 digits)
     - Địa chỉ (Address - optional)
4. **Save or Cancel**
   - Tap "Lưu thay đổi" to save
   - Tap "Hủy" to discard changes

## 🔧 Implementation (Developer Perspective)

### New API Endpoint

```java
@PUT("/api/user/profile")
Call<UserProfileResponse> updateUserProfile(
    @Header("Authorization") String token,
    @Body UpdateProfileRequest request
);
```

### Quick Code Example

```java
// Create request
UpdateProfileRequest request = new UpdateProfileRequest(
    username, fullName, phone, address
);

// Call API
apiService.updateUserProfile("Bearer " + token, request)
    .enqueue(callback);
```

### Navigation Flow

```java
ProfileActivity → EditProfileActivity → ProfileActivity (refreshed)
```

## 📂 Files Modified/Created

### Created (6 files)

1. `UpdateProfileRequest.java` - API request model
2. `EditProfileActivity.java` - Edit screen activity
3. `activity_edit_profile.xml` - Edit screen layout
4. `ic_back.xml` - Back icon
5. `PROFILE_UPDATE_README.md` - Full documentation
6. `PROFILE_UPDATE_SUMMARY.md` - Implementation summary

### Modified (5 files)

1. `ApiService.java` - Added PUT endpoint
2. `UserProfile.java` - Implemented Serializable
3. `ProfileActivity.java` - Added navigation & result handling
4. `AndroidManifest.xml` - Registered EditProfileActivity
5. `QUICK_START.md` - This file

## ✅ Validation Rules

| Field     | Required     | Format      | Error Message                              |
| --------- | ------------ | ----------- | ------------------------------------------ |
| Username  | ✅ Yes       | Any text    | "Tên đăng nhập không được để trống"        |
| Full Name | ✅ Yes       | Any text    | "Họ và tên không được để trống"            |
| Phone     | ✅ Yes       | 9-10 digits | "Số điện thoại không hợp lệ (9-10 chữ số)" |
| Address   | ❌ No        | Multi-line  | -                                          |
| Email     | 🔒 Read-only | -           | Cannot be changed                          |
| Avatar    | 🔒 Read-only | -           | Coming soon                                |

## 🎨 UI Components

```
EditProfileActivity
├── Toolbar (with back button)
├── Avatar (120x120, circular, read-only)
├── Username Input (editable)
├── Full Name Input (editable)
├── Email Input (disabled)
├── Phone Input (editable, +84 prefix)
├── Address Input (multi-line, editable)
└── Action Buttons
    ├── Hủy (Cancel - outlined)
    └── Lưu thay đổi (Save - filled)
```

## 🔐 Security

- ✅ Bearer token authentication
- ✅ Session validation
- ✅ Auto-logout on 401
- ✅ Client-side validation
- ✅ HTTPS communication

## 🧪 Testing

### Quick Test Steps

1. Login to app
2. Navigate to Profile tab
3. Tap "Chỉnh sửa"
4. Modify username → Save
5. Verify changes in Profile screen
6. Verify API updated successfully

### Test Cases

- ✅ Valid update (all fields correct)
- ✅ Empty username (should show error)
- ✅ Empty full name (should show error)
- ✅ Invalid phone format (should show error)
- ✅ Cancel without saving
- ✅ Network error handling
- ✅ Session timeout (401 error)

## 📊 API Details

**Endpoint**: `PUT /api/user/profile`

**Request Headers**:

```
Authorization: Bearer <token>
Content-Type: application/json
```

**Request Body**:

```json
{
  "username": "Devbaoo",
  "fullName": "Khac Bao",
  "phone": "0355418118",
  "address": "Tam Phuoc, Long Thanh, Dong Nai, VN"
}
```

**Success Response** (200):

```json
{
    "success": true,
    "message": "Cập nhật profile thành công",
    "user": { ... }
}
```

## 🐛 Troubleshooting

### Issue: Changes not saving

- Check internet connection
- Verify Bearer token is valid
- Check for validation errors

### Issue: 401 Unauthorized

- Session expired → Re-login required
- Token cleared automatically

### Issue: Keyboard covers input

- Use `windowSoftInputMode="adjustResize"` (already set)

### Issue: Avatar not loading

- Check Cloudinary URL
- Verify internet connection
- Glide handles caching automatically

## 📚 Related Documentation

- **PROFILE_UPDATE_README.md** - Complete feature documentation
- **PROFILE_UPDATE_SUMMARY.md** - Implementation details
- **PROFILE_UI_README.md** - Profile screen UI documentation

## 🎯 Next Steps

1. **Test the feature** thoroughly
2. **Deploy** to staging/production
3. **Monitor** API calls and errors
4. **Collect** user feedback
5. **Plan** avatar upload feature (Phase 2)

## 💡 Tips

- Phone numbers are stored without country code (+84)
- Email cannot be changed (security measure)
- Avatar upload feature is planned for next release
- All changes are instant (no draft saving yet)

---

**Last Updated**: January 4, 2025  
**Version**: 1.0.0  
**Status**: ✅ Ready for Testing
