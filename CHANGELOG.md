# � CHANGELOG

## [1.2.0] - 2025-01-04

### ✨ Added - Profile Update Feature

#### New Features

- ✅ **Profile Editing**: Users can now edit their profile information
- ✅ **Field Validation**: Real-time validation for username, full name, and phone
- ✅ **API Integration**: PUT /api/user/profile endpoint integrated
- ✅ **Auto-refresh**: Profile screen automatically updates after changes
- ✅ **Error Handling**: Comprehensive error handling with user-friendly messages

#### New Files

1. **Models**

   - `UpdateProfileRequest.java` - API request model for profile updates

2. **Activities**

   - `EditProfileActivity.java` - Profile editing screen with form validation

3. **Layouts**

   - `activity_edit_profile.xml` - Material Design 3 edit profile form

4. **Drawables**

   - `ic_back.xml` - Back arrow icon for toolbar

5. **Documentation**
   - `PROFILE_UPDATE_README.md` - Complete feature documentation
   - `PROFILE_UPDATE_SUMMARY.md` - Implementation summary
   - `EDIT_PROFILE_QUICK_START.md` - Quick start guide

#### Modified Files

1. **ApiService.java**

   - Added `updateUserProfile()` method with PUT request
   - Added import for UpdateProfileRequest

2. **UserProfile.java**

   - Implemented `Serializable` interface
   - Added `serialVersionUID` for Intent passing

3. **ProfileActivity.java**

   - Updated "Edit Profile" button to navigate to EditProfileActivity
   - Added `onActivityResult()` to handle profile updates

4. **AndroidManifest.xml**
   - Registered EditProfileActivity
   - Set `windowSoftInputMode="adjustResize"`

#### Editable Fields

- ✅ Username (Tên đăng nhập)
- ✅ Full Name (Họ và tên)
- ✅ Phone (Số điện thoại) - with 9-10 digit validation
- ✅ Address (Địa chỉ) - multi-line, optional

#### Read-Only Fields

- 🔒 Email (cannot be changed)
- 🔒 Avatar (upload feature coming soon)

#### API Details

- **Endpoint**: `PUT /api/user/profile`
- **Authentication**: Bearer Token required
- **Request**: JSON with username, fullName, phone, address
- **Response**: Updated user profile object

---

## [1.1.0] - 2025-11-04

### 🔄 Changed - Remove Logout from Home Screen

```xml
<menu xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto">
    <item
        android:id="@+id/action_logout"
        android:title="Đăng xuất"
        app:showAsAction="always" />
</menu>
```

**After:**

```xml
<menu xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto">
    <!-- Logout moved to Profile screen -->
</menu>
```

**Changes:**

- ❌ Removed logout menu item
- ✅ Added comment explaining the change

---

### 2. `HomeActivity.java`

**Location**: `app/src/main/java/com/example/prm392_evcare/HomeActivity.java`

**Before:**

```java
@Override
public boolean onOptionsItemSelected(@NonNull MenuItem item) {
    if (item.getItemId() == R.id.action_logout) {
        logout();
        return true;
    } else if (item.getItemId() == android.R.id.home) {
        finish();
        return true;
    }
    return super.onOptionsItemSelected(item);
}

private void logout() {
    sessionManager.clearSession();
    navigateToLogin();
}
```

**After:**

```java
@Override
public boolean onOptionsItemSelected(@NonNull MenuItem item) {
    if (item.getItemId() == android.R.id.home) {
        finish();
        return true;
    }
    return super.onOptionsItemSelected(item);
}

// Logout method removed - use Profile screen for logout
```

**Changes:**

- ❌ Removed `action_logout` handling in `onOptionsItemSelected()`
- ❌ Removed `logout()` method
- ✅ Simplified menu handling
- ✅ Added comment for clarity
- ✅ Kept `navigateToLogin()` for potential future use

---

### 3. `MainActivity.java`

**Location**: `app/src/main/java/com/example/prm392_evcare/MainActivity.java`

**Before:**

```java
@Override
public boolean onOptionsItemSelected(@NonNull MenuItem item) {
    if (item.getItemId() == R.id.action_logout) {
        logout();
        return true;
    }
    return super.onOptionsItemSelected(item);
}

private void logout() {
    sessionManager.clearSession();
    navigateToLogin();
}
```

**After:**

```java
@Override
public boolean onOptionsItemSelected(@NonNull MenuItem item) {
    // Logout moved to Profile screen
    return super.onOptionsItemSelected(item);
}
```

**Changes:**

- ❌ Removed `action_logout` handling in `onOptionsItemSelected()`
- ❌ Removed `logout()` method
- ✅ Simplified menu handling
- ✅ Added comment for clarity
- ✅ Kept `navigateToLogin()` for potential future use

---

## 🎯 Kết quả

### Before (Trước)

```
Home Screen Toolbar:
┌──────────────────────────┐
│ ← EVCare    [Đăng xuất] │
│                          │
│   Service Centers...     │
└──────────────────────────┘
```

### After (Sau)

```
Home Screen Toolbar:
┌──────────────────────────┐
│ ← EVCare                 │
│                          │
│   Service Centers...     │
└──────────────────────────┘
```

## ✨ Lợi ích

1. **Better UX**: Logout nằm tập trung ở Profile screen
2. **Cleaner UI**: Home screen toolbar gọn gàng hơn
3. **Consistent Design**: Follow best practices - account actions ở Profile
4. **Less Accidents**: Giảm nguy cơ logout nhầm

## 📱 Logout functionality

### Hiện tại logout ở đâu?

✅ **Profile Screen** - Trang Tài khoản

```
Profile Screen:
┌──────────────────────────┐
│   Avatar & Info          │
├──────────────────────────┤
│ [Edit Profile]           │
│ [Change Password]        │
│ [🚪 Logout] ← Ở đây      │
└──────────────────────────┘
```

### Flow logout:

```
Home → Profile (Bottom Nav) → Logout Button → Confirmation Dialog → Login Screen
```

## 🧪 Testing

### Test Cases

- [x] Home screen không có nút đăng xuất
- [x] MainActivity không có nút đăng xuất
- [x] Toolbar menu trống (hoặc chỉ có các action khác)
- [x] Profile screen vẫn có nút logout
- [x] Logout từ Profile hoạt động bình thường
- [x] No compile errors
- [x] App builds successfully

## 📊 Impact

### Files Modified: 3

- `main_menu.xml` - Removed logout item
- `HomeActivity.java` - Removed logout handling
- `MainActivity.java` - Removed logout handling

### Lines Changed: ~25 lines

- Removed: ~18 lines
- Added: ~7 lines (comments)

### Breaking Changes: None

- Logout functionality still available in Profile
- No API changes
- No data model changes

## 🔄 Rollback (nếu cần)

Nếu muốn khôi phục nút logout ở Home:

```xml
<!-- main_menu.xml -->
<item
    android:id="@+id/action_logout"
    android:title="Đăng xuất"
    app:showAsAction="always" />
```

```java
// HomeActivity.java
@Override
public boolean onOptionsItemSelected(@NonNull MenuItem item) {
    if (item.getItemId() == R.id.action_logout) {
        logout();
        return true;
    }
    // ...
}

private void logout() {
    sessionManager.clearSession();
    navigateToLogin();
}
```

## ✅ Status

**Status**: ✅ COMPLETED
**Tested**: ✅ YES
**Errors**: ❌ NONE
**Ready**: ✅ YES

---

**Note**: Logout functionality vẫn hoạt động đầy đủ ở Profile screen với confirmation dialog.
