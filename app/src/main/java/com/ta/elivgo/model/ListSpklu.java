package com.ta.elivgo.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ListSpklu {

    @SerializedName("name")
    private String name;
    @SerializedName("id")
    private String id;
    @SerializedName("address")
    private String address;
    @SerializedName("owner")
    private String owner;
    @SerializedName("city")
    private String city;
    @SerializedName("latitude")
    private String latitude;
    @SerializedName("longitude")
    private String longitude;
    @SerializedName("capacity")
    private String capacity;
    @SerializedName("code")
    private String code;
    @SerializedName("distance")
    private String distance;
    @SerializedName("acStatus")
    private String[] acStatus;
    @SerializedName("dcStatus")
    private String[] dcStatus;


    private boolean expanded = false;

    public ListSpklu ( String id, String name, String address, String city, String owner, String capacity, String latitude, String longitude, String distance, String code, String[] acStatus, String[] dcStatus) {
        this.name = name;
        this.id = id;
        this.address = address;
        this.owner = owner;
        this.city = city;
        this.latitude = latitude;
        this.longitude = longitude;
        this.capacity = capacity;
        this.distance= distance;
        this.acStatus = acStatus;
        this.dcStatus = dcStatus;
        this.code = code;
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

    public String getOwner() {
        return owner;
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

    public String getCapacity() {
        return capacity;
    }

    public String getDistance() {return distance;}

    public void setCapacity(String capacity) {
        this.capacity = capacity;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String[] getAcStatus() {
        return acStatus;
    }

    public String[] getDcStatus() {
        return dcStatus;
    }
}
