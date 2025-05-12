package com.example.smartweatherremind.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.smartweatherremind.R;
import com.example.smartweatherremind.data.model.WeatherResponse;
import com.example.smartweatherremind.data.network.RetrofitInstance;
import com.example.smartweatherremind.data.network.WeatherApiService;
import com.example.smartweatherremind.utils.Constants;
import com.example.smartweatherremind.utils.PreferencesHelper;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private ProgressBar progressBar;
    private LinearLayout widgetLayout;
    private TextView cityCountryText, tempText, conditionText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        progressBar = view.findViewById(R.id.progressBar);
        widgetLayout = view.findViewById(R.id.widgetLayout);
        cityCountryText = view.findViewById(R.id.cityCountryText);
        tempText = view.findViewById(R.id.tempText);
        conditionText = view.findViewById(R.id.conditionText);

        double latitude = PreferencesHelper.getLatitude(requireContext());
        double longitude = PreferencesHelper.getLongitude(requireContext());

        if (latitude != -1 && longitude != -1) {
            fetchWeatherByLocation(latitude, longitude);
        } else {
            Toast.makeText(requireContext(), "Aucune localisation enregistrée.", Toast.LENGTH_SHORT).show();
        }

        return view;
    }

    private void fetchWeatherByLocation(double lat, double lon) {
        showLoading(true);

        WeatherApiService service = RetrofitInstance.getApiService();
        String query = lat + "," + lon;

        Call<WeatherResponse> call = service.getCurrentWeather(Constants.WEATHER_API_KEY, query, Constants.LANGUAGE);

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
        cityCountryText.setText(weather.location.name + ", " + weather.location.country);
        tempText.setText(weather.current.temp_c + "°C");
        conditionText.setText(weather.current.condition.text);

        widgetLayout.setVisibility(View.VISIBLE);
    }

    private void showLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        widgetLayout.setVisibility(isLoading ? View.GONE : View.VISIBLE);
    }

    private void showError() {
        Toast.makeText(requireContext(), "Impossible de récupérer la météo.", Toast.LENGTH_SHORT).show();
    }
}
