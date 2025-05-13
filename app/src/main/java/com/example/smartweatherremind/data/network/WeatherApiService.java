package com.example.smartweatherremind.data.network;

import com.example.smartweatherremind.data.model.WeatherResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherApiService {
    @GET("forecast.json")
    Call<WeatherResponse> getForecast(
            @Query("key") String apiKey,
            @Query("q") String query,
            @Query("days") int days,
            @Query("lang") String lang
    );
}
