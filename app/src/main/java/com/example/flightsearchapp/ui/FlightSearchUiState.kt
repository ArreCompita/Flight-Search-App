package com.example.flightsearchapp.ui

import com.example.flightsearchapp.data.Airport
import com.example.flightsearchapp.data.FavoriteRoute

data class FlightSearchUiState (
        val allAirports: List<Airport> = emptyList(),
        val favoriteRoutes: List<FavoriteRoute> = emptyList(),
        val currentAirport: Airport? = null
        )