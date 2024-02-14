package com.ta.elivgo.model;

import com.google.gson.annotations.SerializedName;

public class DetailReservation {
    @SerializedName("id")
    private String id;
    @SerializedName("code")
    private String code;
    @SerializedName("status")
    private String status;
    @SerializedName("name")
    private String name;
    @SerializedName("address")
    private String address;
    @SerializedName("city")
    private String city;
    @SerializedName("latitude")
    private String latitude;
    @SerializedName("longitude")
    private String longitude;
    @SerializedName("type")
    private String type;
    @SerializedName("start_datetime")
    private String start_datetime;

    public String getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getCity() {
        return city;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getType() {
        return type;
    }

    public String getStart_datetime() {
        return start_datetime;
    }

    public String getEnd_datetime() {
        return end_datetime;
    }

    public String getStatus() {return  status;}

    @SerializedName("end_datetime")
    private String end_datetime;
}
