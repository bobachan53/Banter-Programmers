package com.miste.one.startup;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.miste.one.HomeActivity;
import com.miste.one.R;
import com.miste.one.SplashActivity;
import com.miste.one.utils.App;

public class LoginActivity extends AppCompatActivity {


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
    private Handler mHandler= new Handler();


    private AppBarLayout ihoioi;
    private RelativeLayout toolbar;
//    private ImageView backButton;
    private EditText nameEdittext;
    private EditText phoneNoEdittext;
    private Button continueButton;


    private FirebaseAuth mAuth;


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
        setContentView(R.layout.activity_login);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        final Window window =  getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.white));


        mAuth = FirebaseAuth.getInstance();

        ihoioi = (AppBarLayout) findViewById(R.id.ihoioi);
        toolbar = (RelativeLayout) findViewById(R.id.toolbar);
//        backButton = (ImageView) findViewById(R.id.backButton);
        nameEdittext = (EditText) findViewById(R.id.nameEdittext);
        phoneNoEdittext = (EditText) findViewById(R.id.phoneNoEdittext);
        continueButton = (Button) findViewById(R.id.continueButton);


        mHandler.post(
                new Runnable() {
                    public void run() {
                        InputMethodManager inputMethodManager =  (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                        inputMethodManager.toggleSoftInputFromWindow(nameEdittext.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);
                        nameEdittext.requestFocus();
                    }
                });

       /* continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                App.preventMultipleClick(continueButton);

                String name = nameEdittext.getText().toString().trim();
                String number = phoneNoEdittext.getText().toString().trim();

                if(name.isEmpty() || number.isEmpty())
                {
                    Toast.makeText(LoginActivity.this, "Please fill all the fields.", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent(LoginActivity.this, OtpVerificationActivity.class);
                intent.putExtra("name",name);
                intent.putExtra("number",number);
                startActivity(intent);
//                finish();
            }
        });*/

        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() != null) {
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        }
    }
}