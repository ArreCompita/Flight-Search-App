package com.example.flightsearchapp.components

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.flightsearchapp.ui.AppUiState
import com.example.flightsearchapp.ui.Destination
import com.example.flightsearchapp.ui.FlightSearchScreen
import com.example.flightsearchapp.ui.HomeScreen
import com.example.flightsearchapp.ui.UiEvent

@Composable
fun AirportsComponent(
    navController: NavHostController,
    uiState: AppUiState,
    onEvent: (UiEvent) -> Unit,

){
    NavHost(
        navController = navController,
        startDestination = Destination.Home.name
    )
    {
        //Home Screen
        composable(Destination.Home.name) {
            HomeScreen(
                state = uiState,
                onEvent = { onEvent(it) }
            )
        }
        composable(
            route = Destination.FlightSearch.name
        ) {
            FlightSearchScreen(
                state = uiState,
                onEvent = { onEvent(it) }
            )
        }


    }
}