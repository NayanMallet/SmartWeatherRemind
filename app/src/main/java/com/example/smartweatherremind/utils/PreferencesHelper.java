package com.example.smartweatherremind.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesHelper {
    private static final String PREFS_NAME = "user_prefs";
    private static final String KEY_LATITUDE = "latitude";
    private static final String KEY_LONGITUDE = "longitude";

    public static void saveLocation(Context context, double lat, double lon) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putLong(KEY_LATITUDE, Double.doubleToRawLongBits(lat))
                .putLong(KEY_LONGITUDE, Double.doubleToRawLongBits(lon))
                .apply();
    }

    public static double getLatitude(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return Double.longBitsToDouble(prefs.getLong(KEY_LATITUDE, Double.doubleToRawLongBits(-1)));
    }

    public static double getLongitude(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return Double.longBitsToDouble(prefs.getLong(KEY_LONGITUDE, Double.doubleToRawLongBits(-1)));
    }

    public static boolean hasSavedLocation(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.contains(KEY_LATITUDE) && prefs.contains(KEY_LONGITUDE);
    }

    public static void clearLocation(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit()
                .remove(KEY_LATITUDE)
                .remove(KEY_LONGITUDE)
                .apply();
    }

}
