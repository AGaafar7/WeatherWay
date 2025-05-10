package org.agaafar.weatherway.data

import android.util.Log
import org.agaafar.weatherway.models.CurrentWeather
import org.agaafar.weatherway.models.Forecast
import org.json.JSONObject

object WeatherParser {

    fun parseCurrentWeather(json: String): CurrentWeather? {
        return try {
            val jsonObject = JSONObject(json)
            val currentConditions = jsonObject.getJSONObject("currentConditions")

            CurrentWeather(
                temperature = currentConditions.getDouble("temp"),
                description = currentConditions.optString("conditions", "No description"),
                humidity = currentConditions.optInt("humidity", -1),
                windSpeed = currentConditions.optDouble("windspeed", Double.NaN),
                icon = currentConditions.optString("icon", "")
            )
        } catch (e: Exception) {
            Log.e("WeatherParser", "Error parsing current weather: ${e.message}")
            null
        }
    }

    fun parseForecast(json: String): List<Forecast>? {
        return try {
            val jsonObject = JSONObject(json)
            val days = jsonObject.getJSONArray("days")
            val forecasts = mutableListOf<Forecast>()

            for (i in 0 until days.length()) {
                val day = days.getJSONObject(i)

                forecasts.add(
                    Forecast(
                        dateTime = day.getString("datetime"),
                        temperature = day.getDouble("temp"),
                        description = day.optString("description", "No description"),
                        icon = day.optString("icon", "")
                    )
                )
            }
            forecasts
        } catch (e: Exception) {
            Log.e("WeatherParser", "Error parsing forecast: ${e.message}")
            null
        }
    }
}
