package com.ta.elivgo.globalfunction;

import android.util.Log;

import java.text.DecimalFormat;

public class DistanceConverter {

    public String ConvertDistance(String val) {
        Log.d("SUNDARI Distance ", val);
        Double distance = Double.parseDouble(val);
        DecimalFormat numberFormat = new DecimalFormat("#.00");
        String strDistance = "";
        if (distance / 1000 < 1 ) {
            strDistance = numberFormat.format(distance) + " m";
        } else  {
            strDistance =numberFormat.format(distance/1000) + " km";
        }
        return strDistance;
    }

}
