package com.example.flightsearchapp.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn

@OptIn(ExperimentalCoroutinesApi::class)
class FlightSearchViewmodel(
    private val Dao: FlightSearchDao,
    private val userPreferences: UserPreferencesRepository): ViewModel()
{

    var isSearchBarVisible by mutableStateOf(false)
        private set
    var isSearchExpanded by mutableStateOf(false)
        private set

    fun toggleSearchBarVisibility() {
        isSearchBarVisible = !isSearchBarVisible
        if (!isSearchBarVisible) {
            isSearchExpanded = false
            searchQuery = ""
        }
    }

    var isExpanded by mutableStateOf(false)
        private set


    var searchQuery by mutableStateOf("")
        private set

    var departureIataCode by mutableStateOf("")
        private set

    var destinationIataCode by mutableStateOf("")
        private set

    private val _favoriteRoutesFlow = MutableStateFlow<List<FavoriteRoute>>(emptyList())
    val favoriteRoutesFlow: StateFlow<List<FavoriteRoute>> = _favoriteRoutesFlow.asStateFlow()

    init {
        viewModelScope.launch {
            retrieveFavoriteRoutes().collect { favorites ->
                _favoriteRoutesFlow.value = favorites
            }
        }

    }



    val departureAirport: Flow<Airport?> = getAirportByIataCode(departureIataCode).flowOn(Dispatchers.IO)

    val destinationAirport: Flow<Airport?> = getAirportByIataCode(destinationIataCode).flowOn(Dispatchers.IO)

    val storedSearchQuery: Flow<String> = userPreferences.searchQuery

    val suggestions: Flow<String> =
        snapshotFlow { searchQuery }.combine(storedSearchQuery) { searchQuery, storedSearchQuery ->
            when {
                searchQuery.isNotEmpty() -> storedSearchQuery.filter { storedSearchQuery.contains(searchQuery,ignoreCase = true)  }
                else -> storedSearchQuery
            }
        }


    val allAirports: Flow<List<Airport>> = retrieveAllAirports().flowOn(Dispatchers.IO)

    val departureAirport2: Flow<Airport?> =
        snapshotFlow { departureIataCode }.flatMapLatest { departureIataCode ->
            getAirportByIataCode(departureIataCode)
        }



    //for long lists
    val searchResultsForLongLists: Flow<List<Airport>> =
        snapshotFlow { searchQuery }.flatMapLatest { searchQuery ->
            if (searchQuery.isNotEmpty()) {
                searchAirport(searchQuery)
            } else {
                allAirports
            }
        }


// for Short lists
//    val searchResultsForShortLists: Flow<List<Airport>> =
//        snapshotFlow { searchQuery }.combine(allAirports) { searchQuery, airports ->
//            when {
//                searchQuery.isNotEmpty() -> airports.filter { airport ->
//                    airport.airportName.contains(searchQuery, ignoreCase = true)
//                            || airport.iataCode.contains(searchQuery, ignoreCase = true)
//                }
//                else -> airports
//            }
//        }.stateIn(
//            scope = viewModelScope,
//            started = SharingStarted.WhileSubscribed(5_000),
//            initialValue = emptyList()
//        )





    fun isFavorite(iataDepartureCode: String, iataDestinationCode: String): Boolean {
        return favoriteRoutesFlow.value.any {
            it.departureCode == iataDepartureCode && it.destinationCode == iataDestinationCode
        }

    }
    fun toggleFavorite(departureCode: String, destinationCode: String) {
        viewModelScope.launch(Dispatchers.IO) {
            if (isFavorite(departureCode, destinationCode)) {
                deleteFavoriteRoute(FavoriteRoute(0,departureCode, destinationCode))
            } else {
                insertFavoriteRoute(FavoriteRoute(0,departureCode, destinationCode))
            }
        }
    }

    fun onSearchQueryChanged(newSearchQuery: String) {
            searchQuery = newSearchQuery

    }
    fun departureAirportChanged(newDepartureIataCode: String) {
        departureIataCode = newDepartureIataCode
    }
    fun destinationAirportChanged(newDestinationIataCode: String) {
        destinationIataCode = newDestinationIataCode
    }

    fun onSearchQuery(searchQuery: String){
        viewModelScope.launch {
            userPreferences.saveSearchQuery(searchQuery)
        }
    }






    fun searchAirport(searchQuery: String) : Flow<List<Airport>> = Dao.searchAirport(searchQuery)
    //Get Airport by iataCode
    fun getAirportByIataCode(iataCode: String) : Flow<Airport?> = Dao.getAirportByIataCode(iataCode)
    //Get All Airports
    fun retrieveAllAirports(): Flow<List<Airport>> = Dao.getAllAirports()
    //Get Favorite Routes
    fun retrieveFavoriteRoutes(): Flow<List<FavoriteRoute>> = Dao.getFavoriteRoutes()


    //Insert and Delete Favorite Routes
    suspend fun insertFavoriteRoute(favoriteRoute: FavoriteRoute) = Dao.insertFavoriteRoute(favoriteRoute)
    suspend fun deleteFavoriteRoute(favoriteRoute: FavoriteRoute) = Dao.deleteFavoriteRoute(favoriteRoute)
    //Save or delete Favourite Route




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



// data class HomeUiState(
//    val searchQuery: String = "",
//    val favoriteRoutes: List<FavoriteRoute> = emptyList(),
//    val airports: List<Airport> = emptyList(),
//    val searchResults: List<Airport> = emptyList(),
//    val departureAirport: Airport? = null
//)
