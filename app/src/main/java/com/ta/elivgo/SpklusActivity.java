package com.ta.elivgo;

import static android.widget.Toast.LENGTH_SHORT;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.google.android.gms.maps.model.LatLng;
import com.ta.elivgo.adapter.SpkluAdapter;
import com.ta.elivgo.model.ListSpklu;
import com.ta.elivgo.response.DataResponse;
import com.ta.elivgo.rest.ApiConnection;
import com.ta.elivgo.rest.InterfaceConnection;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SpklusActivity extends AppCompatActivity implements LocationListener {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    //    private RecyclerView.Adapter spklusAdapter;
    protected LocationManager locationManager;
    protected LocationListener locationListener;
    protected String latitude, longitude;
    protected boolean isDefault = true;
    protected boolean gps_enabled, network_enabled;

    private List<ListSpklu> list = new ArrayList<>();
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1;
    public LatLng currentLoc;
    public Location myLocation;
    private SpkluAdapter spklusAdapter;
    Geocoder geocoder;
    InterfaceConnection interfaceConnection;
    TextView tvlatlong, tvloc;
    EditText etSearch;

    private static final int LOCATIONS_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spklus);

        tvlatlong = findViewById(R.id.tv_spklus_coord);
        tvloc = findViewById(R.id.tv_spklus_loc);
        etSearch = findViewById(R.id.et_spklus_search);
        etSearch.setText("");

        recyclerView = (RecyclerView) findViewById(R.id.rv_spklu);
        // Removes blinks
        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        interfaceConnection = ApiConnection.getClient().create(InterfaceConnection.class);

        myLocation = new Location("A");
        getDefaultLocation();

        locationManager = (LocationManager) getSystemService(getApplicationContext().LOCATION_SERVICE);
        // getting GPS status
        gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        // getting network status
        network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (gps_enabled) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION},
                        LOCATIONS_CODE);
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES,MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
        } else if (network_enabled) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES,MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
//            myLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//            Toast.makeText(getApplicationContext(), String.valueOf(myLocation.getLatitude()), LENGTH_SHORT).show();
            if (locationManager != null) {
                if (myLocation != null) {
//                    latitude = myLocation.getLatitude();
//                    longitude = myLocation.getLongitude();
                }
            } else {
                getDefaultLocation();
            }
        } else if (gps_enabled == false) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage("GPS is disabled in your device. Would you like to enable it?")
                    .setCancelable(false)
                    .setPositiveButton("Goto Settings Page To Enable GPS",
                            new DialogInterface.OnClickListener(){
                                public void onClick(DialogInterface dialog, int id){
                                    Intent callGPSSettingIntent = new Intent(
                                            android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                    startActivity(callGPSSettingIntent);
                                }
                            });
            alertDialogBuilder.setNegativeButton("Cancel",
                    new DialogInterface.OnClickListener(){
                        public void onClick(DialogInterface dialog, int id){
                            dialog.cancel();
                            loadRecyclerViewItem();
                        }
                    });
            AlertDialog alert = alertDialogBuilder.create();
            alert.show();
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES,MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
        } else {
            loadRecyclerViewItem();
        }

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d("SUNDARI ", s.toString());
                spklusAdapter.getFilter().filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
        LocationListener.super.onProviderEnabled(provider);
        Log.d("Sundari Latitude","enable");
    }

    @Override
    public void onLocationChanged (@NonNull Location location){
        tvlatlong.setText(location.getLatitude() + ", " + location.getLongitude());

        currentLoc = new LatLng(location.getLatitude(), location.getLongitude());
        myLocation.setLongitude(location.getLongitude());
        myLocation.setLatitude(location.getLatitude());

        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());
        try {
            String sPlace;
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            String address = addresses.get(0).getAddressLine(0);
            String city = addresses.get(0).getAddressLine(1);
            String country = addresses.get(0).getAddressLine(2);

            String[] splitAddress = address.split(",");
            sPlace = splitAddress[0] + "\n";
            if (city != null && !city.isEmpty()) {
                String[] splitCity = city.split(",");
                sPlace += splitCity[0];
            }
            isDefault = false;
            tvloc.setText(address);

        } catch (IOException e) {
            e.printStackTrace();
        }
        loadRecyclerViewItem();
    }

    public void getDefaultLocation() {
        Call<DataResponse> getIntv = interfaceConnection.getConfiguration("default_location");
        getIntv.enqueue(new Callback<DataResponse>() {
            @Override
            public void onResponse(Call<DataResponse> call, Response<DataResponse> response) {
                if (response.isSuccessful()){
                    String loc;
                    Double lat = 0.0, lng = 0.0;
                    loc = response.body().getMessage().toString();
                    String[] separated = loc.split(";");
                    lat = Double.parseDouble(separated[0].trim());
                    lng = Double.parseDouble(separated[1].trim());
                    Log.d("SUNDARI", lng.toString());
                    myLocation.setLongitude(lat);
                    myLocation.setLongitude(lng);
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
                Toast.makeText(getApplicationContext(), "Terjadi kesalahan", LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onProviderDisabled (@NonNull String provider){
        LocationListener.super.onProviderDisabled(provider);
        Log.d("Latitude", "disable");
    }

    @Override
    public void onStatusChanged (String provider,int status, Bundle extras){
        LocationListener.super.onStatusChanged(provider, status, extras);
        Log.d("Latitude", "status");
    }

    private void loadRecyclerViewItem () {
        spklusAdapter = new SpkluAdapter(getApplicationContext());
        Log.d("SUNDARI Location ",String.valueOf(myLocation.getLatitude()));
//        listSpklu = new ListSpklu("SPKLU PLN UID Jakarta Raya 2", "01", "Jl. M.I. Ridwan Rais No. 1, Gambir","900");
//        list.add(listSpklu);
//        listSpklu = new ListSpklu("SPKLU PLN UID Jakarta Raya 3", "01", "Jl. M.I. Ridwan Rais No. 1, Gambir","900");
//        list.add(listSpklu);
//        listSpklu = new ListSpklu("SPKLU PLN UID Jakarta Raya 4", "01", "Jl. M.I. Ridwan Rais No. 1, Gambir","900");
//        list.add(listSpklu);
//        listSpklu = new ListSpklu("SPKLU PLN UID Jakarta Raya 5", "01", "Jl. M.I. Ridwan Rais No. 1, Gambir","900");
//        list.add(listSpklu);

        //Call<DataResponse> getRespSpklus = interfaceConnection.getAllSpklus();
        Call<DataResponse> getRespSpklus = interfaceConnection.getAllSpklusDist(myLocation.getLatitude(), myLocation.getLongitude());
        getRespSpklus.enqueue(new Callback<DataResponse>() {
            @Override
            public void onResponse(Call<DataResponse> call, Response<DataResponse> response) {
                if (response.isSuccessful()) {
                    List<ListSpklu> spklus = response.body().getAllSpklusDist();
//                    list.addAll(spklus);
                    ListSpklu listSpklu;
                    for (int i = 0; i < spklus.size(); i++) {
                        Log.i("val is ", spklus.get(i).getId());
                        listSpklu = new ListSpklu(spklus.get(i).getId(), spklus.get(i).getName(), spklus.get(i).getAddress(), spklus.get(i).getCity(), spklus.get(i).getOwner(), spklus.get(i).getCapacity(), spklus.get(i).getLatitude(), spklus.get(i).getLongitude(), spklus.get(i).getDistance(), spklus.get(i).getCode(), spklus.get(i).getAcStatus(), spklus.get(i).getDcStatus());
                        list.add(listSpklu);
                    }

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
                spklusAdapter.updateSpkluList(list, myLocation, isDefault);
            }

            @Override
            public void onFailure(Call<DataResponse> call, Throwable t) {
                Log.d("Error Jaringan", "disini");
                t.printStackTrace();
                Log.d("here", "here", t);
                Toast.makeText(getApplicationContext(), "Terjadi kesalahan", LENGTH_SHORT).show();
            }
        });

        recyclerView.setAdapter(spklusAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case LOCATIONS_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                    // User Allows Permission access


                } else {

                    // User denies Permission access

                }
        }
    }
}