package com.ta.elivgo.response;

import com.google.gson.annotations.SerializedName;
import com.ta.elivgo.adapter.ReservationAdapter;
import com.ta.elivgo.model.DetailReservation;
import com.ta.elivgo.model.DetailSpklu;
import com.ta.elivgo.model.ListReservation;
import com.ta.elivgo.model.ListSpklu;

import java.util.List;

public class DataResponse {

    @SerializedName("data")
    List<ListSpklu> allSpklus;

    @SerializedName("spklus")
    List<ListSpklu> allSpkluDist;

    @SerializedName("detail_spklu")
    List<DetailSpklu> spkluDetail;

    @SerializedName("user_resv")
    List<ListReservation> userReservation;

    @SerializedName("rsv_detail")
    List<DetailReservation> reservationDetail;

    private Boolean status;
    private String message;

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<ListSpklu> getAllSpklus() {
        return allSpklus;
    }

    public List<ListSpklu> getAllSpklusDist() {
        return allSpkluDist;
    }

    public List<DetailSpklu> getSpkluDetail() {
        return spkluDetail;
    }

    public List<ListReservation> getUserReservations() { return userReservation; }

    public List<DetailReservation> getReservationDetail() { return reservationDetail; }



}
