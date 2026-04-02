package com.app.myapplication.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Manages user session (JWT token) using SharedPreferences.
 * Call SessionManager.init(context) once in Application or MainActivity.
 */
public class SessionManager {
    private static final String PREF_NAME = "VelocitySession";
    private static final String KEY_TOKEN = "auth_token";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_USER_PHONE = "user_phone";
    private static final String KEY_ROLE = "user_role";

    private static SharedPreferences prefs;
    private static SharedPreferences.Editor editor;

    public static void init(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public static void saveSession(String token, String userId, String role,
                                   String fullName, String phone) {
        editor.putString(KEY_TOKEN, token);
        editor.putString(KEY_USER_ID, userId != null ? userId : "");
        editor.putString(KEY_ROLE, role);
        editor.putString(KEY_USER_NAME, fullName);
        editor.putString(KEY_USER_PHONE, phone);
        editor.apply();
    }

    public static String getToken() {
        return prefs != null ? prefs.getString(KEY_TOKEN, null) : null;
    }

    public static String getUserId() {
        return prefs != null ? prefs.getString(KEY_USER_ID, "") : "";
    }

    public static String getRole() {
        return prefs != null ? prefs.getString(KEY_ROLE, "") : "";
    }

    public static String getUserName() {
        return prefs != null ? prefs.getString(KEY_USER_NAME, "") : "";
    }

    public static String getUserPhone() {
        return prefs != null ? prefs.getString(KEY_USER_PHONE, "") : "";
    }

    public static boolean isLoggedIn() {
        String token = getToken();
        return token != null && !token.isEmpty();
    }

    public static boolean isDriver() {
        return "DRIVER".equalsIgnoreCase(getRole());
    }

    public static void clearSession() {
        if (editor != null) {
            editor.clear();
            editor.apply();
        }
    }
}
