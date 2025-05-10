package org.agaafar.weatherway.ui.screens

import android.content.Context
import android.content.res.Configuration
import android.location.Location
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.agaafar.weatherway.R
import org.agaafar.weatherway.data.WeatherApi
import org.agaafar.weatherway.data.WeatherCache
import org.agaafar.weatherway.data.WeatherParser
import org.agaafar.weatherway.location.LocationHelper
import org.agaafar.weatherway.models.CurrentWeather
import org.agaafar.weatherway.utils.NetworkUtils

@Composable
fun CurrentWeatherScreen(
    locationHelper: LocationHelper,
    onNavigateToForecast: () -> Unit,
    context: Context
) {
    val configuration = LocalConfiguration.current
    val isPortrait = configuration.orientation == Configuration.ORIENTATION_PORTRAIT

    var weather by remember { mutableStateOf<CurrentWeather?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(true) }
    var isOffline by remember { mutableStateOf(false) }

    fun refreshWeather() {
        loading = true
        isOffline = !NetworkUtils.isNetworkAvailable(context)

        if (isOffline) {
            // Load cached weather if offline
            val cachedWeatherJson = WeatherCache.getCurrentWeather(context)
            if (cachedWeatherJson != null) {
                weather = WeatherParser.parseCurrentWeather(cachedWeatherJson)
                errorMessage = if (weather == null) "No cached weather data available" else null
            } else {
                errorMessage = "No cached weather data available"
            }
            loading = false
        } else {
            locationHelper.getCurrentLocation { location: Location? ->
                if (location != null) {
                    WeatherApi.getCurrentWeather(location.latitude, location.longitude) { result ->
                        if (result != null) {
                            weather = WeatherParser.parseCurrentWeather(result)
                            WeatherCache.saveCurrentWeather(context, result) // Cache the weather
                            errorMessage = if (weather == null) "Unable to load weather data" else null
                        } else {
                            errorMessage = "Error fetching weather data"
                        }
                        loading = false
                    }
                } else {
                    errorMessage = "Unable to get location"
                    loading = false
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        refreshWeather()
    }

    if (loading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFE3F2FD)),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Color(0xFF2196F3))
        }
    } else if (isOffline) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFFF3E0)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "No internet connection detected.",
                    color = Color(0xFFD32F2F),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Please enable your internet connection.",
                    color = Color(0xFF757575),
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { refreshWeather() }) {
                    Text("Retry")
                }
            }
        }
    } else if (errorMessage != null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFFEBEE)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = errorMessage ?: "Unknown error",
                color = Color(0xFFD32F2F),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    } else {
        weather?.let {
            WeatherContent(
                weather = it,
                onRefresh = { refreshWeather() },
                onNavigateToForecast = onNavigateToForecast,
                isPortrait = isPortrait
            )
        }
    }
}

@Composable
fun WeatherContent(
    weather: CurrentWeather,
    onRefresh: () -> Unit,
    onNavigateToForecast: () -> Unit,
    isPortrait: Boolean
) {
    if (isPortrait) {
        // Portrait Layout
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFE3F2FD))
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                WeatherDetails(weather, onRefresh, onNavigateToForecast)
            }
        }
    } else {
        // Landscape Layout
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFE3F2FD))
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            WeatherDetails(weather, onRefresh, onNavigateToForecast)
        }
    }
}

@Composable
fun WeatherDetails(
    weather: CurrentWeather,
    onRefresh: () -> Unit,
    onNavigateToForecast: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Weather Icon
        Image(
            painter = painterResource(id = R.drawable.cloudy), // Replace with actual weather icon
            contentDescription = "Weather Icon",
            modifier = Modifier
                .size(100.dp)
                .padding(8.dp),
            contentScale = ContentScale.Fit
        )

        // Temperature
        Text(
            text = "${weather.temperature}°C",
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2196F3)
        )

        // Description
        Text(
            text = weather.description,
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF757575)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Additional Weather Details
        Card(
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                WeatherDetailRow(label = "Humidity", value = "${weather.humidity}%")
                WeatherDetailRow(label = "Wind Speed", value = "${weather.windSpeed} km/h")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = onRefresh) {
                Text("Refresh")
            }
            Button(onClick = onNavigateToForecast) {
                Text("Forecast")
            }
        }
    }
}

@Composable
fun WeatherDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF757575)
        )
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2196F3)
        )
    }
}
