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
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn

@OptIn(ExperimentalCoroutinesApi::class)
class FlightSearchViewmodel(
    private val Dao: FlightSearchDao,
    private val userPreferences: UserPreferencesRepository
): ViewModel() {

    //Search State
    var searchQuery by mutableStateOf("")
        private set
    fun onSearchQueryChanged(newSearchQuery: String) {
        searchQuery = newSearchQuery

    }
    //Airport selected
    var selectedAirport by mutableStateOf<Airport?>(null)
    private set

    //Select Airport
    fun selectAirport(airport: Airport) {
        selectedAirport = airport
    }

    //Data flows
    val allAirports: Flow<List<Airport>> = retrieveAllAirports().flowOn(Dispatchers.IO)

    //Favorite Routes

    private val _favoriteRoutes: MutableStateFlow<List<FavoriteRoute>> = MutableStateFlow(emptyList())
    val favoriteRoutes: Flow<List<FavoriteRoute>> = _favoriteRoutes.asStateFlow()

    init {
        viewModelScope.launch {
            Dao.getFavoriteRoutes().flowOn(Dispatchers.IO)
                .collect { favoriteRoutes ->
                _favoriteRoutes.value = favoriteRoutes
            }
        }
    }
//
//    fun isFavorite(iataDepartureCode: String, iataDestinationCode: String): Boolean {
//            return _favoriteRoutes.value.any {
//            it.departureCode == iataDepartureCode && it.destinationCode == iataDestinationCode
//        }
//    }

    //toggle favorite

    fun toggleFavorite(departureCode: String, destinationCode: String, isFavorite: Boolean ) {
        viewModelScope.launch(Dispatchers.IO) {
            val route = FavoriteRoute(departureCode = departureCode, destinationCode = destinationCode)
            if (isFavorite) {
                _favoriteRoutes.value = _favoriteRoutes.value - route
                deleteFavoriteRoute(FavoriteRoute(departureCode = departureCode, destinationCode = destinationCode))
            } else {
                _favoriteRoutes.value = _favoriteRoutes.value + route
                insertFavoriteRoute(FavoriteRoute( departureCode = departureCode, destinationCode =  destinationCode))
            }
        }
    }

    //Reactive Search
    val searchResultsForLongLists: Flow<List<Airport>> =
        snapshotFlow { searchQuery }.flatMapLatest { searchQuery ->
            if (searchQuery.isNotEmpty()) {
                searchAirport(searchQuery)
            } else {
                allAirports
            }
        }

    //SearchBar State
    var isSearchBarVisible by mutableStateOf(false)
        private set

    var isSearchActive by mutableStateOf(false)
        private set

    fun toggleSearchBarVisibility(
        isSearchBarVisible: Boolean ){
        this.isSearchBarVisible = isSearchBarVisible
    }

    fun onActiveChanged(newActiveValue: Boolean) {
        isSearchActive = newActiveValue
    }




    //AutoCompletion State
    val storedSearchQuery: Flow<String> = userPreferences.searchQuery

    val suggestions: Flow<String> =
        snapshotFlow { searchQuery }.combine(storedSearchQuery) { searchQuery, storedSearchQuery ->
            when {
                searchQuery.isNotEmpty() -> storedSearchQuery.filter { storedSearchQuery.contains(searchQuery,ignoreCase = true)  }
                else -> storedSearchQuery
            }
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

