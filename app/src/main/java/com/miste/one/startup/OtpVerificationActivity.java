package com.miste.one.startup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.TaskExecutors;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.miste.one.HomeActivity;
import com.miste.one.R;
import com.miste.one.models.ResponseModel;
import com.miste.one.retro.APIClient;
import com.miste.one.retro.APIInterface;
import com.miste.one.utils.App;
import com.miste.one.utils.DeviceTokenPref;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import dmax.dialog.SpotsDialog;
import ir.androidexception.andexalertdialog.AndExAlertDialog;
import ir.androidexception.andexalertdialog.AndExAlertDialogListener;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Part;

public class OtpVerificationActivity extends AppCompatActivity {


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

    private AppBarLayout ihoioi;
    private RelativeLayout toolbar;
    private ImageView backButton;
    private TextView mainHomeTextView;
    private TextView txtMob;
    private TextInputEditText edOtp1;
    private TextInputEditText edOtp2;
    private TextInputEditText edOtp3;
    private TextInputEditText edOtp4;
    private TextInputEditText edOtp5;
    private TextInputEditText edOtp6;
    private TextView btnSend;
    private TextView btnReenter;
    private TextView btnTimer;

    private String verificationId;
    private FirebaseAuth mAuth;


    private AlertDialog alertDialog;

    APIInterface apiInterface;




    String name,number;
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
        setContentView(R.layout.activity_otp_verification);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        final Window window =  getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.white));


        name = getIntent().getStringExtra("name");
        number = getIntent().getStringExtra("number");

        ihoioi = (AppBarLayout) findViewById(R.id.ihoioi);
        toolbar = (RelativeLayout) findViewById(R.id.toolbar);
        backButton = (ImageView) findViewById(R.id.backButton);
        mainHomeTextView = (TextView) findViewById(R.id.main_home_textView);
        txtMob = (TextView) findViewById(R.id.txt_mob);
        edOtp1 = (TextInputEditText) findViewById(R.id.ed_otp1);
        edOtp2 = (TextInputEditText) findViewById(R.id.ed_otp2);
        edOtp3 = (TextInputEditText) findViewById(R.id.ed_otp3);
        edOtp4 = (TextInputEditText) findViewById(R.id.ed_otp4);
        edOtp5 = (TextInputEditText) findViewById(R.id.ed_otp5);
        edOtp6 = (TextInputEditText) findViewById(R.id.ed_otp6);
        btnSend = (TextView) findViewById(R.id.btn_send);
        btnReenter = (TextView) findViewById(R.id.btn_reenter);
        btnTimer = (TextView) findViewById(R.id.btn_timer);

        apiInterface = APIClient.getClient().create(APIInterface.class);

        mAuth = FirebaseAuth.getInstance();
        number = getIntent().getStringExtra("number");
        name = getIntent().getStringExtra("name");
        alertDialog = new SpotsDialog(OtpVerificationActivity.this);

        sendVerificationCode("+91" + number);
        txtMob.setText("We have sent you an SMS on " + "+91" + " " + number + "\n with 6 digit verification code");
        try {
            new CountDownTimer(60000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    long seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished);
                    btnTimer.setText(seconds + " Secound Wait");
                }

                @Override
                public void onFinish() {
                    btnReenter.setVisibility(View.VISIBLE);
                    btnTimer.setVisibility(View.GONE);
                }
            }.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                App.preventMultipleClick(btnSend);
                if (validation()) {
                    verifyCode(edOtp1.getText().toString() + "" + edOtp2.getText().toString() + "" + edOtp3.getText().toString() + "" + edOtp4.getText().toString() + "" + edOtp5.getText().toString() + "" + edOtp6.getText().toString());
                }

            }
        });

        btnReenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                App.preventMultipleClick(btnReenter);

                btnReenter.setVisibility(View.GONE);
                btnTimer.setVisibility(View.VISIBLE);
                try {
                    new CountDownTimer(60000, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            long seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished);
                            btnTimer.setText(seconds + " Secound Wait");
                        }

                        @Override
                        public void onFinish() {
                            btnReenter.setVisibility(View.VISIBLE);
                            btnTimer.setVisibility(View.GONE);
                        }
                    }.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                sendVerificationCode("+91" + number);
            }
        });
    }

    private void sendVerificationCode(String number) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallBack
        );
    }



    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationId = s;
        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
                edOtp1.setText("" + code.substring(0, 1));
                edOtp2.setText("" + code.substring(1, 2));
                edOtp3.setText("" + code.substring(2, 3));
                edOtp4.setText("" + code.substring(3, 4));
                edOtp5.setText("" + code.substring(4, 5));
                edOtp6.setText("" + code.substring(5, 6));
                verifyCode(code);
            }
            else
            {
                App.makeToast("Hurray, You got auto verify by us!");
//                Toast.makeText(LoginActivity.this, "Hurray, You got auto verified by us.", Toast.LENGTH_SHORT).show();
                signInWithCredential(phoneAuthCredential);
            }
        }
        @Override
        public void onVerificationFailed(FirebaseException e) {

            Toast.makeText(OtpVerificationActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }
    };

    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithCredential(credential);
    }


    private void signInWithCredential(PhoneAuthCredential credential) {

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {


                        alertDialog.show();
                        alertDialog.setMessage("Message");
                        alertDialog.setTitle("Registering User..");
                        alertDialog.setCancelable(false);




                        final String token = DeviceTokenPref.getInstance(getApplicationContext()).getDeviceToken();
                        final String deviceId = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);

                        RequestBody name_body= RequestBody.create(MultipartBody.FORM, name);
                        RequestBody number_body= RequestBody.create(MultipartBody.FORM, number);
                        RequestBody points_body= RequestBody.create(MultipartBody.FORM, "0");
                        RequestBody active_status_body= RequestBody.create(MultipartBody.FORM, "1");
                        RequestBody last_login_time_body= RequestBody.create(MultipartBody.FORM, "");
                        RequestBody uid_body= RequestBody.create(MultipartBody.FORM, mAuth.getCurrentUser().getUid());
                        RequestBody status_body= RequestBody.create(MultipartBody.FORM, "1");
                        RequestBody deviceId_body= RequestBody.create(MultipartBody.FORM, deviceId);


                        Call<ResponseModel> call = apiInterface.normal_register(name_body,number_body,points_body,active_status_body,last_login_time_body
                        ,uid_body,status_body,deviceId_body);

                        call.enqueue(new Callback<ResponseModel>() {
                            @Override
                            public void onResponse(@NonNull Call<ResponseModel> call, @NonNull Response<ResponseModel> response) {
                                Log.e("TAG", response.toString());

                                alertDialog.dismiss();

                                ResponseModel responseModel = response.body();

                                Log.e("TAG", responseModel.getMessage());

                                new AndExAlertDialog.Builder(OtpVerificationActivity.this)
                                        .setTitle("MESSAGE")
                                        .setMessage(responseModel.getMessage())
                                        .setPositiveBtnText("Ok")
                                        .setCancelableOnTouchOutside(false)
                                        .OnPositiveClicked(new AndExAlertDialogListener() {
                                            @Override
                                            public void OnClick(String input) {
                                                if(responseModel.isSuccess()) {

                                                    Intent intent = new Intent(OtpVerificationActivity.this, HomeActivity.class);
                                                    startActivity(intent);

                                                   /* DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("USER_INFORMATION");


                                                    Map<String,Object> params = new HashMap<String, Object>();
                                                    params.put("u_id", uid);
                                                    params.put("name", user_name);
                                                    params.put("bio", user_bio);
                                                    params.put("user_name", user_name);
                                                    params.put("user_id", user_id);
                                                    params.put("user_phone_number", user_phoneNumber);
                                                    params.put("user_gmail", "empty");
                                                    params.put("user_login_with", "0");
                                                    userRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(params);

                                                    Intent intent = new Intent(SetupProfileActivity.this, MainActivity.class);
                                                    startActivity(intent);
                                                    overridePendingTransition(R.anim.slide_up, R.anim.slide_down);
                                                    finish();*/
                                                }
                                            }
                                        })
                                        .build();

                            }

                            @Override
                            public void onFailure(@NonNull Call<ResponseModel> call, @NonNull Throwable t) {
                                Log.e("TAG", t.getMessage().toString());
                                call.cancel();
                                alertDialog.dismiss();

                            }
                        });


                    } else {
                        Toast.makeText(OtpVerificationActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }







    public boolean validation() {

        if (edOtp1.getText().toString().isEmpty()) {
            edOtp1.setError("");
            return false;
        }
        if (edOtp2.getText().toString().isEmpty()) {
            edOtp2.setError("");
            return false;
        }
        if (edOtp3.getText().toString().isEmpty()) {
            edOtp3.setError("");
            return false;
        }
        if (edOtp4.getText().toString().isEmpty()) {
            edOtp4.setError("");
            return false;
        }
        if (edOtp5.getText().toString().isEmpty()) {
            edOtp5.setError("");
            return false;
        }
        if (edOtp6.getText().toString().isEmpty()) {
            edOtp6.setError("");
            return false;
        }
        return true;
    }
}
