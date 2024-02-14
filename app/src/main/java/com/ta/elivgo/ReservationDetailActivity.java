package com.ta.elivgo;

import static java.security.AccessController.getContext;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ta.elivgo.globalfunction.EpochConverter;
import com.ta.elivgo.model.DetailReservation;
import com.ta.elivgo.model.DetailSpklu;
import com.ta.elivgo.response.DataResponse;
import com.ta.elivgo.rest.ApiConnection;
import com.ta.elivgo.rest.InterfaceConnection;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReservationDetailActivity extends AppCompatActivity {

    EditText etDate, etTimeStart, etTimeEnd;
    TextView tvName, tvAddress, tvCode, tvStatus;
    Button btnCancel, btnUpdate;
    final Calendar myCalendar= Calendar.getInstance();
    List<DetailReservation> detailData = new ArrayList<>();
    InterfaceConnection interfaceConnection;
    EpochConverter ec;

//    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation_detail);

        interfaceConnection = ApiConnection.getClient().create(InterfaceConnection.class);
        etDate =(EditText) findViewById(R.id.rsv_date);
        etTimeStart = (EditText) findViewById(R.id.rsv_ts);
        etTimeEnd = (EditText) findViewById(R.id.rsv_te);
        tvName =  findViewById(R.id.tv_rsv_spklu_name);
        tvAddress = findViewById(R.id.tv_rsv_spklu_addr);
        tvStatus = findViewById(R.id.tv_rsv_val_status);
        tvCode = findViewById(R.id.tv_rsv_val_code);
        btnUpdate = findViewById(R.id.btn_edit_rsv);
        btnCancel = findViewById(R.id.btn_delete_rsv);

        //tvName.setText(getIntent().getStringExtra("name"));
        //tvAddress.setText(getIntent().getStringExtra("address"));

        DatePickerDialog.OnDateSetListener date =new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH,month);
                myCalendar.set(Calendar.DAY_OF_MONTH,day);
                datePicker.setMinDate(System.currentTimeMillis() - 1000);
                updateLabel();
            }

        };

        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(view.getContext(),date,myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        etTimeStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePicker(etTimeStart);
            }
        });

        etTimeEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePicker(etTimeEnd);
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateReservation(getIntent().getStringExtra("id"),"cancel");
            }
        });

        loadData(getIntent().getStringExtra("id"));

    }

    private void updateLabel(){
        String myFormat="dd/MM/yyyy";
        SimpleDateFormat dateFormat=new SimpleDateFormat(myFormat, Locale.getDefault());
        etDate.setText(dateFormat.format(myCalendar.getTime()));
    }

    private void showTimePicker(EditText et){
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(ReservationDetailActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                et.setText(selectedHour + ":" + selectedMinute);
            }
        }, hour, minute, false);
        timePickerDialog.show();

    }

    public void loadData(String id) {
        Call<DataResponse> getResponse = interfaceConnection.getReservationDetail(id);
        getResponse.enqueue(new Callback<DataResponse>() {
            @Override
            public void onResponse(Call<DataResponse> call, Response<DataResponse> response) {
                if (response.isSuccessful()) {
                   detailData = response.body().getReservationDetail();
                   ec = new EpochConverter();
                   tvName.setText(detailData.get(0).getName());
                   tvAddress.setText(detailData.get(0).getAddress());
                   tvCode.setText(detailData.get(0).getCode());
                    if (detailData.get(0).getStatus().equals("0")) {
                        tvStatus.setText("Open");
                        tvStatus.setBackgroundColor(Color.parseColor("#03A9F4"));
                        btnCancel.setVisibility(View.VISIBLE);
                        btnUpdate.setVisibility(View.VISIBLE);
                    } else if (detailData.get(0).getStatus().equals("1")) {
                        tvStatus.setText("In-Progress");
                        tvStatus.setBackgroundColor(Color.parseColor("#8BC34A"));
                    } else if (detailData.get(0).getStatus().equals("2")) {
                        tvStatus.setText("Close");
                        tvStatus.setBackgroundColor(Color.parseColor("#3B3B3B"));
                    } else if (detailData.get(0).getStatus().equals("3")) {
                        tvStatus.setText("Canceled");
                        tvStatus.setBackgroundColor(Color.parseColor("#D50000"));
                    }
                   etDate.setText(ec.fromEpoch(Long.parseLong(detailData.get(0).getStart_datetime()),"dd/MM/yyyy"));
                   etTimeStart.setText(ec.fromEpoch(Long.parseLong(detailData.get(0).getStart_datetime()),"HH:mm"));
                   etTimeEnd.setText(ec.fromEpoch(Long.parseLong(detailData.get(0).getEnd_datetime()),"HH:mm"));

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

    private void updateReservation(String id, String action) {
        Log.d("ACTIONNYA", action);
        if (String.valueOf(action) == "cancel" ) {
            Call<DataResponse> cancelReservation = interfaceConnection.updateReservation(id, action, "", "");
            cancelReservation.enqueue(new Callback<DataResponse>() {
                @Override
                public void onResponse(Call<DataResponse> call, Response<DataResponse> response) {
                    if (response.isSuccessful()){
                        Toast.makeText(getApplicationContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        try {
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            Toast.makeText(getApplicationContext(), jObjError.getString("message"), Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<DataResponse> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), "Terjadi kesalahan", Toast.LENGTH_SHORT).show();
                }
            });
        } else {

        }
    }

}