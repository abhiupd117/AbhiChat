package com.amandeep.abhichat.AppUtils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class MyAppPref {
    public static final String KEY_USER_ID = "userId";
    public static final String KEY_STATIC_ACCESS_TOKEN = "staticToken";
    public static final String KEY_ACCESS_TOKEN = "accessToken";
    public static final String KEY_USER_TYPE = "userType";
    public static final String KEY_USER_NAME = "userName";
    public static final String KEY_USER_MOB = "userMob";
    public static final String KEY_USER_EMAIL = "userEmail";
    public static final String KEY_USER_PROFILE = "userImageUrl";
    public static final String KEY_USER_ADDRESS = "userAddress";
    public static final String KEY_FCM_TOKEN = "fcmToken";
    public static final String KEY_QB_REGISTERED = "isQBRegistered";
    public static final String KEY_QB_USERID = "qbUserId";

    private Context context;
    private final SharedPreferences sharedPreferences;

    public MyAppPref(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences("utterNow", Context.MODE_PRIVATE);
    }

    @SuppressLint("LongLogTag")
    public void setAccessToken(String accessToken) {
        Log.e("STr ACcess token in SHared Pref ********************", accessToken);
        sharedPreferences.edit().putString(KEY_ACCESS_TOKEN, accessToken).apply();
    }

    public String getAccessToken() {
        return sharedPreferences.getString(KEY_ACCESS_TOKEN, null);
    }

    @SuppressLint("LongLogTag")
    public void setUserId(int userId) {
        Log.e("STr userID in SHared Pref ********************", userId + "");

        sharedPreferences.edit().putInt(KEY_USER_ID, userId).apply();
    }

    public int getUserId() {
        return sharedPreferences.getInt(KEY_USER_ID, 0);
    }

    public void setUserType(String userType) {
        sharedPreferences.edit().putString(KEY_USER_TYPE, userType).apply();
    }

    public String getUserType() {
        return sharedPreferences.getString(KEY_USER_TYPE, null);
    }

    public void setUserName(String userName) {
        sharedPreferences.edit().putString(KEY_USER_NAME, userName).apply();
    }

    public String getUserName() {
        return sharedPreferences.getString(KEY_USER_NAME, null);
    }

    public void clearMyPref() {
        sharedPreferences.edit().clear().apply();
    }

    public String getMobile() {
        return sharedPreferences.getString(KEY_USER_MOB, null);
    }

    public void setMobile(String mobile) {
        sharedPreferences.edit().putString(KEY_USER_MOB, mobile).apply();
    }

    public String getEmail() {
        return sharedPreferences.getString(KEY_USER_EMAIL, null);
    }

    public void setEmail(String email) {
        sharedPreferences.edit().putString(KEY_USER_EMAIL, email).apply();
    }

    public String getImageUrl() {
        return sharedPreferences.getString(KEY_USER_PROFILE, "Na");
    }

    public void setImageUrl(String url) {
        sharedPreferences.edit().putString(KEY_USER_PROFILE, url).apply();
    }

    public String getAddress() {
        return sharedPreferences.getString(KEY_USER_ADDRESS, "NA");
    }

    public void setAddress(String address) {
        sharedPreferences.edit().putString(KEY_USER_ADDRESS, address).apply();
    }

    public void setIsQBRegistered(boolean isQBRegistered) {
        sharedPreferences.edit().putBoolean(KEY_QB_REGISTERED, isQBRegistered).apply();
    }

    public boolean isQBRegistered() {
        return sharedPreferences.getBoolean(KEY_QB_REGISTERED, false);
    }

    public void setQBUserId(int qbUserId) {
        sharedPreferences.edit().putInt(KEY_QB_USERID, qbUserId).apply();
    }

    public int getQBUserId() {
        return sharedPreferences.getInt(KEY_QB_USERID, 0);
    }
}
