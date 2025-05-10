package org.agaafar.weatherway.navigation

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import org.agaafar.weatherway.location.LocationHelper
import org.agaafar.weatherway.ui.screens.CurrentWeatherScreen
import org.agaafar.weatherway.ui.screens.ForecastScreen

@Composable
fun AppNavigation(navController: NavHostController, context: Context) {
    val locationHelper = LocationHelper(context)

    NavHost(navController = navController, startDestination = "current_weather") {
       composable("current_weather") {
    CurrentWeatherScreen(
        locationHelper = locationHelper,
        onNavigateToForecast = {
            navController.navigate("forecast")
        }
    )
}

        composable("forecast") {
            ForecastScreen(
                locationHelper = locationHelper, // Pass LocationHelper here
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
