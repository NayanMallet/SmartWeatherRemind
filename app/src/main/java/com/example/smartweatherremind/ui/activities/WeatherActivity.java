package com.example.smartweatherremind.ui.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

    private EditText cityInput;
    private TextView weatherResult;
    private Button fetchWeatherBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        cityInput = findViewById(R.id.editCity);
        weatherResult = findViewById(R.id.weatherResult);
        fetchWeatherBtn = findViewById(R.id.btnFetch);

        fetchWeatherBtn.setOnClickListener(view -> {
            String city = cityInput.getText().toString().trim();
            if (!city.isEmpty()) {
                fetchWeather(city);
            } else {
                Toast.makeText(this, "Entrez une ville", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchWeather(String city) {
        WeatherApiService service = RetrofitInstance.getApiService();

        Call<WeatherResponse> call = service.getCurrentWeather(
                Constants.WEATHER_API_KEY,
                city,
                Constants.LANGUAGE
        );

        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    WeatherResponse weather = response.body();
                    String result = "Ville : " + weather.location.name + "\n"
                            + "Pays : " + weather.location.country + "\n"
                            + "Température : " + weather.current.temp_c + "°C\n"
                            + "Condition : " + weather.current.condition.text;
                    weatherResult.setText(result);
                } else {
                    weatherResult.setText("Aucune donnée trouvée.");
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                weatherResult.setText("Erreur : " + t.getMessage());
            }
        });
    }
}
