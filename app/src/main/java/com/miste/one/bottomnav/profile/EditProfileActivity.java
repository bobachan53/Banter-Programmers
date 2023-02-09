package com.miste.one.bottomnav.profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.JsonObject;
import com.makeramen.roundedimageview.RoundedImageView;
import com.miste.one.HomeActivity;
import com.miste.one.R;
import com.miste.one.common.Constants;
import com.miste.one.models.ResponseModel;
import com.miste.one.retro.APIClient;
import com.miste.one.retro.APIInterface;
import com.miste.one.startup.OtpVerificationActivity;
import com.miste.one.utils.FileUtils;
import com.squareup.picasso.Picasso;

import java.io.File;

import dmax.dialog.SpotsDialog;
import ir.androidexception.andexalertdialog.AndExAlertDialog;
import ir.androidexception.andexalertdialog.AndExAlertDialogListener;
import ir.shahabazimi.instagrampicker.InstagramPicker;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Part;

public class EditProfileActivity extends AppCompatActivity {


    private AppBarLayout ihoioi;
    private RelativeLayout toolbar;
    private ImageView backButton;
    private TextView mainHomeTextView;
    private RoundedImageView imgProfile;
    private EditText nameEdittext;
    private EditText emailEdittext;
    private EditText addressEdittext;
    private EditText phoneNumberEdittext;
    private Button saveChangesButton;
    String profileImagePath = "";

    private AlertDialog alertDialog;

    APIInterface apiInterface;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        ihoioi = (AppBarLayout) findViewById(R.id.ihoioi);
        toolbar = (RelativeLayout) findViewById(R.id.toolbar);
        backButton = (ImageView) findViewById(R.id.backButton);
        mainHomeTextView = (TextView) findViewById(R.id.main_home_textView);
        imgProfile = (RoundedImageView) findViewById(R.id.img_profile);
        nameEdittext = (EditText) findViewById(R.id.nameEdittext);
        emailEdittext = (EditText) findViewById(R.id.emailEdittext);
        addressEdittext = (EditText) findViewById(R.id.addressEdittext);
        phoneNumberEdittext = (EditText) findViewById(R.id.phoneNumberEdittext);
        saveChangesButton = (Button) findViewById(R.id.saveChangesButton);


        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        mAuth = FirebaseAuth.getInstance();
        apiInterface = APIClient.getClient().create(APIInterface.class);
        alertDialog = new SpotsDialog(EditProfileActivity.this);

        UpdateProfile(mAuth.getCurrentUser().getUid());
        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InstagramPicker in = new InstagramPicker(EditProfileActivity.this);
                in.show(0,0, address ->  {
                    Log.e("Uri", String.valueOf(Uri.parse(address)));
                    Log.e("TAG", address);
                    profileImagePath = address;
                    Picasso.get().load(address).into(imgProfile);
//                    content://media/external/images/media/3610
                    //here you get your selected picture address in String format.
                    // you can convert it to Uri if you want: Uri.parse();

                    alertDialog.show();
                    alertDialog.setMessage("Message");
                    alertDialog.setTitle("Photo Uploading..");
                    alertDialog.setCancelable(false);


                    String imagePath = profileImagePath;

                    File file = FileUtils.getFile(EditProfileActivity.this, Uri.parse(imagePath));

                    RequestBody requestFile =
                            RequestBody.create(
                                    MediaType.parse("image/*"),
                                    file
                            );

                    MultipartBody.Part body =
                            MultipartBody.Part.createFormData("image", file.getName(), requestFile);


                    RequestBody uid_body= RequestBody.create(MultipartBody.FORM, mAuth.getCurrentUser().getUid());

                    Call<ResponseModel> call = apiInterface.update_profile_photo(uid_body,body);

                    call.enqueue(new Callback<ResponseModel>() {
                        @Override
                        public void onResponse(@NonNull Call<ResponseModel> call, @NonNull Response<ResponseModel> response) {
                            Log.e("TAG", response.toString());

                            alertDialog.dismiss();

                            ResponseModel responseModel = response.body();

                            Log.e("TAG", responseModel.getMessage());

                            new AndExAlertDialog.Builder(EditProfileActivity.this)
                                    .setTitle("MESSAGE")
                                    .setMessage(responseModel.getMessage())
                                    .setPositiveBtnText("Ok")
                                    .setCancelableOnTouchOutside(false)
                                    .OnPositiveClicked(new AndExAlertDialogListener() {
                                        @Override
                                        public void OnClick(String input) {
                                            if(responseModel.isSuccess()) {

//                                            Intent intent = new Intent(EditProfileActivity.this, HomeActivity.class);
//                                            startActivity(intent);

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

                });
            }
        });

        saveChangesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String name = nameEdittext.getText().toString().trim();
                String number = phoneNumberEdittext.getText().toString().trim();
                String email = emailEdittext.getText().toString().trim();
                String address = addressEdittext.getText().toString().trim();



                if (name.isEmpty()) {
                    Toast.makeText(EditProfileActivity.this, "Name is empty.", Toast.LENGTH_SHORT).show();
                    return;
                }


                if (email.isEmpty()) {
                    Toast.makeText(EditProfileActivity.this, "Email is empty.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (address.isEmpty()) {
                    Toast.makeText(EditProfileActivity.this, "Address is empty.", Toast.LENGTH_SHORT).show();
                    return;
                }
                alertDialog.show();
                alertDialog.setMessage("Message");
                alertDialog.setTitle("Registering User..");
                alertDialog.setCancelable(false);



                RequestBody name_body= RequestBody.create(MultipartBody.FORM, name);
                RequestBody number_body= RequestBody.create(MultipartBody.FORM, number);
                RequestBody address_body= RequestBody.create(MultipartBody.FORM, address);
                RequestBody email_body= RequestBody.create(MultipartBody.FORM, email);
//                RequestBody points_body= RequestBody.create(MultipartBody.FORM, "0");
//                RequestBody active_status_body= RequestBody.create(MultipartBody.FORM, "1");
                RequestBody last_login_time_body= RequestBody.create(MultipartBody.FORM, "");
                RequestBody uid_body= RequestBody.create(MultipartBody.FORM, mAuth.getCurrentUser().getUid());



                Call<ResponseModel> call = apiInterface.fileUpload(name_body,number_body,email_body,address_body,last_login_time_body
                        ,uid_body);

                call.enqueue(new Callback<ResponseModel>() {
                    @Override
                    public void onResponse(@NonNull Call<ResponseModel> call, @NonNull Response<ResponseModel> response) {
                        Log.e("TAG", response.toString());

                        alertDialog.dismiss();

                        ResponseModel responseModel = response.body();

                                Log.e("TAG", responseModel.getMessage());

                        new AndExAlertDialog.Builder(EditProfileActivity.this)
                                .setTitle("MESSAGE")
                                .setMessage(responseModel.getMessage())
                                .setPositiveBtnText("Ok")
                                .setCancelableOnTouchOutside(false)
                                .OnPositiveClicked(new AndExAlertDialogListener() {
                                    @Override
                                    public void OnClick(String input) {
                                        if(responseModel.isSuccess()) {

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
            }
        });

    }
    private void UpdateProfile(String u_id) {
        String uid = u_id;
        RequestBody uid_body= RequestBody.create(MultipartBody.FORM, uid);
        Call<JsonObject> call = apiInterface.get_customer_info(uid_body);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {


                String id = response.body().get("id").toString().replaceAll("\"","");
                String name = response.body().get("name").toString().replaceAll("\"","");
                String number = response.body().get("number").toString().replaceAll("\"","");
                String points = response.body().get("points").toString().replaceAll("\"","");
                String active_status = response.body().get("active_status").toString().replaceAll("\"","");
                String last_login_time = response.body().get("last_login_time").toString().replaceAll("\"","");
                String uid = response.body().get("uid").toString().replaceAll("\"","");
                String image = response.body().get("image").toString().replaceAll("\"","");
                String status = response.body().get("status").toString().replaceAll("\"","");
                String email = response.body().get("email").toString().replaceAll("\"","");
                String address = response.body().get("address").toString().replaceAll("\"","");

                if(!image.equals("null"))
                {
                    String imageUrl = Constants.IMAGE_URL+image;

                    Picasso.get().load(imageUrl.trim()).into(imgProfile);
                }

                if(!name.equals("null"))
                {
                    nameEdittext.setText(name);
                }

                if(!number.equals("null"))
                {
                    phoneNumberEdittext.setText(number);
                }

                if(!email.equals("null"))
                {
                    emailEdittext.setText(email);
                }
                if(!address.equals("null"))
                {
                    addressEdittext.setText(address);
                }




//                emailEdittext.setText(email);
//                addressEdittext.setText(address);






            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                Log.e("TAG", t.getMessage().toString());


            }
        });

    }
}