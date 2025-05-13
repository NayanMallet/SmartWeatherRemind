package com.example.smartweatherremind.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
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
import com.example.smartweatherremind.utils.WeatherUIHelper;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.List;

public class WeatherActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private LinearLayout widgetLayout;
    private TextView cityCountryText, tempText, conditionText;
    private ImageView conditionIcon;
    private LinearLayout hourlyContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        progressBar = findViewById(R.id.progressBar);
        widgetLayout = findViewById(R.id.widgetLayout);
        cityCountryText = findViewById(R.id.cityCountryText);
        tempText = findViewById(R.id.tempText);
        conditionText = findViewById(R.id.conditionText);
        conditionIcon = findViewById(R.id.conditionIcon);
        hourlyContainer = findViewById(R.id.hourlyForecastContainer);

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
        Call<WeatherResponse> call = service.getForecast(Constants.WEATHER_API_KEY, city, 1, Constants.LANGUAGE);
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
        String query = lat + "," + lon;
        Log.d("WeatherActivity", "Query: " + query);

        WeatherApiService service = RetrofitInstance.getApiService();
        Call<WeatherResponse> call = service.getForecast(Constants.WEATHER_API_KEY, query, 1, Constants.LANGUAGE);
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

    private void displayWeather(WeatherResponse weather) {
        WeatherUIHelper.displayWeather(this, weather, cityCountryText, tempText, conditionText, conditionIcon);
        WeatherUIHelper.populateHourlyForecast(this, LayoutInflater.from(this), hourlyContainer, weather.forecast.forecastday.get(0).hour);
        widgetLayout.setVisibility(View.VISIBLE);
    }

    private void showLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        widgetLayout.setVisibility(isLoading ? View.GONE : View.VISIBLE);
    }

    private void showError() {
        Toast.makeText(this, "Impossible de récupérer la météo.", Toast.LENGTH_SHORT).show();
    }
}
