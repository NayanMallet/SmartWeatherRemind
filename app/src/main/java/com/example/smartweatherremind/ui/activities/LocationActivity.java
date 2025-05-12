package com.example.smartweatherremind.ui.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.smartweatherremind.ui.activities.WeatherActivity;
import com.example.smartweatherremind.utils.PreferencesHelper;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;


public class LocationActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;
    private FusedLocationProviderClient fusedClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fusedClient = LocationServices.getFusedLocationProviderClient(this);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            getLastLocation();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permission non accordée pour accéder à la position.", Toast.LENGTH_SHORT).show();
            return;
        }

        fusedClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                launchWeatherActivity(location);
            } else {
                // Fallback : demander une nouvelle localisation
                LocationRequest locationRequest = LocationRequest.create()
                        .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                        .setInterval(1000)
                        .setNumUpdates(1);

                fusedClient.requestLocationUpdates(locationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(@NonNull LocationResult locationResult) {
                        Location lastLocation = locationResult.getLastLocation();
                        if (lastLocation != null) {
                            launchWeatherActivity(lastLocation);
                        } else {
                            Toast.makeText(LocationActivity.this, "Localisation impossible.", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                }, getMainLooper());
            }
        });
    }

    private void launchWeatherActivity(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        // Save location
        PreferencesHelper.saveLocation(this, latitude, longitude);

        Intent intent = new Intent(this, WeatherActivity.class);
        intent.putExtra("latitude", latitude);
        intent.putExtra("longitude", longitude);
        Log.d("LocationActivity", "Latitude: " + latitude + ", Longitude: " + longitude);
        startActivity(intent);
        finish();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE &&
                grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getLastLocation();
        } else {
            Toast.makeText(this, "Permission refusée", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
