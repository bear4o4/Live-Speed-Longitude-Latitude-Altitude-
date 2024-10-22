package com.example.location;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 200;
    private LocationManager locationManager;
    private TextView textView;
    private Button button;
    private RadioButton radioButtonkmh;
    private RadioButton radioButtonms;
    private boolean isSpeedInKmh = true;
    private LocationManager lm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.textView);
        button = findViewById(R.id.buttonid);
        radioButtonkmh = findViewById(R.id.radioButtonid);
        radioButtonms = findViewById(R.id.radioButtonn2);

        radioButtonkmh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isSpeedInKmh = true;
                radioButtonms.setChecked(false);
            }
        });

        radioButtonms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isSpeedInKmh = false;
                radioButtonkmh.setChecked(false);
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkLocationPermission();
            }
        });
    }

    private void checkLocationPermission() {
        int coarse = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        int fine = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

        if (fine != PackageManager.PERMISSION_GRANTED && coarse != PackageManager.PERMISSION_GRANTED) {
            String[] thepermissions = new String[2];
            thepermissions[0] = Manifest.permission.ACCESS_FINE_LOCATION;
            thepermissions[1] = Manifest.permission.ACCESS_COARSE_LOCATION;
            this.requestPermissions(thepermissions, PERMISSION_REQUEST_CODE);
        } else {
            startLocationUpdates();
        }
    }

    private void startLocationUpdates() {
        lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (lm != null) {
            Location l = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (l == null) {
                l = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }

            if (l != null) {

                updateLocationUI(l);
            }
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        if (location != null) {
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();
                            double altitude = location.getAltitude();
                            double speed = location.getSpeed();

                            if (isSpeedInKmh) {
                                speed = speed * 3.6;
                            }

                            String locationData = String.format("Latitude: %.2f\nLongitude: %.2f\nAltitude: %.2f\nSpeed: %.2f %s", latitude, longitude, altitude, speed, isSpeedInKmh ? "km/h" : "m/s");

                            textView.setText(locationData);
                            Log.d("LocationData", "Location: " + locationData);
                        } else {
                            textView.setText("Location not available");
                        }
                    }
                });
            }
        }
    }

    private void updateLocationUI(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        double altitude = location.getAltitude();
        double speed = location.getSpeed();


        if (isSpeedInKmh) {
            speed = speed * 3.6;
        }


        String locationData = String.format("Latitude: %.2f\nLongitude: %.2f\nAltitude: %.2f\nSpeed: %.2f %s",
                latitude, longitude, altitude, speed, isSpeedInKmh ? "km/h" : "m/s");

        textView.setText(locationData);
        Log.d("LocationData", "Location: " + locationData);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates();
            } else {
                textView.setText("Permission denied.");
            }
        }
    }
}