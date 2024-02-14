package com.ta.elivgo.model;

import com.google.gson.annotations.SerializedName;

public class Connectors {

    @SerializedName("id")
    private String id;
    @SerializedName("type")
    private String type;
    @SerializedName("conn_code")
    private String conn_code;
    @SerializedName("status")
    private String status;
    @SerializedName("price")
    private String price;
    @SerializedName("etc")
    private String etc;
    @SerializedName("queue")
    private String queue;
    @SerializedName("wait_time")
    private String wait_time;

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getConn_code() {
        return conn_code;
    }

    public String getStatus() {
        return status;
    }

    public String getPrice() {
        return price;
    }

    public String getEtc() {
        return etc;
    }

    public String getQueue() {
        return queue;
    }

    public String getWait_time() {
        return wait_time;
    }
}
