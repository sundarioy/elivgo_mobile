package com.ta.elivgo.globalfunction;

public class StatusConvertion {

    public String reservationStatus(String code) {
        String value = null;
        if (code == "0") {
            value = "Open";
        } else if (code == "1") {
            value = "In-Progress";
        } else if (code == "2") {
            value = "Close";
        } else if (code == "3") {
            value = "Cancel";
        }
        return value;
    }

}
