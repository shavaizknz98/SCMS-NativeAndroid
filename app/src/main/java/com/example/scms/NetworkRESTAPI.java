package com.example.scms;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface NetworkRESTAPI {
//10.25.149.7:5000/user/test

    @FormUrlEncoded //you have to do this
    @POST("post")   //last thing from the url only
    Call<ResponseBody> signUpUser(
            @Field("email") String emailAddress,
            @Field("phonenumber") String phoneNumber,
            @Field("fullname") String fullName,
            @Field("password") String password
    );


    @FormUrlEncoded
    @POST("post")
    Call<ResponseBody> signInUser(
        @Field("email") String emailAddress,
                @Field("password") String password
    );
}