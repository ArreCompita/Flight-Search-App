package com.example.flightsearchapp.ui

import com.example.flightsearchapp.data.Airport
import com.example.flightsearchapp.data.FavoriteRoute

sealed interface
data class FlightSearchUiState(
        val allAirports: List<Airport> = emptyList(),
        val favoriteRoutes: List<FavoriteRoute> = emptyList(),
        val searchResults: List<Airport> = emptyList(),
        val currentAirport: Airport? = null,
        val isSearchActive: Boolean = false,
        val searchQuery: String = ""
)

sealed interface UiEvent {
        data class OnSearchQueryChange(val newSearchQuery: String) : UiEvent
        data class OnActiveChanged(val newActiveValue: Boolean) : UiEvent
        data class SelectAirport(val newAirport: Airport) : UiEvent
        data class ToggleFavorite(val departureCode: String, val destinationCode: String) : UiEvent
}

//sealed class UiIntent {
//        data class OnBackClicked () : UiIntent()
//}