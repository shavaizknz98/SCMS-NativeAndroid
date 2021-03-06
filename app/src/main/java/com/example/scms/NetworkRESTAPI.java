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

    //user sign up
    @FormUrlEncoded //you have to do this
    @POST("/user/signUp")   //last thing from the url only
    Call<ResponseBody> signUpUser(
            @Field("user_email") String emailAddress,
            @Field("phonenumber") String phoneNumber,
            @Field("fullname") String fullName,
            @Field("password") String password
    );


    //user login
    @FormUrlEncoded
    @POST("/user/login")
    Call<ResponseBody> signInUser(
        @Field("user_email") String emailAddress,
        @Field("password") String password
    );


    //get available bikes
    @FormUrlEncoded
    @POST("/user/getAvailableBikes")
    Call<ResponseBody> getAvailableBikes (
            @Field("temp") String randomThing
    );

    //reserve a bike
    @FormUrlEncoded
    @POST("/user/reserve")
    Call<ResponseBody> reserveBike(
            @Field("user_id") String userID,
            @Field("bike_id") String bikeID
    );

    //reserve a bike
    @FormUrlEncoded
    @POST("/user/cancelReservation")
    Call<ResponseBody> cancelReservation(
            @Field("user_id") String userID,
            @Field("bike_id") String bikeID
    );

    //reservation timer
    @FormUrlEncoded
    @POST("/user/startReservationTimer")
    Call<ResponseBody> startReservationTimer(
            @Field("aa") String empty
    );

    //report a user
    @FormUrlEncoded
    @POST("/user/reportUser")
    Call<ResponseBody> reportUser(
            @Field("bike_id") String bikeID
    );

    //start ride
    @FormUrlEncoded
    @POST("/user/startRide")
    Call<ResponseBody> startRide(
            @Field("user_id") String userID,
            @Field("slot_id") String slotID
    );

    //end ride
    @FormUrlEncoded
    @POST("/user/endRide")
    Call<ResponseBody> endRide(
            @Field("user_id") String userID,
            @Field("slot_id") String slotID
    );

    //get user profile
    @FormUrlEncoded
    @POST("/user/getUserInfo")
    Call<ResponseBody> getUserInfo(
            @Field("user_email") String userID
    );

}
