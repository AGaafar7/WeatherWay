package org.agaafar.weatherway.data

import android.util.Log
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Executors

object WeatherApi {

    private const val API_KEY = "7Y4ZYHNRBNWT486BEHCZQ7KNG"
    private const val BASE_URL = "https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/"

    private val executorService = Executors.newSingleThreadExecutor()

    fun getCurrentWeather(latitude: Double, longitude: Double, callback: (String?) -> Unit) {
        val urlString = "${BASE_URL}${latitude},${longitude}?key=$API_KEY"
        fetchWeatherData(urlString, callback)
    }

    fun getForecast(latitude: Double, longitude: Double, startDate: String, endDate: String, callback: (String?) -> Unit) {
        val urlString = "${BASE_URL}${latitude},${longitude}/$startDate/$endDate?key=$API_KEY"
        fetchWeatherData(urlString, callback)
    }

    private fun fetchWeatherData(urlString: String, callback: (String?) -> Unit) {
        executorService.execute {
            val result = makeGetRequest(urlString)
            callback(result)
        }
    }

    private fun makeGetRequest(urlString: String): String? {
        return try {
            val url = URL(urlString)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connect()

            val responseCode = connection.responseCode
            Log.d("WeatherApi", "Response Code: $responseCode")

            if (responseCode == HttpURLConnection.HTTP_OK) {
                val reader = BufferedReader(InputStreamReader(connection.inputStream))
                val response = StringBuilder()
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    response.append(line)
                }
                response.toString()
            } else {
                Log.e("WeatherApi", "Error response code: $responseCode")
                null
            }
        } catch (e: Exception) {
            Log.e("WeatherApi", "Error making GET request: ${e.message}")
            null
        }
    }
}
