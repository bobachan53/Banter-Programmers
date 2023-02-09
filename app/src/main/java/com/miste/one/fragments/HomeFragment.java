package com.miste.one.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.JsonObject;
import com.makeramen.roundedimageview.RoundedImageView;
import com.miste.one.NotificationActivity;
import com.miste.one.R;
import com.miste.one.bottomnav.home.QrCodeScanActivity;
import com.miste.one.bottomnav.home.TopUpActivity;
import com.miste.one.bottomnav.profile.EditProfileActivity;
import com.miste.one.common.Constants;
import com.miste.one.retro.APIClient;
import com.miste.one.retro.APIInterface;
import com.miste.one.utils.App;
import com.nex3z.notificationbadge.NotificationBadge;
import com.squareup.picasso.Picasso;

import java.util.Calendar;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class HomeFragment extends Fragment {



    NotificationBadge badge;

    private AppBarLayout ihoioi;
    private RelativeLayout toolbar;
    private RoundedImageView imgProfile;
    private TextView nameTextview;
    private TextView wishTextview;
    private ImageView icon;




    ImageView qr_icon;

    APIInterface apiInterface;
    private FirebaseAuth mAuth;

    CardView topup_cardview,pay_cardview;

    View myView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myView = inflater.inflate(R.layout.fragment_home, container, false);

        badge = myView.findViewById(R.id.badge);

        ihoioi = (AppBarLayout)  myView.findViewById(R.id.ihoioi);
        toolbar = (RelativeLayout)  myView.findViewById(R.id.toolbar);
        imgProfile = (RoundedImageView)  myView.findViewById(R.id.img_profile);
        nameTextview = (TextView)  myView.findViewById(R.id.name_textview);
        wishTextview = (TextView)  myView.findViewById(R.id.wish_textview);
        icon = (ImageView)  myView.findViewById(R.id.icon);
        badge = (NotificationBadge)  myView.findViewById(R.id.badge);
        qr_icon = (ImageView)  myView.findViewById(R.id.qr_icon);
        topup_cardview =   myView.findViewById(R.id.topup_cardview);
        pay_cardview =   myView.findViewById(R.id.pay_cardview);

        return myView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Context context = requireContext().getApplicationContext();
        WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());

        Log.e("TAG", ip );
        mAuth = FirebaseAuth.getInstance();
        apiInterface = APIClient.getClient().create(APIInterface.class);

//        UpdateProfile(mAuth.getCurrentUser().getUid());
        badge.setNumber(10);

        Calendar c = Calendar.getInstance();
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);

        if(timeOfDay >= 0 && timeOfDay < 12){
            wishTextview.setText("Good Morning");

        }else if(timeOfDay >= 12 && timeOfDay < 16){
            wishTextview.setText("Good Afternoon");

        }else if(timeOfDay >= 16 && timeOfDay < 21){
            wishTextview.setText("Good Evening");

        }
        else if(timeOfDay >= 21 && timeOfDay < 24){
            wishTextview.setText("Good Night");

        }

        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), EditProfileActivity.class);
                startActivity(intent);
            }
        });

        topup_cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), TopUpActivity.class);
                startActivity(intent);
            }
        });
        pay_cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), QrCodeScanActivity.class);
                startActivity(intent);
            }
        });

        qr_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), QrCodeScanActivity.class);
                startActivity(intent);
            }
        });

        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), NotificationActivity.class);
                startActivity(intent);
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


                if(response.isSuccessful())
                {
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
                        String cap = name.substring(0, 1).toUpperCase() + name.substring(1);
                        nameTextview.setText("Hi, "+cap);
                    }


                }
                else
                {
                    App.makeToast("Network error as occured");
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

    @Override
    public void onResume() {
        super.onResume();
        UpdateProfile(mAuth.getCurrentUser().getUid());
    }
}