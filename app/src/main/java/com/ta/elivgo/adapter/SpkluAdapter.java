package com.ta.elivgo.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;
import com.ta.elivgo.R;
import com.ta.elivgo.SpkluDetailActivity;
import com.ta.elivgo.globalfunction.DistanceConverter;
import com.ta.elivgo.model.ListSpklu;
import com.ta.elivgo.response.DataResponse;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Callback;

public class SpkluAdapter extends RecyclerView.Adapter<SpkluAdapter.ViewHolder> implements Filterable {

    private ArrayList<ListSpklu> list, temp;
    private Context mCtx;
    public Location myLoc;

    public boolean isDefaultLocation;
    private Filter fRecords;

    DistanceConverter dc;

    public SpkluAdapter(Context mCtx) {
        this.list = new ArrayList<>();
        this.mCtx = (Context) mCtx;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.spklu_list_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ListSpklu listSpklu = list.get(position);
        boolean expanded = listSpklu.isExpanded();
        dc = new DistanceConverter();

        Location spkluLoc = new Location("B");
        spkluLoc.setLatitude(Double.parseDouble(listSpklu.getLatitude()));
        spkluLoc.setLongitude(Double.parseDouble(listSpklu.getLongitude()));

        /*float[] results = new float[1];
        Location.distanceBetween(-6.175392, 106.827153,
                Double.parseDouble(listSpklu.getLatitude()), Double.parseDouble(listSpklu.getLongitude()), results);
        float distance = results[0]; */

        if (!isDefaultLocation) {
            //holder.tvDistance.setText(dc.ConvertDistance(String.valueOf(distance)));
            holder.tvDistance.setText(dc.ConvertDistance(listSpklu.getDistance()));
        }
        holder.tvName.setText(listSpklu.getName() + " (" + listSpklu.getCode()+ ")");
        holder.tvAddress.setText(listSpklu.getAddress());
        holder.llConn.setVisibility(expanded ? View.VISIBLE : View.GONE);

        String[] acStatus, dcStatus;
        acStatus = listSpklu.getAcStatus();
        dcStatus = listSpklu.getDcStatus();
//        holder.tvConnAc.setText(acStatus[0]);
        if (acStatus[0].equals("4")) {
            holder.tvConnAc.setBackground(ContextCompat.getDrawable(mCtx, R.drawable.rounded_corner_gray));
            holder.tvAcStatus.setTextColor(Color.parseColor("#AFAFAF"));
            holder.tvAcStatus.setText("Tidak Tersedia");
        } else if (acStatus[0].equals("1")) {
            holder.tvConnAc.setBackground(ContextCompat.getDrawable(mCtx, R.drawable.rounded_corner_gray));
            holder.tvAcStatus.setText("Dalam Penggunaan");
            holder.tvAcStatus.setTextColor(Color.parseColor("#AFAFAF"));
            holder.ivAcWait.setVisibility(View.VISIBLE);
            holder.tvAcWait.setText(acStatus[1]+" menit");
            holder.tvAcWait.setVisibility(View.VISIBLE);
            holder.ivAcQue.setVisibility(View.VISIBLE);
            holder.tvAcQue.setText(acStatus[2]+" antrian");
            holder.tvAcQue.setVisibility(View.VISIBLE);
        } else {
            holder.tvConnAc.setBackground(ContextCompat.getDrawable(mCtx, R.drawable.rounded_corner_green));
            holder.tvAcStatus.setText("Tersedia");
            holder.tvAcStatus.setTextColor(Color.parseColor("#4CAF50"));
            holder.ivAcWait.setVisibility(View.INVISIBLE);
            holder.tvAcWait.setVisibility(View.INVISIBLE);
            holder.ivAcQue.setVisibility(View.INVISIBLE);
            holder.tvAcQue.setVisibility(View.INVISIBLE);
        }

        if (dcStatus[0].equals("4")) {
            holder.tvConnDc.setBackground(ContextCompat.getDrawable(mCtx, R.drawable.rounded_corner_gray));
            holder.tvDcStatus.setTextColor(Color.parseColor("#AFAFAF"));
            holder.tvDcStatus.setText("Tidak Tersedia");
        } else if (dcStatus[0].equals("1")) {
            holder.tvConnDc.setBackground(ContextCompat.getDrawable(mCtx, R.drawable.rounded_corner_gray));
            holder.tvDcStatus.setText("Dalam Penggunaan");
            holder.tvDcStatus.setTextColor(Color.parseColor("#AFAFAF"));
            holder.ivDcWait.setVisibility(View.VISIBLE);
            holder.tvDcWait.setText(dcStatus[1]+" menit");
            holder.tvDcWait.setVisibility(View.VISIBLE);
            holder.ivDcQue.setVisibility(View.VISIBLE);
            holder.tvDcQue.setText(dcStatus[2]+" antrian");
            holder.tvDcQue.setVisibility(View.VISIBLE);
        } else {
            holder.tvConnDc.setBackground(ContextCompat.getDrawable(mCtx, R.drawable.rounded_corner_green));
            holder.tvDcStatus.setText("Tersedia");
            holder.tvDcStatus.setTextColor(Color.parseColor("#4CAF50"));
            holder.ivDcWait.setVisibility(View.INVISIBLE);
            holder.tvDcWait.setVisibility(View.INVISIBLE);
            holder.ivDcQue.setVisibility(View.INVISIBLE);
            holder.tvDcQue.setVisibility(View.INVISIBLE);
        }
        holder.tvConnAc.setPadding(11,10,11,10);
        holder.tvConnDc.setPadding(10,10,10,10);

        holder.itemView.setOnClickListener(v -> {
            listSpklu.setExpanded(!expanded);
            notifyItemChanged(holder.getAdapterPosition());
        });

        holder.tvDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), SpkluDetailActivity.class);
                intent.putExtra("id", listSpklu.getId());
                intent.putExtra("lat", myLoc.getLatitude());
                intent.putExtra("long", myLoc.getLongitude());
                intent.putExtra("dist", dc.ConvertDistance(listSpklu.getDistance()));
                if (isDefaultLocation) {
                    intent.putExtra("default", "1");
                } else {
                    intent.putExtra("default", "0");
                }
                view.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void updateSpkluList(List<ListSpklu> data, Location loc, Boolean isDefault) {
        ListSpklu listSpklu;
        list.clear();
        for (int i = 0 ; i < data.size() ; i++) {
//            Log.i("val is ", data.get(i).getId());
            listSpklu = new ListSpklu(data.get(i).getId(), data.get(i).getName(), data.get(i).getAddress(),data.get(i).getCity(), data.get(i).getOwner(), data.get(i).getCapacity(), data.get(i).getLatitude(), data.get(i).getLongitude(), data.get(i).getDistance(),data.get(i).getCode(), data.get(i).getAcStatus(), data.get(i).getDcStatus());
            list.add(listSpklu);
        }
        myLoc = loc;
        isDefaultLocation = isDefault;
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        if(fRecords == null) {
            fRecords=new RecordFilter();
        }
        return fRecords;
    }

    private class RecordFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            Log.d("SUNDARI - Filter result", constraint.toString());

            FilterResults results = new FilterResults();
            temp = list;

            //Implement filter logic
            // if edittext is null return the actual list
            if (constraint == null || constraint.length() == 0) {
                //No need for filter
                results.values = list;
                results.count = list.size();

            } else {
                //Need Filter
                // it matches the text  entered in the edittext and set the data in adapter list
                ArrayList<ListSpklu> fRecords = new ArrayList<ListSpklu>();

                for (ListSpklu s : list) {
                    if (s.getName().toUpperCase().trim().contains(constraint.toString().toUpperCase().trim()) || s.getAddress().toUpperCase().trim().contains(constraint.toString().toUpperCase().trim())) {
                        fRecords.add(s);
                    }
                }
                results.values = fRecords;
                results.count = fRecords.size();
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint,FilterResults results) {

            //it set the data from filter to adapter list and refresh the recyclerview adapter
            list = (ArrayList<ListSpklu>) results.values;
            notifyDataSetChanged();
        }
    }

    public class ViewHolder extends  RecyclerView.ViewHolder{
        public TextView tvName, tvAddress, tvDistance, tvDetails, tvConnAc, tvConnDc, tvAcStatus, tvDcStatus, tvAcWait, tvDcWait, tvAcQue, tvDcQue;
        public ImageView ivAcWait, ivAcQue, ivDcWait, ivDcQue;
        public LinearLayout llConn;
        boolean expanded;
        public ViewHolder(View v) {
            super(v);
            tvName = (TextView) v.findViewById(R.id.tv_sc_spklu_name);
            tvAddress = (TextView) v.findViewById(R.id.tv_sc_spklu_addr);
            tvDistance = (TextView) v.findViewById(R.id.tv_sc_spklu_dist);
            tvDetails = (TextView) v.findViewById(R.id.tv_item_todetail);
            llConn = (LinearLayout) v.findViewById(R.id.ll_ds_detail_conn);
            tvConnAc = v.findViewById(R.id.tv_sc_conn_ac_val);
            tvAcStatus = v.findViewById(R.id.tv_sc_conn_ac_status);
            tvAcWait = v.findViewById(R.id.tv_sc_conn_ac_wait);
            tvAcQue = v.findViewById(R.id.tv_sc_conn_ac_que);
            ivAcWait = v.findViewById(R.id.iv_sc_conn_ac_wait);
            ivAcQue = v.findViewById(R.id.iv_sc_conn_ac_que);
            tvConnDc = v.findViewById(R.id.tv_sc_conn_dc_val);
            tvDcStatus = v.findViewById(R.id.tv_sc_conn_dc_status);
            tvDcWait = v.findViewById(R.id.tv_sc_conn_dc_wait);
            tvDcQue = v.findViewById(R.id.tv_sc_conn_dc_que);
            ivDcWait = v.findViewById(R.id.iv_sc_conn_dc_wait);
            ivDcQue = v.findViewById(R.id.iv_sc_conn_dc_que);
        }
    }
}


