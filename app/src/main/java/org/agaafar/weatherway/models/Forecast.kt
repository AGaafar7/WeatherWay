package org.agaafar.weatherway.models

data class Forecast(
    val dateTime: String,
    val temperature: Double,
    val description: String,
    val icon: String
)