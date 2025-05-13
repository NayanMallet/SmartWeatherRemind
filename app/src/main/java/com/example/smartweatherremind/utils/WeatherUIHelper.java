package com.example.smartweatherremind.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.smartweatherremind.R;
import com.example.smartweatherremind.data.model.WeatherResponse;

import java.net.URL;
import java.util.Calendar;
import java.util.List;

public class WeatherUIHelper {

    public static void displayWeather(Context context, WeatherResponse weather, TextView cityText, TextView tempText, TextView condText, ImageView conditionIcon) {
        cityText.setText(weather.location.name + ", " + trimCountryDisplay(weather.location.country));
        tempText.setText(weather.current.temp_c + "°C");
        condText.setText(weather.current.condition.text);
        loadWeatherIcon(context, weather.current.condition.icon, conditionIcon);
    }

    public static void populateHourlyForecast(Context context, LayoutInflater inflater, LinearLayout container, List<WeatherResponse.Hour> hours) {
        container.removeAllViews();
        int currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        for (int i = 0; i < 6; i++) {
            WeatherResponse.Hour hourData = hours.get((currentHour + i) % 24);
            View item = inflater.inflate(R.layout.item_hourly_forecast, container, false);
            TextView hour = item.findViewById(R.id.hourText);
            TextView temp = item.findViewById(R.id.tempText);
            ImageView icon = item.findViewById(R.id.conditionIcon);

            String hourFormatted = hourData.time.split(" ")[1].split(":")[0] + " h";
            if (i == 0) hourFormatted = "Maint.";
            hour.setText(hourFormatted);
            temp.setText((int) hourData.temp_c + "°C");

            loadWeatherIcon(context, hourData.condition.icon, icon);
            container.addView(item);
        }
    }

    private static void loadWeatherIcon(Context context, String iconUrl, ImageView iconView) {
        String url = "https:" + iconUrl;
        new Thread(() -> {
            try {
                Bitmap bmp = BitmapFactory.decodeStream(new URL(url).openConnection().getInputStream());
                ((Activity) context).runOnUiThread(() -> iconView.setImageBitmap(bmp));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private static String trimCountryDisplay(String country) {
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
}
