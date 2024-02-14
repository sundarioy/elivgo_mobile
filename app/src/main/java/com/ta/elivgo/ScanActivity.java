package com.ta.elivgo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.ta.elivgo.response.DataResponse;
import com.ta.elivgo.rest.ApiConnection;
import com.ta.elivgo.rest.InterfaceConnection;

import org.json.JSONObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScanActivity extends AppCompatActivity {

    private SurfaceView surfaceView;
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    private static final int REQUEST_CAMERA_PERMISSION = 201;
    private ToneGenerator toneGen;
    private TextView barcodeText, tvName, tvAddress, tvInfo;
    private String barcodeData;
    public Button btnStart;
    String qrcode,id,name,address;
    InterfaceConnection interfaceConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        interfaceConnection = ApiConnection.getClient().create(InterfaceConnection.class);
        toneGen = new ToneGenerator(AudioManager.STREAM_MUSIC,100);
        surfaceView = findViewById(R.id.sv_sc_qr);
        barcodeText = findViewById(R.id.tv_sc_lbl_1);
        btnStart = findViewById(R.id.btn_start_charging);
        tvName = findViewById(R.id.tv_sc_spklu_name);
        tvAddress = findViewById(R.id.tv_sc_spklu_addr);
        tvInfo = findViewById(R.id.tv_sc_info);
        tvName.setText(getIntent().getStringExtra("name"));
        tvAddress.setText(getIntent().getStringExtra("address"));

        btnStart.setClickable(false);
        btnStart.setAllowClickWhenDisabled(false);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), ChargingActivity.class);
                intent.putExtra("code",qrcode);
                intent.putExtra("id", getIntent().getStringExtra("id"));
                intent.putExtra("name",  getIntent().getStringExtra("name"));
                intent.putExtra("address", getIntent().getStringExtra("address"));
                startActivity(intent);
            }
        });
        detectCode();

    }

     public void detectCode() {
        barcodeDetector = new BarcodeDetector.Builder(this).setBarcodeFormats(Barcode.ALL_FORMATS).build();
        cameraSource = new CameraSource.Builder(this,barcodeDetector).setRequestedPreviewSize(1920,1080).setAutoFocusEnabled(true).build();
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
                try {
                    if (ActivityCompat.checkSelfPermission(ScanActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        cameraSource.start(surfaceView.getHolder());
                    } else {
                        ActivityCompat.requestPermissions(ScanActivity.this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {

            }

            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {
                cameraSource.stop();
            }
        });

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(@NonNull Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if(barcodes.size() != 0 ) {
                    barcodeText.post(new Runnable() {
                        @Override
                        public void run() {
                            if (barcodes.valueAt(0).email != null) {
                                barcodeText.removeCallbacks(null);
                                barcodeData = barcodes.valueAt(0).email.address;
                                barcodeText.setText(barcodeData);
//                                toneGen.startTone(ToneGenerator.TONE_CDMA_PIP, 150);
                            } else {
                                barcodeData = barcodes.valueAt(0).displayValue;
                                barcodeText.setText(barcodeData);
                                qrcode = barcodeData;
//                                toneGen.startTone(ToneGenerator.TONE_CDMA_PIP, 150);
                            }
                            cekChargeValidity("1", barcodeData);
                        }
                    });

                }

            }
        });
     }

     public void showScan(String code) {
         qrcode = code;
         btnStart.setClickable(true);
         btnStart.setVisibility(View.VISIBLE);
         btnStart.setBackgroundColor(Color.parseColor("#F85F6A"));
         Log.d("SUNDARI - SCAN ", code);
     }

    public void startScan(String code) {

    }

     public void cekChargeValidity(String id, String barcodeData) {
//         Log.d("SUNDARI get validity ", id+code);
         Call<DataResponse> checkValidity = interfaceConnection.cekChargeValidity(id, barcodeData);
         checkValidity.enqueue(new Callback<DataResponse>() {
             @Override
             public void onResponse(Call<DataResponse> call, Response<DataResponse> response) {
                 if (response.isSuccessful()){
                     String str;
                     str = response.body().getMessage().toString().replace("[","").replace("]","");
                     String[] separated = str.split(";");
//                     Log.d("SUNDARI get validity ", separated[0]);
                     tvName.setText(separated[4]);
                     tvAddress.setText(separated[3]);
                     if (String.valueOf(separated[0]).equals("1")) {
                         Log.d("SUNDARI get validity ", barcodeData);
                         showScan(barcodeData);
                     } else {
                         tvInfo.setText("Saldo tidak mencukupi untuk pengisian minimum pada konektor yang dipilih");
                     }
                 } else {
                     try {
                         JSONObject jObjError = new JSONObject(response.errorBody().string());
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
         cameraSource.stop();
     }
}