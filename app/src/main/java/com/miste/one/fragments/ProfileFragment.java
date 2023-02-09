package com.miste.one.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.JsonObject;
import com.makeramen.roundedimageview.RoundedImageView;
import com.miste.one.HomeActivity;
import com.miste.one.R;
import com.miste.one.bottomnav.profile.EditProfileActivity;
import com.miste.one.bottomnav.profile.SupportActivity;
import com.miste.one.common.Constants;
import com.miste.one.retro.APIClient;
import com.miste.one.retro.APIInterface;
import com.miste.one.startup.OtpVerificationActivity;
import com.squareup.picasso.Picasso;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {


    private RoundedImageView imgProfile;
    private LinearLayout editProfilell;
    private LinearLayout bookingsll;
    private LinearLayout walletll;
    private LinearLayout refernearnll;
    private LinearLayout redeemrewardsll;
    private LinearLayout aboutusll;
    private LinearLayout supportll;
    private LinearLayout logoutll;
    private TextView nametextview;



    APIInterface apiInterface;
    private FirebaseAuth mAuth;

    View myView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myView =  inflater.inflate(R.layout.fragment_profile, container, false);

        imgProfile = (RoundedImageView) myView.findViewById(R.id.img_profile);
        editProfilell = (LinearLayout) myView.findViewById(R.id.edit_profilell);
        bookingsll = (LinearLayout) myView.findViewById(R.id.bookingsll);
        walletll = (LinearLayout) myView.findViewById(R.id.walletll);
        refernearnll = (LinearLayout) myView.findViewById(R.id.refernearnll);
        redeemrewardsll = (LinearLayout) myView.findViewById(R.id.redeemrewardsll);
        aboutusll = (LinearLayout) myView.findViewById(R.id.aboutusll);
        supportll = (LinearLayout) myView.findViewById(R.id.supportll);
        logoutll = (LinearLayout) myView.findViewById(R.id.logoutll);
        nametextview = (TextView) myView.findViewById(R.id.nametextview);


        return myView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        mAuth = FirebaseAuth.getInstance();
        apiInterface = APIClient.getClient().create(APIInterface.class);


//         UpdateProfile(mAuth.getCurrentUser().getUid());

        editProfilell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), EditProfileActivity.class);
                startActivity(intent);
            }
        });

        supportll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SupportActivity.class);
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
//                        Log.e("TAG", imageUrl );
                        Picasso.get().load(imageUrl.trim()).into(imgProfile);
                    }

                    if(!name.equals("null"))
                    {
                        String cap = name.substring(0, 1).toUpperCase() + name.substring(1);
                        nametextview.setText(cap);
                    }



                }
                else
                {
                    Toast.makeText(getActivity(), "Network Error has occured.", Toast.LENGTH_SHORT).show();
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