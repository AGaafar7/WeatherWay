package org.agaafar.weatherway.data

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

object WeatherCache {

    private const val PREFS_NAME = "weather_cache"
    private const val KEY_CURRENT_WEATHER = "current_weather"
    private const val KEY_FORECAST = "forecast"

    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun saveCurrentWeather(context: Context, json: String) {
        getSharedPreferences(context).edit() {
            putString(KEY_CURRENT_WEATHER, json)
        }
    }

    fun getCurrentWeather(context: Context): String? {
        return getSharedPreferences(context).getString(KEY_CURRENT_WEATHER, null)
    }

    fun saveForecast(context: Context, json: String) {
        getSharedPreferences(context).edit() {
            putString(KEY_FORECAST, json)
        }
    }

    fun getForecast(context: Context): String? {
        return getSharedPreferences(context).getString(KEY_FORECAST, null)
    }
}
