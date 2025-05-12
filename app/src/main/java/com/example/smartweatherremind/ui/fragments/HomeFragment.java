package com.example.smartweatherremind.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.FrameLayout; // ✅ Remplacer LinearLayout par FrameLayout

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.smartweatherremind.R;
import com.example.smartweatherremind.data.model.WeatherResponse;
import com.example.smartweatherremind.data.network.RetrofitInstance;
import com.example.smartweatherremind.data.network.WeatherApiService;
import com.example.smartweatherremind.utils.Constants;
import com.example.smartweatherremind.utils.PreferencesHelper;
import com.lottiefiles.dotlottie.core.widget.DotLottieAnimation;
import com.lottiefiles.dotlottie.core.model.Config;
import com.lottiefiles.dotlottie.core.util.DotLottieSource;
import com.dotlottie.dlplayer.Mode;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private ProgressBar progressBar;
    private FrameLayout widgetLayout; // ✅ Remplacé ici
    private TextView cityCountryText, tempText, conditionText;
    private DotLottieAnimation weatherLottie;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        progressBar = view.findViewById(R.id.progressBar);
        widgetLayout = view.findViewById(R.id.widgetLayout); // ✅ Cast vers FrameLayout
        cityCountryText = view.findViewById(R.id.cityCountryText);
        tempText = view.findViewById(R.id.tempText);
        conditionText = view.findViewById(R.id.conditionText);
        weatherLottie = view.findViewById(R.id.weatherLottie);

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

        loadLottie(weather.current.condition.text);
        widgetLayout.setVisibility(View.VISIBLE);
    }

    private void loadLottie(String condition) {
        String lottieUrl = getLottieUrlForCondition(condition);
        Config config = new Config.Builder()
                .autoplay(true)
                .loop(true)
                .speed(1f)
                .source(new DotLottieSource.Url(lottieUrl))
                .playMode(Mode.FORWARD)
                .build();
        weatherLottie.load(config);
    }

    private String getLottieUrlForCondition(String condition) {
        condition = condition.toLowerCase();
        if (condition.contains("sunny") || condition.contains("clear")) {
            return "https://lottie.host/9a53179a-056b-46ba-bdeb-3c90dc667f32/ij5ca4uL5d.lottie";
        } else if (condition.contains("rain") || condition.contains("shower")) {
            return "https://lottie.host/bb5bd8ba-9e13-4e21-b56e-67debe5f42ae/YyU0rVGcdd.lottie";
        } else if (condition.contains("cloud") || condition.contains("overcast")) {
            return "https://lottie.host/62f9fa6f-97f7-4748-8d3b-f59b0b7e7c46/ikxqIYBUFM.lottie";
        } else if (condition.contains("wind")) {
            return "https://lottie.host/1951fc25-a1a1-4f1d-a515-385bcd1e4e57/hYUKrLKpNf.lottie";
        } else if (condition.contains("snow") || condition.contains("sleet") || condition.contains("blizzard")) {
            return "https://lottie.host/e3eed585-8879-4f2a-bf6f-7612b56baea9/N87yEEqmN4.lottie";
        } else if (condition.contains("thunder") || condition.contains("storm")) {
            return "https://lottie.host/44c14df6-21b2-427e-9648-1515c1782c6f/DNQCDE8bKO.lottie";
        } else {
            return "https://lottie.host/62f9fa6f-97f7-4748-8d3b-f59b0b7e7c46/ikxqIYBUFM.lottie"; // fallback cloud
        }
    }

    private void showLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        widgetLayout.setVisibility(isLoading ? View.GONE : View.VISIBLE);
    }

    private void showError() {
        Toast.makeText(requireContext(), "Impossible de récupérer la météo.", Toast.LENGTH_SHORT).show();
    }
}
