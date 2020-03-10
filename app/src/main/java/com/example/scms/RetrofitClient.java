package com.example.scms;


import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    public static final String BASE_URL = "http://192.168.1.163:5000";
    private static RetrofitClient mRetrofitClient;
    private Retrofit retrofit;

    private RetrofitClient() {
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static synchronized RetrofitClient getRetrofitClient() {
        if(mRetrofitClient == null) {
            mRetrofitClient = new RetrofitClient();
        }
        return mRetrofitClient;
    }

    public NetworkRESTAPI getAPI() {
            return retrofit.create(NetworkRESTAPI.class);
    }

}
