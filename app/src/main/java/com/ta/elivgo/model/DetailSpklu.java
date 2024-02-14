package com.ta.elivgo.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class DetailSpklu {

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
    @SerializedName("distance")
    private String distance;
    @SerializedName("capacity")
    private String capacity;
    @SerializedName("price")
    private String price;
    @SerializedName("spklu_code")
    private String spklu_code;
    @SerializedName("connectors")
    private List<Connectors> connectors;

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

    public String getDistance() {
        return distance;
    }

    public String getPrice() {
        return price;
    }

    public String getSpklu_code() {
        return spklu_code;
    }

    public List<Connectors> getConnectors() {
        return connectors;
    }
}
