package org.agaafar.weatherway.models

data class CurrentWeather(
    val temperature: Double,
    val description: String,
    val humidity: Int,
    val windSpeed: Double,
    val icon: String
)
