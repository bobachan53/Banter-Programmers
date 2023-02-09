package com.miste.one.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class DeviceTokenPref {
    private static final String SHARED_PREF_NAME = "TokenPref";
    private static final String TAG_DEVICE_TOKEN = "device_token";

    private static DeviceTokenPref mInstance;
    private static Context mCtx;

    private DeviceTokenPref(Context context) {
        mCtx = context;
    }

    public static synchronized DeviceTokenPref getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new DeviceTokenPref(context);
        }
        return mInstance;
    }

    //this method will save the device token to shared preferences
    public boolean saveDeviceToken(String token) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(TAG_DEVICE_TOKEN, token);
        editor.apply();
        return true;
    }

    //this method will fetch the device token from shared preferences
    public String getDeviceToken() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(TAG_DEVICE_TOKEN, null);
    }
}
