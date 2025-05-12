package com.example.smartweatherremind.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.net.URL;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;


public class WeatherActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private LinearLayout widgetLayout;
    private TextView cityCountryText, tempText, conditionText;

    private ImageView conditionIcon;


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

    private void loadWeatherIcon(String iconPath) {
        String url = "https:" + iconPath;

        new Thread(() -> {
            try {
                java.net.URL imageUrl = new java.net.URL(url);
                final android.graphics.Bitmap bitmap = android.graphics.BitmapFactory.decodeStream(imageUrl.openConnection().getInputStream());

                runOnUiThread(() -> conditionIcon.setImageBitmap(bitmap));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void fetchWeatherByCity(String city) {
        showLoading(true);

        WeatherApiService service = RetrofitInstance.getApiService();
        Call<WeatherResponse> call = service.getForecast(Constants.WEATHER_API_KEY, city, 1, Constants.LANGUAGE);
        //Call<WeatherResponse> call = service.getCurrentWeather(Constants.WEATHER_API_KEY, city, Constants.LANGUAGE);

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

        Call<WeatherResponse> call = service.getForecast(Constants.WEATHER_API_KEY, query, 1, Constants.LANGUAGE);


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

    private String trimCountryDisplay(String country) {
        if (country.length() > 20 && country.contains(" ")) {
            String[] parts = country.split(" ");
            StringBuilder initials = new StringBuilder();
            for (String part : parts) {
                if (!part.isEmpty()) {
                    initials.append(part.charAt(0));
                }
            }
            return initials.toString().toUpperCase(); // (ex: "United States of America" => "USOA")
        }
        return country;
    }

    private void displayWeather(WeatherResponse weather) {
        cityCountryText.setText(weather.location.name + ", " + trimCountryDisplay(weather.location.country));
        tempText.setText(weather.current.temp_c + "°C");
        conditionText.setText(weather.current.condition.text);
        loadWeatherIcon(weather.current.condition.icon);

        widgetLayout.setVisibility(View.VISIBLE);
        LinearLayout hourlyContainer = findViewById(R.id.hourlyForecastContainer);
        hourlyContainer.removeAllViews();

        // Récupère les 6 premières heures de la journée
        List<WeatherResponse.Hour> hourlyData = weather.forecast.forecastday.get(0).hour;

        for (int i = 0; i < 6; i++) {

            int currentHour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY);

            WeatherResponse.Hour hourData = currentHour + i < 24 ? hourlyData.get(currentHour + i) : hourlyData.get(currentHour + i - 24);

            View item = getLayoutInflater().inflate(R.layout.item_hourly_forecast, hourlyContainer, false);

            TextView hour = item.findViewById(R.id.hourText);
            ImageView icon = item.findViewById(R.id.conditionIcon);
            TextView temp = item.findViewById(R.id.tempText);

            // Extraire heure depuis date
            String time = hourData.time.split(" ")[1]; // format "2025-05-12 10:00"
            hour.setText(time); // ou format "10h" si tu veux ajouter "h"

            temp.setText((int) hourData.temp_c + "°C");

            // Charger l'icône météo
            String iconUrl = "https:" + hourData.condition.icon;
            new Thread(() -> {
                try {
                    URL url = new URL(iconUrl);
                    final Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                    runOnUiThread(() -> icon.setImageBitmap(bmp));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();

            hourlyContainer.addView(item);
        }
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
