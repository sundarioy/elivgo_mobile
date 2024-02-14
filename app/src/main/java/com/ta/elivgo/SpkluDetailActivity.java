package com.ta.elivgo;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ta.elivgo.globalfunction.DistanceConverter;
import com.ta.elivgo.model.DetailSpklu;
import com.ta.elivgo.response.DataResponse;
import com.ta.elivgo.rest.ApiConnection;
import com.ta.elivgo.rest.InterfaceConnection;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SpkluDetailActivity extends AppCompatActivity {

    ImageView rsv,ch,nav, ivAcWait, ivAcQue, ivDcWait, ivDcQue;
    TextView tvName, tvAddress, tvOwner, tvDistance, tvConnCount, tvAcStatus, tvAcWaitLbl, tvAcWaitVal, tvAcQueLbl, tvAcQueVal, tvDcStatus, tvDcWaitLbl, tvDcWaitVal, tvDcQueLbl, tvDcQueVal;
    private List<DetailSpklu> detailData = new ArrayList<>();
    InterfaceConnection interfaceConnection;
    DistanceConverter dc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spklu_detail);

        interfaceConnection = ApiConnection.getClient().create(InterfaceConnection.class);
        tvName = findViewById(R.id.tv_ds_spklu_name);
        tvAddress = findViewById(R.id.tv_ds_spklu_addr);
        tvOwner = findViewById(R.id.tv_ds_conn_own_val);
        tvDistance = findViewById(R.id.tv_ds_conn_dist_val);
        tvConnCount = findViewById(R.id.tv_ds_conn_count_val);
        tvAcStatus = findViewById(R.id.tv_ds_conn_ac_status_val);
        tvAcQueLbl = findViewById(R.id.tv_ds_conn_ac_que_lbl);
        tvAcQueVal = findViewById(R.id.tv_ds_conn_ac_que_val);
        tvAcWaitLbl = findViewById(R.id.tv_ds_conn_ac_wait_lbl);
        tvAcWaitVal = findViewById(R.id.tv_ds_conn_ac_wait);
        tvDcStatus = findViewById(R.id.tv_ds_conn_dc_status_val);
        tvDcQueLbl = findViewById(R.id.tv_ds_conn_dc_que_lbl);
        tvDcQueVal = findViewById(R.id.tv_ds_conn_dc_que_val);
        tvDcWaitLbl = findViewById(R.id.tv_ds_conn_dc_wait_lbl);
        tvDcWaitVal = findViewById(R.id.tv_ds_conn_dc_wait);
        ivAcQue = findViewById(R.id.iv_ds_conn_ac_que);
        ivAcWait = findViewById(R.id.iv_ds_conn_ac_wait);
        ivDcQue = findViewById(R.id.iv_ds_conn_dc_que);
        ivDcWait = findViewById(R.id.iv_ds_conn_dc_wait);

        rsv = (ImageView) findViewById(R.id.iv_ds_cir_rsv);
        rsv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), CreateReservationActivity.class);
                intent.putExtra("id", getIntent().getStringExtra("id")); //you can name the keys whatever you like
                intent.putExtra("name", detailData.get(0).getName() + " (" +detailData.get(0).getSpklu_code()+")");
                intent.putExtra("address", detailData.get(0).getAddress());
                startActivity(intent);
            }
        });

        loadData(getIntent().getStringExtra("id"),getIntent().getStringExtra("lat"),getIntent().getStringExtra("long"));

        ch = (ImageView) findViewById(R.id.iv_ds_cir_ch);
        ch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), ScanActivity.class);
                intent.putExtra("id", getIntent().getStringExtra("id")); //you can name the keys whatever you like
                intent.putExtra("name", detailData.get(0).getName() + " (" +detailData.get(0).getSpklu_code()+")");
                intent.putExtra("address", detailData.get(0).getAddress());
                startActivity(intent);
            }
        });

        nav = (ImageView) findViewById(R.id.iv_ds_cir_nav);
        nav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), NavActivity.class);
                startActivity(intent);
            }
        });

    }

    private void loadData(String id, String lat, String lng) {
        retrofit2.Call<DataResponse> getRespDetailSpklu = interfaceConnection.getSpkluDetail(id,lat,lng);
        getRespDetailSpklu.enqueue(new Callback<DataResponse>() {
            @Override
            public void onResponse(Call<DataResponse> call, Response<DataResponse> response) {
                if (response.isSuccessful()) {
                    detailData = response.body().getSpkluDetail();
                    dc = new DistanceConverter();
                    tvName.setText(detailData.get(0).getName()+" ("+detailData.get(0).getSpklu_code()+")");
                    tvAddress.setText(detailData.get(0).getAddress());
                    tvOwner.setText(detailData.get(0).getOwner());
                    if (getIntent().getStringExtra("default").equals("0")) {
                        tvDistance.setText(getIntent().getStringExtra("dist"));
                    } else {

                    }


                    Integer connCount = detailData.get(0).getConnectors().size();
                    String acStatus = null, acQue, acWait, acPrice, acCode, acType, dcStatus = null, dcQue, dcWait, dcPrice, dcCode, dcType;

                    if (connCount == 1) {
                        String type = detailData.get(0).getConnectors().get(0).getType().toString();
                        if (type.equals("AC")) {
                            dcStatus = "Tidak Tersedia";
                            acStatus = detailData.get(0).getConnectors().get(0).getStatus().toString();
                            if (acStatus.equals("0")) {
                                acStatus = "Tersedia";
                            } else {
                                acStatus = "Dalam Penggunaan";
                            }
                        } else {
                            acStatus = "Tidak Tersedia";
                            dcStatus = detailData.get(0).getConnectors().get(1).getStatus().toString();
                            if (dcStatus.equals("0")) {
                                dcStatus = "Tersedia";
                            } else {
                                dcStatus = "Dalam Penggunaan";
                            }
                        }
                    } else {
                        acCode = detailData.get(0).getConnectors().get(0).getConn_code().toString();
                        acPrice = detailData.get(0).getConnectors().get(0).getPrice().toString();
                        acType = detailData.get(0).getConnectors().get(0).getType().toString();
                        acStatus = detailData.get(0).getConnectors().get(0).getStatus().toString();
                        if (acStatus.equals("0")) {
                            acStatus = "Tersedia";
                            tvAcStatus.setTextColor(Color.parseColor("#4CAF50"));
                        } else {
                            acStatus = "Dalam Penggunaan";
                            tvAcStatus.setTextColor(Color.parseColor("#686868"));
                        }
                        acQue = detailData.get(0).getConnectors().get(0).getQueue().toString();
                        if (Integer.parseInt(acQue) != 0) {
                            ivAcQue.setVisibility(View.VISIBLE);
                            tvAcQueLbl.setVisibility(View.VISIBLE);
                            tvAcQueVal.setVisibility(View.VISIBLE);
                            tvAcQueVal.setText(acQue + " antrian");

                        }
                        acWait = detailData.get(0).getConnectors().get(0).getWait_time().toString();
                        if (Integer.parseInt(acWait) != 0) {
                            ivAcWait.setVisibility(View.VISIBLE);
                            tvAcWaitLbl.setVisibility(View.VISIBLE);
                            tvAcWaitVal.setVisibility(View.VISIBLE);
                            tvAcWaitVal.setText(acWait + " menit");
                        }

                        dcPrice = detailData.get(0).getConnectors().get(1).getPrice().toString();
                        dcCode = detailData.get(0).getConnectors().get(1).getConn_code().toString();
                        dcType = detailData.get(0).getConnectors().get(1).getType().toString();
                        dcStatus = detailData.get(0).getConnectors().get(1).getStatus().toString();
                        if (dcStatus.equals("0")) {
                            dcStatus = "Tersedia";
                            tvDcStatus.setTextColor(Color.parseColor("#4CAF50"));
                        } else {
                            dcStatus = "Dalam Penggunaan";
                            tvDcStatus.setTextColor(Color.parseColor("#686868"));
                        }
                        dcQue = detailData.get(0).getConnectors().get(1).getQueue().toString();
                        if (Integer.parseInt(dcQue) != 0) {
                            ivDcQue.setVisibility(View.VISIBLE);
                            tvDcQueLbl.setVisibility(View.VISIBLE);
                            tvDcQueVal.setVisibility(View.VISIBLE);
                            tvDcQueVal.setText(dcQue + " antrian");
                        }
                        dcWait = detailData.get(0).getConnectors().get(1).getWait_time().toString();
                        if (Integer.parseInt(dcWait) != 0) {
                            ivDcWait.setVisibility(View.VISIBLE);
                            tvDcWaitLbl.setVisibility(View.VISIBLE);
                            tvDcWaitVal.setText(dcWait + " menit");
                            tvDcWaitVal.setVisibility(View.VISIBLE);
                        }

                    }

                    tvDcStatus.setText(dcStatus);
                    tvAcStatus.setText(acStatus);

                    Log.d("SUNDARI ", detailData.get(0).getConnectors().get(0).getConn_code().toString());
                    tvConnCount.setText(connCount.toString());

                } else {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(getApplicationContext(), jObjError.getString("message"), Toast.LENGTH_LONG).show();
                        Log.i("error", jObjError.getString("message"));
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                        Log.i("error", e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<DataResponse> call, Throwable t) {
                Log.d("Error Jaringan", "disini");
                t.printStackTrace();
                Log.d("here", "here", t);
                Toast.makeText(getApplicationContext(), "Terjadi kesalahan", Toast.LENGTH_SHORT).show();
            }
        });
    }

}