package com.ta.elivgo.rest;

import com.ta.elivgo.response.DataResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface InterfaceConnection {

    @GET("spklu")
    Call<DataResponse> getAllSpklus();

    @FormUrlEncoded
    @POST("spkludist")
    Call<DataResponse> getAllSpklusDist(
            @Field("latitude") double latitude,
            @Field("longitude") double longitude
    );

    @FormUrlEncoded
    @POST("spklu")
    Call<DataResponse> getSpkluDetail(
            @Field("id") String id,
            @Field("latitude") String latitude,
            @Field("longitude") String longitude
    );

    @GET("user/{id}/reservations")
    Call<DataResponse> getUserReservations(@Path("id") String id);

    @GET("reservation/{id}")
    Call<DataResponse> getReservationDetail(@Path("id") String id);


    //@Headers({"Content-type: application/json", "Connection: close", "Accept-Encoding: identity", "Content-Length: 32"})
    @FormUrlEncoded
    @POST("reservation/store")
    Call<DataResponse> createReservation(
            @Field("spklu_connector_id") int connId,
            @Field("user_payment_id") int userId,
            @Field("start_datetime") String startDt,
            @Field("end_datetime") String endDt
    );

    @FormUrlEncoded
    @POST("reservation/{id}/update")
    Call<DataResponse> updateReservation(
            @Path("id") String id,
            @Field("action") String action,
            @Field("start_datetime") String startDt,
            @Field("end_datetime") String endDt
    );

    @FormUrlEncoded
    @POST("config")
    Call<DataResponse> getConfiguration(
            @Field("name") String name
    );

    @FormUrlEncoded
    @POST("charging/start")
    Call<DataResponse> startCharging(
            @Field("code") String code,
            @Field("userid") String userid,
            @Field("start_datetime") long start_datetime
    );

    @FormUrlEncoded
    @POST("charging/stop")
    Call<DataResponse> stopCharging(
            @Field("chargeid") String id
    );

    @FormUrlEncoded
    @POST("charging/calculate")
    Call<DataResponse> calculateCharge(
            @Field("id") String id
    );

    @FormUrlEncoded
    @POST("charging/checkvalidity")
    Call<DataResponse> cekChargeValidity(
            @Field("userid") String id,
            @Field("code") String code
    );


}
