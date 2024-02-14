package com.ta.elivgo.model;

import com.google.gson.annotations.SerializedName;

public class ListReservation {
    @SerializedName("name")
    private String name;
    @SerializedName("id")
    private String id;
    @SerializedName("address")
    private String address;
    @SerializedName("code")
    private String code;
    @SerializedName("status")
    private String status;
    @SerializedName("start_datetime")
    private String datestart;
    @SerializedName("end_datetime")
    private String dateend;

    public ListReservation(String name, String id, String address, String datestart, String dateend, String code, String status) {
        this.name = name;
        this.id = id;
        this.address = address;
        this.datestart = datestart;
        this.dateend = dateend;
        this.code = code;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getAddress() {
        return address;
    }
    public String getDatestart() {
        return datestart;
    }
    public String getDateEnd() {
        return dateend;
    }
    public String getCode() {return code;}
    public String getStatus() {return status;}
}
