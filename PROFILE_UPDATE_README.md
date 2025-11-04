# Profile Update Feature Documentation

## Overview

This document describes the implementation of the profile update feature for the EVCare application. Users can now edit their profile information including username, full name, phone number, and address.

## API Endpoint

### Update Profile

- **URL**: `PUT /api/user/profile`
- **Authentication**: Required (Bearer Token)
- **Request Body**:

```json
{
  "username": "Devbaoo",
  "fullName": "Khac Bao",
  "phone": "0355418118",
  "address": "Tam Phuoc, Long Thanh, Dong Nai, VN"
}
```

- **Response**:

```json
{
  "success": true,
  "message": "Cập nhật profile thành công",
  "user": {
    "id": "68c9376cb7fbfbca01bb1ca2",
    "username": "Devbaoo",
    "email": "devbaoo712@gmail.com",
    "fullName": "Khac Bao",
    "phone": "0355418118",
    "address": "Tam Phuoc, Long Thanh, Dong Nai, VN",
    "role": "customer",
    "avatar": "https://res.cloudinary.com/...",
    "isVerified": true
  }
}
```

## Implementation Details

### 1. New Files Created

#### Models

- **UpdateProfileRequest.java** - Request model for profile updates
  - Fields: username, fullName, phone, address
  - All fields are required

#### Activities

- **EditProfileActivity.java** - Main activity for editing profile
  - Loads current user data
  - Validates user input
  - Sends update request to API
  - Returns updated data to ProfileActivity

#### Layouts

- **activity_edit_profile.xml** - Edit profile screen layout
  - Material Design 3 components
  - Form fields for editable data
  - Read-only fields for email and avatar
  - Save/Cancel action buttons

#### Drawables

- **ic_back.xml** - Back arrow icon for toolbar

### 2. Modified Files

#### ApiService.java

- Added `updateUserProfile()` method with PUT request
- Accepts Authorization header and UpdateProfileRequest body
- Returns UserProfileResponse

#### UserProfile.java

- Implemented `Serializable` interface
- Added `serialVersionUID` for serialization
- Enables passing UserProfile through Intents

#### ProfileActivity.java

- Updated "Edit Profile" button to navigate to EditProfileActivity
- Added `onActivityResult()` to handle updated profile data
- Refreshes UI when profile is updated

#### AndroidManifest.xml

- Registered EditProfileActivity
- Set `windowSoftInputMode="adjustResize"` for keyboard handling

## Features

### Editable Fields

✅ **Username** - Tên đăng nhập
✅ **Full Name** - Họ và tên
✅ **Phone** - Số điện thoại (with validation)
✅ **Address** - Địa chỉ (multi-line)

### Read-Only Fields

🔒 **Email** - Cannot be changed
🔒 **Avatar** - Avatar editing coming soon

### Validation Rules

1. **Username**: Cannot be empty
2. **Full Name**: Cannot be empty
3. **Phone**:
   - Cannot be empty
   - Must be 9-10 digits
   - Only numeric characters
4. **Address**: Optional

### User Flow

1. User taps "Chỉnh sửa" button in ProfileActivity
2. EditProfileActivity opens with pre-filled data
3. User modifies desired fields
4. User taps "Lưu thay đổi" to save or "Hủy" to cancel
5. On save, validation is performed
6. API request is sent with Bearer token
7. On success, updated data is returned to ProfileActivity
8. ProfileActivity refreshes UI with new data
9. Success toast message is shown

## UI/UX Design

### EditProfileActivity Layout

```
┌─────────────────────────────┐
│ ← Chỉnh sửa thông tin       │ (Toolbar)
├─────────────────────────────┤
│                             │
│      [Avatar Image]         │ (120x120dp)
│  Avatar không thể chỉnh sửa │
│                             │
│  ┌─────────────────────┐    │
│  │ 👤 Tên đăng nhập    │    │
│  │ Devbaoo            │    │
│  └─────────────────────┘    │
│                             │
│  ┌─────────────────────┐    │
│  │ 👤 Họ và tên        │    │
│  │ Khac Bao           │    │
│  └─────────────────────┘    │
│                             │
│  ┌─────────────────────┐    │
│  │ ✉️ Email (read-only) │    │
│  │ devbaoo712@...     │    │
│  └─────────────────────┘    │
│                             │
│  ┌─────────────────────┐    │
│  │ 📞 +84 Số điện thoại│    │
│  │ 0355418118         │    │
│  └─────────────────────┘    │
│                             │
│  ┌─────────────────────┐    │
│  │ 📍 Địa chỉ          │    │
│  │ Tam Phuoc,         │    │
│  │ Long Thanh,...     │    │
│  └─────────────────────┘    │
│                             │
│  ┌─────────┐ ┌────────────┐ │
│  │   Hủy   │ │ Lưu thay đổi│ │
│  └─────────┘ └────────────┘ │
└─────────────────────────────┘
```

### Design Specifications

- **Color Scheme**: Material Design 3 with primary color
- **Input Fields**: Outlined TextInputLayout
- **Icons**: Material Icons (person, email, phone, location)
- **Avatar**: Circular 120dp with Glide loading
- **Buttons**:
  - Cancel: Outlined button (secondary action)
  - Save: Filled button with primary color
- **Spacing**: 16dp between fields, 24dp padding
- **Typography**: 16sp for input text, 12sp for hints

## Error Handling

### Validation Errors

- Empty username → "Tên đăng nhập không được để trống"
- Empty full name → "Họ và tên không được để trống"
- Empty phone → "Số điện thoại không được để trống"
- Invalid phone → "Số điện thoại không hợp lệ (9-10 chữ số)"

### Network Errors

- **401 Unauthorized** → Redirects to login screen
- **Network failure** → Shows error toast with message
- **Other errors** → Shows error code

### Loading States

- Progress bar shown during API call
- Save button disabled during request
- Re-enabled after response

## Security

- **Authentication**: Bearer token required for all requests
- **Session Management**: Token validated before API calls
- **Auto-logout**: On 401 error, clears session and redirects to login
- **Data Validation**: Client-side validation before API call

## Testing Checklist

### Functional Testing

- [ ] Load profile data correctly
- [ ] Validate all required fields
- [ ] Validate phone number format
- [ ] Save changes successfully
- [ ] Handle network errors gracefully
- [ ] Handle 401 unauthorized errors
- [ ] Cancel button works correctly
- [ ] Back button works correctly
- [ ] Profile refreshes after successful update

### UI Testing

- [ ] All fields are properly aligned
- [ ] Avatar loads correctly
- [ ] Email field is disabled
- [ ] Phone prefix (+84) is displayed
- [ ] Address field is multi-line
- [ ] Progress bar shows during loading
- [ ] Toast messages are displayed
- [ ] Keyboard doesn't overlap input fields

### Edge Cases

- [ ] Very long address text
- [ ] Special characters in name
- [ ] Empty optional fields
- [ ] Network timeout
- [ ] Session expiration during edit
- [ ] Rapid button clicks

## Future Enhancements

1. **Avatar Upload**: Add camera/gallery selection
2. **Email Change**: With email verification
3. **Phone Verification**: OTP verification for phone changes
4. **Profile Picture Crop**: Image cropping before upload
5. **Auto-save**: Save draft changes locally
6. **Change History**: Track profile change history
7. **Profile Completion**: Progress indicator for profile completion

## Code Examples

### Calling Update API

```java
UpdateProfileRequest request = new UpdateProfileRequest(
    "Devbaoo",
    "Khac Bao",
    "0355418118",
    "Tam Phuoc, Long Thanh, Dong Nai, VN"
);

String token = sessionManager.getAuthToken();
Call<UserProfileResponse> call = apiService.updateUserProfile(
    "Bearer " + token,
    request
);

call.enqueue(new Callback<UserProfileResponse>() {
    @Override
    public void onResponse(Call<UserProfileResponse> call,
                         Response<UserProfileResponse> response) {
        if (response.isSuccessful() && response.body() != null) {
            UserProfile updatedUser = response.body().getUser();
            // Update UI with new data
        }
    }

    @Override
    public void onFailure(Call<UserProfileResponse> call, Throwable t) {
        // Handle error
    }
});
```

### Navigation to Edit Profile

```java
// In ProfileActivity
btnEditProfile.setOnClickListener(v -> {
    Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
    intent.putExtra(EditProfileActivity.EXTRA_USER_PROFILE, currentUser);
    startActivityForResult(intent, EditProfileActivity.REQUEST_CODE_EDIT_PROFILE);
});

// Handle result
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if (requestCode == EditProfileActivity.REQUEST_CODE_EDIT_PROFILE
        && resultCode == RESULT_OK) {
        UserProfile updatedUser = (UserProfile) data.getSerializableExtra(
            EditProfileActivity.EXTRA_USER_PROFILE
        );
        displayUserProfile(updatedUser);
    }
}
```

## Dependencies

- Retrofit 2.9.0 - HTTP client
- Gson - JSON parsing
- Glide 4.15.1 - Image loading
- Material Components - UI components

## Version History

- **v1.0.0** (2025-01-04) - Initial implementation
  - Basic profile editing
  - Validation for all fields
  - API integration
  - Error handling

---

**Last Updated**: January 4, 2025  
**Author**: Development Team  
**Status**: ✅ Completed
