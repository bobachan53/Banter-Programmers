package com.miste.one.retro;

import androidx.annotation.Nullable;


import com.google.gson.JsonObject;
import com.miste.one.models.ResponseModel;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface APIInterface {


    @Multipart
    @POST("/customers")
    Call<ResponseModel> fileUpload(

            @Part("name") RequestBody name,
            @Part("number") RequestBody number,
            @Part("email") RequestBody email,
            @Part("address") RequestBody address,
            @Part("last_login_time") RequestBody last_login_time,
            @Part("uid") RequestBody uid);


    @Multipart
    @POST("/normal_register")
    Call<ResponseModel> normal_register(
            @Part("name") RequestBody name,
            @Part("number") RequestBody number,
            @Part("points") RequestBody points,
            @Part("active_status") RequestBody active_status,
            @Part("last_login_time") RequestBody last_login_time,
            @Part("uid") RequestBody uid,
            @Part("status") RequestBody status,
            @Part("deviceId") RequestBody deviceId
 );

 @Multipart
    @POST("/update_profile_photo")
    Call<ResponseModel> update_profile_photo(
         @Part("uid") RequestBody uid,
         @Part MultipartBody.Part image
 );


    @Multipart
    @POST("/get_customer_info")
    Call<JsonObject> get_customer_info(
            @Part("uid") RequestBody uid);


}


