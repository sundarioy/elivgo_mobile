package com.ta.elivgo.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ta.elivgo.R;
import com.ta.elivgo.ReservationDetailActivity;
import com.ta.elivgo.SpkluDetailActivity;
import com.ta.elivgo.globalfunction.EpochConverter;
import com.ta.elivgo.globalfunction.StatusConvertion;
import com.ta.elivgo.model.ListReservation;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ReservationAdapter extends RecyclerView.Adapter<ReservationAdapter.ViewHolder> {

    private final List<ListReservation> listR;
    private Context mCtx;

    StatusConvertion sc;

    EpochConverter ec;

    public ReservationAdapter(Context mCtx) {
        listR = new ArrayList<>();
        this.mCtx = mCtx;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.reservation_list_item, parent, false);
        return new ViewHolder(v);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ListReservation listReservation = listR.get(position);
        ec = new EpochConverter();
        sc = new StatusConvertion();
        String date, timestart, timeend, status;
        date = ec.fromEpoch(Long.parseLong(listReservation.getDatestart()),"dd MMM yyyy");
        timestart = ec.fromEpoch(Long.parseLong(listReservation.getDatestart()),"HH:mm");
        timeend = ec.fromEpoch(Long.parseLong(listReservation.getDateEnd()),"HH:mm");
        status = sc.reservationStatus(listReservation.getStatus());

        holder.tvName.setText(listReservation.getCode());
        holder.tvAddress.setText(listReservation.getName());
        holder.tvDate.setText(date);
        holder.tvTime.setText(timestart+" - "+timeend);
        if (listReservation.getStatus().equals("0")) {
            holder.tvStatus.setText("Open");
            holder.tvStatus.setBackgroundColor(Color.parseColor("#03A9F4"));
        } else if (listReservation.getStatus().equals("1")) {
            holder.tvStatus.setText("In-Progress");
            holder.tvStatus.setBackgroundColor(Color.parseColor("#8BC34A"));
        } else if (listReservation.getStatus().equals("2")) {
            holder.tvStatus.setText("Close");
            holder.tvStatus.setBackgroundColor(Color.parseColor("#FF5C5C5C"));
            holder.tvStatus.setTextColor(Color.parseColor("#FFFFFF"));
        } else if (listReservation.getStatus().equals("3")) {
            holder.tvStatus.setText("Canceled");
            holder.tvStatus.setBackgroundColor(Color.parseColor("#D50000"));
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), ReservationDetailActivity.class);
                intent.putExtra("id", listReservation.getId()); //you can name the keys whatever you like
                //intent.putExtra("name", listReservation.getName());
                //intent.putExtra("address", listReservation.getAddress());
                view.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listR.size();
    }

    public void updateReservation(List<ListReservation> data) {
        ListReservation listReservation;
        listR.clear();
        for (int i = 0 ; i < data.size() ; i++) {
//            Log.i("val is ", data.get(i).getId());
            listReservation = new ListReservation(data.get(i).getName(),data.get(i).getId(),data.get(i).getAddress(),data.get(i).getDatestart(),data.get(i).getDateEnd(), data.get(i).getCode(), data.get(i).getStatus());
            listR.add(listReservation);
        }
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvName, tvAddress, tvDate, tvTime, tvStatus;
        public ViewHolder(View v) {
            super(v);
            tvName = v.findViewById(R.id.tv_rsvl_val_name);
            tvAddress = v.findViewById(R.id.tv_rsvl_val_addr);
            tvDate = v.findViewById(R.id.tv_rsvl_val_date);
            tvTime = v.findViewById(R.id.tv_rsvl_val_time);
            tvStatus = v.findViewById(R.id.tv_rsvl_val_status);
        }
    }
}
