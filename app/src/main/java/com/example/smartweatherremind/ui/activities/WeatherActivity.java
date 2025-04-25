package com.example.smartweatherremind.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartweatherremind.R;
import com.example.smartweatherremind.data.model.WeatherResponse;
import com.example.smartweatherremind.data.network.RetrofitInstance;
import com.example.smartweatherremind.data.network.WeatherApiService;
import com.example.smartweatherremind.utils.Constants;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeatherActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private LinearLayout resultLayout;
    private TextView cityText, countryText, tempText, conditionText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        Toast.makeText(this, "WeatherActivity lancé", Toast.LENGTH_SHORT).show();

        progressBar = findViewById(R.id.progressBar);
        resultLayout = findViewById(R.id.resultLayout);
        cityText = findViewById(R.id.cityText);
        countryText = findViewById(R.id.countryText);
        tempText = findViewById(R.id.tempText);
        conditionText = findViewById(R.id.conditionText);

        Intent intent = getIntent();

        String city = intent.getStringExtra("city");
        double latitude = intent.getDoubleExtra("latitude", -1);
        double longitude = intent.getDoubleExtra("longitude", -1);

        if (city != null && !city.isEmpty()) {
            fetchWeatherByCity(city);
        } else if (latitude != -1 && longitude != -1) {
            fetchWeatherByLocation(latitude, longitude);
        } else {
            Toast.makeText(this, "Aucune information de localisation reçue.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void fetchWeatherByCity(String city) {
        showLoading(true);

        WeatherApiService service = RetrofitInstance.getApiService();
        Call<WeatherResponse> call = service.getCurrentWeather(Constants.WEATHER_API_KEY, city, Constants.LANGUAGE);

        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    displayWeather(response.body());
                } else {
                    showError();
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                showLoading(false);
                showError();
            }
        });
    }

    private void fetchWeatherByLocation(double lat, double lon) {
        showLoading(true);

        WeatherApiService service = RetrofitInstance.getApiService();
        String query = lat + "," + lon;

        Log.d("WeatherActivity", "Query: " + query);

        Call<WeatherResponse> call = service.getCurrentWeather(Constants.WEATHER_API_KEY, query, Constants.LANGUAGE);

        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("WeatherActivity", "Response: " + response.body());
                    displayWeather(response.body());
                } else {
                    Log.d("WeatherActivity", "Response error: " + response.errorBody());
                    showError();
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                showLoading(false);
                Log.d("WeatherActivity", "Failure: " + t.getMessage());
                showError();
            }
        });
    }

    private void displayWeather(WeatherResponse weather) {
        cityText.setText(weather.location.name);
        countryText.setText(weather.location.country);
        tempText.setText(weather.current.temp_c + "°C");
        conditionText.setText(weather.current.condition.text);

        resultLayout.setVisibility(View.VISIBLE);
    }

    private void showLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        resultLayout.setVisibility(isLoading ? View.GONE : View.VISIBLE);
    }

    private void showError() {
        Toast.makeText(this, "Impossible de récupérer la météo.", Toast.LENGTH_SHORT).show();
    }
}
