package com.example.flightsearchapp.ui

import com.example.flightsearchapp.data.Airport
import com.example.flightsearchapp.data.FavoriteRoute


data class HomeScreenUiState(
        val allAirports: List<Airport> = emptyList(),
        val favoriteRoutes: List<FavoriteRoute> = emptyList(),
        val searchResults: List<Airport> = emptyList(),
        val currentAirport: Airport? = null,
        val isSearchActive: Boolean = false,
        val searchQuery: String = ""
)
sealed interface UiEvent {
        data class OnSearchQueryChange(val newSearchQuery: String) : UiEvent
        data class OnSearchQuery(val searchQuery: String) : UiEvent
        data class SelectAirport(val newAirport: Airport) : UiEvent
        data class ToggleFavorite(val departureCode: String, val destinationCode: String) : UiEvent
}
