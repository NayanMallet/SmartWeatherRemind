package com.example.smartweatherremind.ui.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
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
import com.lottiefiles.dotlottie.core.widget.DotLottieAnimation;

import com.example.smartweatherremind.utils.PreferencesHelper;
import com.lottiefiles.dotlottie.core.model.Config;
import com.lottiefiles.dotlottie.core.util.DotLottieSource;
import com.dotlottie.dlplayer.Mode;

import java.net.URL;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private ProgressBar progressBar;
    private FrameLayout widgetLayout;
    private TextView cityCountryText, tempText, conditionText;
    private LinearLayout hourlyContainer;

    private DotLottieAnimation weatherLottie;

    private String lastLottieUrl = null;

    private double latitude = 48.8566; // par défaut : Paris
    private double longitude = 2.3522;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        progressBar = view.findViewById(R.id.progressBar);
        widgetLayout = view.findViewById(R.id.widgetLayout); // Pas besoin de cast, c’est une View
        cityCountryText = view.findViewById(R.id.cityCountryText);
        tempText = view.findViewById(R.id.tempText);
        conditionText = view.findViewById(R.id.conditionText);
        hourlyContainer = view.findViewById(R.id.hourlyForecastContainer);
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

    @Override
    public void onResume() {
        super.onResume();
        double latitude = PreferencesHelper.getLatitude(requireContext());
        double longitude = PreferencesHelper.getLongitude(requireContext());

        if (latitude != -1 && longitude != -1) {
            fetchWeatherByLocation(latitude, longitude);
        }
    }


    private void fetchWeatherByLocation(double lat, double lon) {
        showLoading(true);
        WeatherApiService service = RetrofitInstance.getApiService();
        String query = lat + "," + lon;
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
        cityCountryText.setText(weather.location.name + ", " + trimCountryDisplay(weather.location.country));
        tempText.setText(weather.current.temp_c + "°C");
        conditionText.setText(weather.current.condition.text);
        loadLottie(weather.current.condition.text);
        widgetLayout.setVisibility(View.VISIBLE);
        hourlyContainer.removeAllViews();

        List<WeatherResponse.Hour> hourlyData = weather.forecast.forecastday.get(0).hour;
        int currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);

        for (int i = 0; i < 6; i++) {
            WeatherResponse.Hour hourData = (currentHour + i < 24) ? hourlyData.get(currentHour + i)
                    : hourlyData.get(currentHour + i - 24);

            View item = getLayoutInflater().inflate(R.layout.item_hourly_forecast, hourlyContainer, false);

            TextView hour = item.findViewById(R.id.hourText);
            ImageView icon = item.findViewById(R.id.conditionIcon);
            TextView temp = item.findViewById(R.id.tempText);

            hour.setText(hourData.time.split(" ")[1]);
            temp.setText((int) hourData.temp_c + "°C");

            String iconUrl = "https:" + hourData.condition.icon;
            new Thread(() -> {
                try {
                    URL url = new URL(iconUrl);
                    Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                    requireActivity().runOnUiThread(() -> icon.setImageBitmap(bmp));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();

            hourlyContainer.addView(item);
        }
    }

    private String trimCountryDisplay(String country) {
        if (country.length() > 20 && country.contains(" ")) {
            String[] parts = country.split(" ");
            StringBuilder initials = new StringBuilder();
            for (String part : parts) {
                if (!part.isEmpty()) initials.append(part.charAt(0));
            }
            return initials.toString().toUpperCase();
        }
        return country;
    }

    private void loadLottie(String condition) {
        lastLottieUrl = getLottieUrlForCondition(condition); // ✅ Sauvegarde
        loadLottieFromUrl(lastLottieUrl); // ✅ Charge depuis l'URL sauvegardée
    }

    private void loadLottieFromUrl(String url) {
        Config config = new Config.Builder()
                .autoplay(true)
                .loop(true)
                .speed(1f)
                .source(new DotLottieSource.Url(url))
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
