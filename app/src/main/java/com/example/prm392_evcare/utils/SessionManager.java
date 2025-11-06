package com.example.prm392_evcare.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.prm392_evcare.models.User;
import com.google.gson.Gson;

public class SessionManager {
    private static final String PREF_NAME = "EVCareSession";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_AUTH_TOKEN = "authToken";
    private static final String KEY_REFRESH_TOKEN = "refreshToken";
    private static final String KEY_USER = "user";
    
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Context context;
    private Gson gson;
    
    public SessionManager(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        gson = new Gson();
    }
    
    public void saveAuthToken(String token) {
        editor.putString(KEY_AUTH_TOKEN, token);
        editor.apply();
    }
    
    public String getAuthToken() {
        return sharedPreferences.getString(KEY_AUTH_TOKEN, null);
    }
    
    public void saveRefreshToken(String token) {
        editor.putString(KEY_REFRESH_TOKEN, token);
        editor.apply();
    }
    
    public String getRefreshToken() {
        return sharedPreferences.getString(KEY_REFRESH_TOKEN, null);
    }
    
    public void saveUser(User user) {
        android.util.Log.d("SessionManager", "========== SAVING USER ==========");
        android.util.Log.d("SessionManager", "User object: " + (user != null ? "EXISTS" : "NULL"));
        if (user != null) {
            android.util.Log.d("SessionManager", "User ID before save: " + user.getId());
            android.util.Log.d("SessionManager", "User Role: " + user.getRole());
            android.util.Log.d("SessionManager", "User Email: " + user.getEmail());
        }
        
        String userJson = gson.toJson(user);
        android.util.Log.d("SessionManager", "User JSON: " + userJson);
        
        editor.putString(KEY_USER, userJson);
        editor.apply();
        
        // Verify immediately after save
        String savedJson = sharedPreferences.getString(KEY_USER, null);
        android.util.Log.d("SessionManager", "Saved JSON in SharedPrefs: " + savedJson);
        android.util.Log.d("SessionManager", "===================================");
    }
    
    public User getUser() {
        String userJson = sharedPreferences.getString(KEY_USER, null);
        android.util.Log.d("SessionManager", "========== GETTING USER ==========");
        android.util.Log.d("SessionManager", "User JSON from SharedPrefs: " + userJson);
        
        if (userJson != null) {
            User user = gson.fromJson(userJson, User.class);
            android.util.Log.d("SessionManager", "Deserialized User: " + (user != null ? "EXISTS" : "NULL"));
            if (user != null) {
                android.util.Log.d("SessionManager", "User ID after load: " + user.getId());
                android.util.Log.d("SessionManager", "User Role: " + user.getRole());
            }
            android.util.Log.d("SessionManager", "===================================");
            return user;
        }
        
        android.util.Log.d("SessionManager", "User JSON is NULL");
        android.util.Log.d("SessionManager", "===================================");
        return null;
    }
    
    public String getUserId() {
        User user = getUser();
        String userId = user != null ? user.getId() : null;
        android.util.Log.d("SessionManager", "getUserId() returning: " + userId);
        return userId;
    }
    
    public void setLoggedIn(boolean isLoggedIn) {
        editor.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn);
        editor.apply();
    }
    
    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }
    
    public void clearSession() {
        editor.clear();
        editor.apply();
    }
}