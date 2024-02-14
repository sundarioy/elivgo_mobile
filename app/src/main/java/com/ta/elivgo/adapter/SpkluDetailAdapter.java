package com.ta.elivgo.adapter;

import android.content.Context;

import com.ta.elivgo.model.DetailSpklu;

import java.util.ArrayList;

public class SpkluDetailAdapter {

    private ArrayList<DetailSpklu> detailSpklu;
    private Context mCtx;

    public SpkluDetailAdapter(Context mCtx) {
        this.detailSpklu = new ArrayList<>();
        this.mCtx = (Context) mCtx;
    }

    

}
