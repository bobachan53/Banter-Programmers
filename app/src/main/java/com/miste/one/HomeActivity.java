package com.miste.one;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CALL_PHONE;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.GET_ACCOUNTS;
import static android.Manifest.permission.INTERNET;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.miste.one.fragments.HomeFragment;
import com.miste.one.fragments.ProfileFragment;
import com.miste.one.fragments.StudioFragment;

public class HomeActivity extends AppCompatActivity {


    @TargetApi(Build.VERSION_CODES.N)
    private static final int[] ORDERED_DENSITY_DP_N = {
            DisplayMetrics.DENSITY_LOW,
            DisplayMetrics.DENSITY_MEDIUM,
            DisplayMetrics.DENSITY_TV,
            DisplayMetrics.DENSITY_HIGH,
            DisplayMetrics.DENSITY_280,
            DisplayMetrics.DENSITY_XHIGH,
            DisplayMetrics.DENSITY_360,
            DisplayMetrics.DENSITY_400,
            DisplayMetrics.DENSITY_420,
            DisplayMetrics.DENSITY_XXHIGH,
            DisplayMetrics.DENSITY_560,
            DisplayMetrics.DENSITY_XXXHIGH
    };
// resolution change

    @TargetApi(Build.VERSION_CODES.N_MR1)
    private static final int[] ORDERED_DENSITY_DP_N_MR1 = {
            DisplayMetrics.DENSITY_LOW,
            DisplayMetrics.DENSITY_MEDIUM,
            DisplayMetrics.DENSITY_TV,
            DisplayMetrics.DENSITY_HIGH,
            DisplayMetrics.DENSITY_260,
            DisplayMetrics.DENSITY_280,
            DisplayMetrics.DENSITY_XHIGH,
            DisplayMetrics.DENSITY_340,
            DisplayMetrics.DENSITY_360,
            DisplayMetrics.DENSITY_400,
            DisplayMetrics.DENSITY_420,
            DisplayMetrics.DENSITY_XXHIGH,
            DisplayMetrics.DENSITY_560,
            DisplayMetrics.DENSITY_XXXHIGH
    };

    @TargetApi(Build.VERSION_CODES.P)
    private static final int[] ORDERED_DENSITY_DP_P = {
            DisplayMetrics.DENSITY_LOW,
            DisplayMetrics.DENSITY_MEDIUM,
            DisplayMetrics.DENSITY_TV,
            DisplayMetrics.DENSITY_HIGH,
            DisplayMetrics.DENSITY_260,
            DisplayMetrics.DENSITY_280,
            DisplayMetrics.DENSITY_XHIGH,
            DisplayMetrics.DENSITY_340,
            DisplayMetrics.DENSITY_360,
            DisplayMetrics.DENSITY_400,
            DisplayMetrics.DENSITY_420,
            DisplayMetrics.DENSITY_440,
            DisplayMetrics.DENSITY_XXHIGH,
            DisplayMetrics.DENSITY_560,
            DisplayMetrics.DENSITY_XXXHIGH
    };


    @Override
    protected void attachBaseContext(final Context baseContext) {

        Context newContext = baseContext;

        // Screen zoom is supported from API 24+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

            Resources resources = baseContext.getResources();
            DisplayMetrics displayMetrics = resources.getDisplayMetrics();
            Configuration configuration = resources.getConfiguration();

            Log.v("TESTS", "attachBaseContext: currentDensityDp: " + configuration.densityDpi
                    + " widthPixels: " + displayMetrics.widthPixels + " deviceDefault: " + DisplayMetrics.DENSITY_DEVICE_STABLE);

            if (displayMetrics.densityDpi != DisplayMetrics.DENSITY_DEVICE_STABLE) {
                // display_size_forced exists for Samsung Devices that allow user to change screen resolution
                // (screen resolution != screen zoom.. HD, FHD, WQDH etc)
                // This check can be omitted.. It seems this code works even if the device supports screen zoom only
                if (Settings.Global.getString(baseContext.getContentResolver(), "display_size_forced") != null) {
                    Log.v("TESTS", "attachBaseContext: This device supports screen resolution changes");

                    // density is densityDp / 160
                    float defaultDensity = (DisplayMetrics.DENSITY_DEVICE_STABLE / (float) DisplayMetrics.DENSITY_DEFAULT);
                    float defaultScreenWidthDp = displayMetrics.widthPixels / defaultDensity;
                    Log.v("TESTS", "attachBaseContext: defaultDensity: " + defaultDensity + " defaultScreenWidthDp: " + defaultScreenWidthDp);
                    configuration.densityDpi = findDensityDpCanFitScreen((int) defaultScreenWidthDp);
                } else {
                    // If the device does not allow the user to change the screen resolution, we can
                    // just set the default density
                    configuration.densityDpi = DisplayMetrics.DENSITY_DEVICE_STABLE;
                }
                Log.v("TESTS", "attachBaseContext: result: " + configuration.densityDpi);
                newContext = baseContext.createConfigurationContext(configuration);
            }
        }
        super.attachBaseContext(newContext);
    }

    @TargetApi(Build.VERSION_CODES.N)
    private static int findDensityDpCanFitScreen(final int densityDp) {
        int[] orderedDensityDp;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            orderedDensityDp = ORDERED_DENSITY_DP_P;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            orderedDensityDp = ORDERED_DENSITY_DP_N_MR1;
        } else {
            orderedDensityDp = ORDERED_DENSITY_DP_N;
        }

        int index = 0;
        while (densityDp >= orderedDensityDp[index]) {
            index++;
        }
        return orderedDensityDp[index];
    }



    BottomNavigationView bottomNavigation;

    final Fragment fragment1 = new HomeFragment();
        final Fragment fragment2 = new StudioFragment();
    final Fragment fragment3 = new ProfileFragment();

    Fragment active = fragment1;
    FragmentManager fm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Configuration configuration = getResources().getConfiguration();
        configuration.fontScale = (float) 1; //0.85 small size, 1 normal size, 1,15 big etc
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        metrics.scaledDensity = configuration.fontScale * metrics.density;
        configuration.densityDpi = (int) getResources().getDisplayMetrics().xdpi;
        getBaseContext().getResources().updateConfiguration(configuration, metrics);



        setContentView(R.layout.activity_home);
//        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        final Window window =  getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.logo_bg_color));


        String[] perms = {"android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION", "android.permission.INTERNET", "android.permission.CALL_PHONE", "android.permission.GET_ACCOUNTS", "android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE"};


        int permsRequestCode = 290;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(perms, permsRequestCode);
        }
        if (!checkPermission()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(perms, permsRequestCode);
            }
        }


        fm = getSupportFragmentManager();
        bottomNavigation = findViewById(R.id.bottom_navigation);


        fm.beginTransaction().add(R.id.main_container, fragment3, "3").hide(fragment3).commit();
        fm.beginTransaction().add(R.id.main_container, fragment2, "2").hide(fragment2).commit();
        fm.beginTransaction().add(R.id.main_container,fragment1, "1").commit();


        MenuItem item = bottomNavigation.getMenu().findItem(R.id.navigation_home);
        item.setChecked(true);

        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_studio:
                        fm.beginTransaction().hide(active).show(fragment2).commit();
                        active = fragment2;
                        return true;
                    case R.id.navigation_home:
                        fm.beginTransaction().hide(active).show(fragment1).commit();
                        active = fragment1;
                        return true;

                    case R.id.navigation_profile:
                        fm.beginTransaction().hide(active).show(fragment3).commit();
                        active = fragment3;
                        return true;


                }
                return false;
            }
        });
//        bottomNavigation.getMenu().findItem(R.id.home).setChecked(true);

    }
    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_FINE_LOCATION);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_COARSE_LOCATION);
        int result2 = ContextCompat.checkSelfPermission(getApplicationContext(), INTERNET);
        int result3 = ContextCompat.checkSelfPermission(getApplicationContext(), CALL_PHONE);
        int result4 = ContextCompat.checkSelfPermission(getApplicationContext(), GET_ACCOUNTS);
        int result5 = ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA);
        //int result6 = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);
        int result7 = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED && result2 == PackageManager.PERMISSION_GRANTED && result3 == PackageManager.PERMISSION_GRANTED && result4 == PackageManager.PERMISSION_GRANTED && result5 == PackageManager.PERMISSION_GRANTED && result7 == PackageManager.PERMISSION_GRANTED;
    }
}