package org.agaafar.weatherway.ui.screens

import android.location.Location
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
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
import org.agaafar.weatherway.data.WeatherParser
import org.agaafar.weatherway.location.LocationHelper
import org.agaafar.weatherway.models.Forecast
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun ForecastScreen(
    locationHelper: LocationHelper,
    onNavigateBack: () -> Unit
) {
    var forecast by remember { mutableStateOf<List<Forecast>?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(true) }

    fun fetchForecast() {
        loading = true
        locationHelper.getCurrentLocation { location: Location? ->
            if (location != null) {
                WeatherApi.getForecast(
                    latitude = location.latitude,
                    longitude = location.longitude,
                    startDate = getStartDate(),
                    endDate = getEndDate()
                ) { result ->
                    if (result != null) {
                        forecast = WeatherParser.parseForecast(result)
                        errorMessage = if (forecast.isNullOrEmpty()) "Could not load forecast" else null
                    } else {
                        errorMessage = "Error fetching forecast"
                    }
                    loading = false
                }
            } else {
                errorMessage = "Unable to get location"
                loading = false
            }
        }
    }

    LaunchedEffect(Unit) {
        fetchForecast()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE3F2FD)) // Same background as CurrentWeatherScreen
            .padding(horizontal = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 32.dp), // Fixed padding to avoid notch overlap
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Title Row with Back Arrow and Refresh Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color(0xFF2196F3))
                }
                Text(
                    text = "5-Day Forecast",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2196F3)
                )
                IconButton(onClick = { fetchForecast() }) {
                    Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = Color(0xFF2196F3))
                }
            }

            when {
                loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        color = Color(0xFF2196F3)
                    )
                }
                errorMessage != null -> {
                    Text(
                        text = errorMessage ?: "Unknown error",
                        color = Color(0xFFD32F2F),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                else -> {
                    forecast?.let { forecastList ->
                        // Display the first day's forecast prominently
                        val firstDay = forecastList.firstOrNull()
                        firstDay?.let {
                            HighlightedForecast(forecast = it)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Display the five-day forecast in a row
                        LazyRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(forecastList) { item ->
                                ForecastDayItem(forecast = item)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HighlightedForecast(forecast: Forecast) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.cloudy),
                contentDescription = "Weather Icon",
                modifier = Modifier.size(64.dp),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = forecast.dateTime,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2196F3)
                )
                Text(
                    text = "${forecast.temperature}°C",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = forecast.description,
                    fontSize = 16.sp,
                    color = Color(0xFF757575)
                )
            }
        }
    }
}
@Composable
fun ForecastDayItem(forecast: Forecast) {
    Card(
        modifier = Modifier
            .wrapContentWidth() // Adjust width to wrap content
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .wrapContentWidth(), // Ensure the column wraps its content
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = forecast.dateTime,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2196F3),
                maxLines = 1 // Ensure the date stays on one line
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "${forecast.temperature}°C",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
fun getStartDate(): String {
    val calendar = Calendar.getInstance()
    return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
}

fun getEndDate(): String {
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.DATE, 5)
    return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
}
