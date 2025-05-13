package com.example.smartweatherremind.data.model;

import java.util.List;

public class WeatherResponse {

    public Location location;
    public Current current;
    public Forecast forecast;

    public static class Location {
        public String name;
        public String country;
        public double lat;
        public double lon;
    }

    public static class Current {
        public float temp_c;
        public Condition condition;
    }

    public static class Forecast {
        public List<ForecastDay> forecastday;
    }

    public static class ForecastDay {
        public String date;
        public List<Hour> hour;
    }

    public static class Hour {
        public String time;
        public double temp_c;
        public Condition condition;
    }

    public static class Condition {
        public String text;
        public String icon;
    }
}
