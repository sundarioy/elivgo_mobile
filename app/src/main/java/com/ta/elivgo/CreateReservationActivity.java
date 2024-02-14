package com.ta.elivgo;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ta.elivgo.globalfunction.EpochConverter;
import com.ta.elivgo.response.DataResponse;
import com.ta.elivgo.rest.ApiConnection;
import com.ta.elivgo.rest.InterfaceConnection;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateReservationActivity extends AppCompatActivity {

    TextView tvName, tvAddress;
    EditText etDate, etStartTime, etEndTime, etNote;
    Spinner spConnectorList;
    Button btnCreate;
    InterfaceConnection interfaceConnection;
    EpochConverter ec;
    final Calendar myCalendar= Calendar.getInstance();

    String id, idConn;

    String[] connector = { "AC", "DC"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_reservation);

        etDate = findViewById(R.id.et_rsv_cr_date);
        etStartTime = findViewById(R.id.et_rsv_cr_ts);
        etEndTime = findViewById(R.id.et_rsv_cr_te);
        etNote = findViewById(R.id.et_rsv_cr_note);
        btnCreate = findViewById(R.id.btn_create_rsv);
        tvName = findViewById(R.id.tv_rsv_cr_spklu_name);
        tvAddress = findViewById(R.id.tv_rsv_cr_spklu_addr);
        spConnectorList = findViewById(R.id.sp_rsv_cr_conn);

        tvName.setText(getIntent().getStringExtra("name"));
        tvAddress.setText(getIntent().getStringExtra("address"));
        id = getIntent().getStringExtra("id");

        ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item,connector);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        spConnectorList.setAdapter(aa);

        spConnectorList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getApplicationContext(),connector[i] , Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        interfaceConnection = ApiConnection.getClient().create(InterfaceConnection.class);

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

        etStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePicker(etStartTime);
            }
        });

        etEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePicker(etEndTime);
            }
        });

        btnCreate.setOnClickListener(view -> SubmitReservation());
    }

    private void showTimePicker(EditText et){
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(CreateReservationActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                String hr = String.valueOf(selectedHour);
                String mt = String.valueOf(selectedMinute);
                hr = hr.length() == 1 ? "0"+hr : hr;
                mt = mt.length() == 1 ? "0"+mt : mt;
                et.setText(hr + ":" + mt);
            }
        }, hour, minute, true);
        timePickerDialog.show();

    }

    private void updateLabel(){
        String myFormat="yyyy-MM-dd";
        SimpleDateFormat dateFormat=new SimpleDateFormat(myFormat, Locale.getDefault());
        etDate.setText(dateFormat.format(myCalendar.getTime()));
    }

    private void SubmitReservation() {

        EpochConverter ec = new EpochConverter();

        int connId = Integer.parseInt(getIntent().getStringExtra("id"));
        int userId = 1;
        String startDt = ec.toEpoch(etDate.getText().toString()+"T"+etStartTime.getText().toString()+":00Z");
        Log.d("SUNDARI", String.valueOf(startDt));
        //String startDt = ec.toEpoch(etStartTime.toString()).toString();
        String endDt = ec.toEpoch(etDate.getText().toString()+"T"+etEndTime.getText().toString()+":00Z");
        Log.d("SUNDARI", String.valueOf(endDt));

        /*if (namaBarang.isEmpty() || namaBarang.length() == 0){
            layoutNamaBarang.setError("Nama Barang Tidak Boleh Kosong");
        } else if (jmlBarang.isEmpty() || jmlBarang.length() == 0){
            layoutJumlahBarang.setError("Jumlah Barang Tidak Boleh Kosong");
        } else if (((!namaBarang.isEmpty() && namaBarang.length() !=0) && (!jmlBarang.isEmpty() && jmlBarang.length()!=0))){
            */
            Call<DataResponse> createRsv = interfaceConnection
                    .createReservation(connId, userId, startDt, endDt);
            createRsv.enqueue(new Callback<DataResponse>() {
                @Override
                public void onResponse(Call<DataResponse> call, Response<DataResponse> response) {
                    if (response.isSuccessful()){
                        Toast.makeText(getApplicationContext() , response.body().getMessage(), Toast.LENGTH_LONG).show();
                        Log.d("SUNDARI", response.body().getMessage());
                        Intent intent = new Intent(getApplicationContext(), ReservationDetailActivity.class);
                        intent.putExtra("id",response.body().getMessage().toString());
                        startActivity(intent);
                    } else {
                        try {
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            Toast.makeText(getApplicationContext(), jObjError.getString("message"), Toast.LENGTH_LONG).show();
                            Log.d("SUNDARI", jObjError.getString("message") );
                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                            Log.d("SUNDARI", e.getMessage());
                        }
                    }
                }

                @Override
                public void onFailure(Call<DataResponse> call, Throwable t) {
                    Log.d("Error here", "Error here", t);
                    t.printStackTrace();
                    Log.d("Error here", "Error here2", t);
                    Toast.makeText(getApplicationContext(), "Terjadi kesalahan", Toast.LENGTH_SHORT).show();
                }
            });
        // }

    }

}