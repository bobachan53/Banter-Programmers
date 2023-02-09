package com.miste.one.utils;


import android.app.Activity;
import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.os.Handler;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.miste.one.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Locale;


public class App extends Application {


    private static App instance;
    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;

//        SharedPref.initPref(instance);


    }
    public static void preventMultipleClick(final View view) {
        view.setEnabled(false);
        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                view.setEnabled(true);
            }
        }, 1000);
    }

    public static synchronized App getInstance() {
        return instance;
    }

    public static void makeToast(String message) {
        Toast.makeText(instance.getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    public static void makeCustomToast(String message) {
        Toast toast = Toast.makeText(instance, message, Toast.LENGTH_SHORT);
        toast.getView()
                .setBackgroundTintList(ColorStateList.valueOf(instance.getResources().getColor(R.color.black)));
        ((TextView) toast.getView().findViewById(android.R.id.message))
                .setTextColor(instance.getResources().getColor(R.color.colorWhite));
        toast.show();
    }



}
