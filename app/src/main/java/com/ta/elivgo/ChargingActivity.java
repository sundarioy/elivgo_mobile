package com.ta.elivgo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.ArraySet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ta.elivgo.response.DataResponse;
import com.ta.elivgo.rest.ApiConnection;
import com.ta.elivgo.rest.InterfaceConnection;

import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChargingActivity extends AppCompatActivity {

    TextView tvName, tvAddress, tvCode, tvChargeId, tvTotalKwh, tvTotalCharge, tvInfo;
    Button btnChargeStop;
    ImageView ivCircle;
    InterfaceConnection interfaceConnection,interfaceConnection2;
    private int seconds = 0;
    private boolean running;
    private boolean wasRunning;
    Handler handlerCharged;
    Runnable runnable;
    public int INTERVAL ;//= 1000 * 60 * 5; //5 minutes

    public String idcharge= null;

    public ChargingActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charging);

        tvName = findViewById(R.id.tv_ch_spklu_name);
        tvAddress = findViewById(R.id.tv_ch_spklu_addr);
        tvCode = findViewById(R.id.tv_ch_spklu_code);
        tvChargeId = findViewById(R.id.tv_ch_id);
        tvTotalKwh = findViewById(R.id.tv_ch_kwh_val);
        tvTotalCharge = findViewById(R.id.tv_ch_rp_val);
        tvInfo = findViewById(R.id.tv_ch_charge_info);
        btnChargeStop = findViewById(R.id.btn_ch_stop);
        ivCircle = findViewById(R.id.iv_ch_circle_yel);

        tvName.setText(getIntent().getStringExtra("name"));
        tvAddress.setText(getIntent().getStringExtra("address"));
        tvCode.setText(getIntent().getStringExtra("code"));

        interfaceConnection = ApiConnection.getClient().create(InterfaceConnection.class);

        if (savedInstanceState != null) {

            // Get the previous state of the stopwatch
            // if the activity has been
            // destroyed and recreated.
            seconds = savedInstanceState.getInt("seconds");
            running = savedInstanceState.getBoolean("running");
            wasRunning = savedInstanceState.getBoolean("wasRunning");
        }

        getInterval();

    }

    public void getInterval() {
        Call<DataResponse> getIntv = interfaceConnection.getConfiguration("charge_interval_s");
        getIntv.enqueue(new Callback<DataResponse>() {
            int res;
            @Override
            public void onResponse(Call<DataResponse> call, Response<DataResponse> response) {
                if (response.isSuccessful()){
                    res = Integer.parseInt(response.body().getMessage().toString());
                    INTERVAL = 1000 * res;
                    Log.d("SUNDARI get Interval", response.body().getMessage());
//                    Toast.makeText(getApplicationContext() , String.valueOf(INTERVAL), Toast.LENGTH_LONG).show();

                    startCharging(getIntent().getStringExtra("code"));

                } else {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
//                        Toast.makeText(getApplicationContext(), jObjError.getString("message"), Toast.LENGTH_LONG).show();
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

    }

    public void startCharging(String id) {
        long unixTime = System.currentTimeMillis() / 1000L;
        Log.d("startCharging: ", String.valueOf(unixTime));
        Log.d("startCharging: ", id);
        Call<DataResponse> startcharge = interfaceConnection.startCharging (id, "1", unixTime);
        startcharge.enqueue(new Callback<DataResponse>() {
            @Override
            public void onResponse(Call<DataResponse> call, Response<DataResponse> response) {
                if (response.isSuccessful()){
                    Log.d("SUNDARI charge start id : ", response.body().getMessage());
                    //idcharge = response.body().getMessage();
                    tvChargeId.setText(response.body().getMessage());
                    running = true;
                    runTimer(INTERVAL);
                    //Toast.makeText(getApplicationContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
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
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt("seconds", seconds);
        savedInstanceState.putBoolean("running", running);
        savedInstanceState.putBoolean("wasRunning", wasRunning);
    }
    // If the activity is paused,
    // stop the stopwatch.

    public void onPause()
    {
        stopCharging();
        super.onPause();
        wasRunning = running;
        running = false;

    }

    // If the activity is resumed,
    // start the stopwatch
    // again if it was running previously.
    @Override
    protected void onResume()
    {
        super.onResume();
        if (wasRunning) {
            running = true;
        }
    }

    // Start the stopwatch running
    // when the Start button is clicked.
    // Below method gets called
    // when the Start button is clicked.
    public void onClickStart(View view)
    {
        running = true;
    }

    // Stop the stopwatch running
    // when the Stop button is clicked.
    // Below method gets called
    // when the Stop button is clicked.
    public void onClickStop(View view)
    {
        running = false;
//        super.onStop();
//        handlerCharged.removeCallbacks((Runnable) this);
        stopCharging();
    }

    // Reset the stopwatch when
    // the Reset button is clicked.
    // Below method gets called
    // when the Reset button is clicked.
    public void onClickReset(View view)
    {
        running = false;
        seconds = 0;
    }

    // Sets the NUmber of seconds on the timer.
    // The runTimer() method uses a Handler
    // to increment the seconds and
    // update the text view.
    private void runTimer(int INTERVAL)
    {

        // Get the text view.
        final TextView timeView = (TextView)findViewById(R.id.tv_ch_hr_val);

        // Creates a new Handler
        final Handler handlerStopwatch = new Handler();

        // Call the post() method,
        // passing in a new Runnable.
        // The post() method processes
        // code without a delay,
        // so the code in the Runnable
        // will run almost immediately.
        handlerStopwatch.post(new Runnable() {
            @Override

            public void run()
            {
                int hours = seconds / 3600;
                int minutes = (seconds % 3600) / 60;
                int secs = seconds % 60;

                // Format the seconds into hours, minutes,
                // and seconds.
                String time = String.format(Locale.getDefault(),"%d:%02d:%02d", hours, minutes, secs);

                // Set the text view text.
                timeView.setText(time);

                // If running is true, increment the
                // seconds variable.
                if (running) {
                    seconds++;
                }

                // Post the code again
                // with a delay of 1 second.
                handlerStopwatch.postDelayed(this, 1000);
            }
        });

        handlerCharged = new Handler();

        handlerCharged.post(new Runnable() {
            @Override
            public void run() {
                if (!running) {
//                    wasRunning = running;
//                    handlerCharged.removeCallbacks(this);
//                    onPause();
                } else {
                    calculateCharge(INTERVAL);
                    handlerCharged.postDelayed(this, INTERVAL);
                }
            }
        });

    }

    public void calculateCharge(int INTERVAL) {
        //Toast.makeText(getApplicationContext(), , Toast.LENGTH_LONG).show();
        String chargeid = tvChargeId.getText().toString();
        Log.d("SUNDARI chargeid : ", chargeid);
        Call<DataResponse> calcch = interfaceConnection.calculateCharge (chargeid);
        calcch.enqueue(new Callback<DataResponse>() {
            @Override
            public void onResponse(Call<DataResponse> call, Response<DataResponse> response) {
                if (response.isSuccessful()){
                    Log.d("SUNDARI new charge : ", response.body().getMessage());

                    if (response.body().getMessage().equals("Not enough balance")) {
                        Log.d("SUNDARI new charge : ", response.body().getMessage());
                        running = false;
                        btnChargeStop.setVisibility(View.GONE);
                        tvInfo.setText("Pengisian dihentikan, saldo tidak mencukupi");
                        tvInfo.setVisibility(View.VISIBLE);
                        ivCircle.setImageResource(R.drawable.circle_blue);
                    } else {
                        String val = response.body().getMessage();
                        val = val.replace("[", "");
                        val = val.replace("]", "");
                        String[] separated = val.split(",");
                        tvTotalCharge.setText(separated[0]);
                        tvTotalKwh.setText(separated[1]);
                    }

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
    }

    public void stopCharging() {

        Call<DataResponse> startcharge = interfaceConnection.stopCharging(tvChargeId.getText().toString());
        startcharge.enqueue(new Callback<DataResponse>() {
            @Override
            public void onResponse(Call<DataResponse> call, Response<DataResponse> response) {
                if (response.isSuccessful()){
                    Log.d("SUNDARI charge stop : ", response.body().getMessage());
                    tvInfo.setText("Pengisian daya sudah dihentikan");
                    tvInfo.setVisibility(View.VISIBLE);
                    tvTotalKwh.setText(Integer.parseInt(tvTotalKwh.getText().toString())+1);
                    ivCircle.setImageResource(R.drawable.circle_blue);
                    Toast.makeText(getApplicationContext(),"Pengisian sudah dihentikan", Toast.LENGTH_SHORT).show();
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
    }

}