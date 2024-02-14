package com.ta.elivgo.rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiConnection {
//    public static final String BASE_URL = "http://192.168.221.92/sun/public/api/";
    public static final String BASE_URL = "http://192.168.1.6/sun/public/api/";
//            "http://127.0.0.1:8000/api/";

    private static Retrofit retrofit = null;
    public static Retrofit getClient() {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        if (retrofit==null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        /*return new Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BuildConfig.BASE_URL).build()
        */
        return retrofit;
    }
}
