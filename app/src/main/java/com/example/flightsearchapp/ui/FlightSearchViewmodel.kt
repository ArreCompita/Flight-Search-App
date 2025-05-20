package com.example.flightsearchapp.ui

import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.flightsearchapp.FlightSearchApplication
import com.example.flightsearchapp.data.Airport
import com.example.flightsearchapp.data.FavoriteRoute
import com.example.flightsearchapp.data.FlightSearchDao
import com.example.flightsearchapp.data.UserPreferencesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

@OptIn(ExperimentalCoroutinesApi::class)
class FlightSearchViewmodel(
    private val dao: FlightSearchDao,
    private val userPreferences: UserPreferencesRepository
): ViewModel() {

init {
    viewModelScope.launch {
        loadAllAirports()
        loadFavoriteRoutes()
        loadSearchQuery()
    }

}
    private fun loadAllAirports() {
        viewModelScope.launch {
            dao.getAllAirports().collect { airports ->
                _fLightSearchUiState.update { currentState ->
                    currentState.copy(
                        allAirports = airports
                    )
                }
            }


        }
    }
    private fun loadFavoriteRoutes() {
        viewModelScope.launch {
            dao.getFavoriteRoutes().collect { favoriteRoutes ->
                _fLightSearchUiState.update { currentState ->
                    currentState.copy(
                        favoriteRoutes = favoriteRoutes
                    )
                }
            }
        }
    }
    private fun loadSearchQuery() {
        viewModelScope.launch {
            userPreferences.searchQuery.collect { searchQuery ->
                _fLightSearchUiState.update { currentState ->
                    currentState.copy(
                        searchQuery = searchQuery
                    )
                }
            }
        }
    }

    //UiState
    private val _fLightSearchUiState : MutableStateFlow<HomeScreenUiState> = MutableStateFlow(HomeScreenUiState())
    val flightSearchUiState: StateFlow<HomeScreenUiState> = _fLightSearchUiState
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = HomeScreenUiState()
        )


    fun onEvent(event: UiEvent){
        when(event){
            is UiEvent.OnSearchQueryChange -> {
                _fLightSearchUiState.update {
                    it.copy(
                        searchQuery = event.newSearchQuery,
                        isSearchActive = true,
                        searchResults =
                        if ( _fLightSearchUiState.value.searchQuery.isNotEmpty()) {
                            _fLightSearchUiState.value.allAirports.filter { airport ->
                                airport.airportName.contains(event.newSearchQuery, ignoreCase = true)
                                        || airport.iataCode.contains(event.newSearchQuery, ignoreCase = true)
                            }
                        } else {
                            _fLightSearchUiState.value.allAirports
                        },
                    )
                }
            }
            is UiEvent.OnSearchQuery -> {
                onSearchQuery(event.searchQuery)
            }
            is UiEvent.SelectAirport -> {
                _fLightSearchUiState.update { it.copy(
                    currentAirport = event.newAirport,
                    searchQuery = event.newAirport.iataCode,
                    isSearchActive = false
                ) }
                onSearchQuery(event.newAirport.iataCode)
            }
            is UiEvent.ToggleFavorite -> {
                toggleFavorite(event.departureCode, event.destinationCode)
            }
        }
    }

    //toggle favorite
    private fun toggleFavorite(departureCode: String, destinationCode: String ) {
        viewModelScope.launch(Dispatchers.IO) {
            val favoriteRoute = getFavoriteRoutesByCodes(departureCode, destinationCode).first()
            if ( favoriteRoute != null ) {
                deleteFavoriteRoute(FavoriteRoute(departureCode = departureCode, destinationCode = destinationCode))
            } else {
                insertFavoriteRoute(FavoriteRoute( departureCode = departureCode, destinationCode =  destinationCode))
            }
        }
    }



    private fun onSearchQuery(searchQuery: String) { viewModelScope.launch { userPreferences.saveSearchQuery(searchQuery) } }


    //Get Favorite Route by Iata Codes
    private fun getFavoriteRoutesByCodes(depCode: String, destCode: String): Flow<FavoriteRoute?> {
        return dao.getFavoriteRouteByCodes(depCode, destCode)
    }

    //Insert and Delete Favorite Routes
    suspend fun insertFavoriteRoute(favoriteRoute: FavoriteRoute) = dao.insertFavoriteRoute(favoriteRoute)
    suspend fun deleteFavoriteRoute(favoriteRoute: FavoriteRoute) = dao.deleteFavoriteRoute(favoriteRoute.departureCode,favoriteRoute.destinationCode)




    companion object {
        val factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    (this[ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY] as FlightSearchApplication)
                FlightSearchViewmodel(
                    application.database.flightSearchDao(),
                    application.userPreferencesRepository
                )
            }
        }
    }
}
