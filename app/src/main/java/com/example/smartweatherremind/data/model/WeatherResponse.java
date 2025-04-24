package com.example.smartweatherremind.data.model;

public class WeatherResponse {
    public Location location;
    public Current current;

    public static class Location {
        public String name;
        public String country;
    }

    public static class Current {
        public float temp_c;
        public Condition condition;
    }

    public static class Condition {
        public String text;
        public String icon;
    }
}
