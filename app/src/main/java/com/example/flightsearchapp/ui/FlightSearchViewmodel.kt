package com.example.flightsearchapp.ui

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.example.flightsearchapp.FlightSearchApplication
import com.example.flightsearchapp.data.Airport
import com.example.flightsearchapp.data.FavoriteRoute
import com.example.flightsearchapp.data.FlightSearchDao
import com.example.flightsearchapp.data.UserPreferencesRepository
import com.example.flightsearchapp.ui.navigation.Destination
import com.example.flightsearchapp.ui.navigation.NavigationDestination
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.stateIn

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

    fun saveSearchQuery(searchQuery: String) {
        viewModelScope.launch {
           userPreferences.saveSearchQuery(searchQuery)
        }
    }

    init {
        viewModelScope.launch {
            userPreferences.searchQuery.collect { searchQuery ->
                this@FlightSearchViewmodel.searchQuery = searchQuery
            }
        }
    }

    //UiState
    private val _fLightSearchUiState : MutableStateFlow<FlightSearchUiState> = MutableStateFlow(FlightSearchUiState())
    val flightSearchUiState: StateFlow<FlightSearchUiState> = _fLightSearchUiState
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = FlightSearchUiState()
        )

    //Airport selected

    private var _selectedAirport: MutableStateFlow<Airport?> = MutableStateFlow(null)
    val selectedAirport: StateFlow<Airport?> = _selectedAirport
        .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null
    )

    //Select Airport
    fun selectAirport(airport: Airport) {
        _selectedAirport.value = airport
    }

    //Data flows

    // All airports List with Iata code and Airport Name
    val allAirports: Flow<List<Airport>> = retrieveAllAirports()

    // Favorite Routes
   val favoriteRoutes: Flow<List<FavoriteRoute>> = retrieveFavoriteRoutes()

    init {
        viewModelScope.launch {
            retrieveFavoriteRoutes()
                .catch{ e -> Log.e("FlightSearchViewmodel", "Error retrieving favorite routes: $e") }
                .collect { routes ->
                    favoriteRoutes
                }
        }
    }



    //toggle favorite
    fun toggleFavorite(departureCode: String, destinationCode: String ) {
        viewModelScope.launch(Dispatchers.IO) {
            val favoriteRoute = getFavoriteRoutesByCodes(departureCode, destinationCode).first()
            if ( favoriteRoute != null ) {
                deleteFavoriteRoute(FavoriteRoute(departureCode = departureCode, destinationCode = destinationCode))
            } else {
                insertFavoriteRoute(FavoriteRoute( departureCode = departureCode, destinationCode =  destinationCode))
            }
        }
    }
//
//     //Reactive search for Short lists
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

    //Reactive Search for Long Lists
    val searchResults: Flow<List<Airport>> =
        snapshotFlow { searchQuery }.flatMapLatest { searchQuery ->
            if (searchQuery.isNotEmpty()) {
                searchAirport(searchQuery)
            } else {
                allAirports
            }
        }


    //SearchBar State
//    var isSearchBarVisible by mutableStateOf(false)
//        private set


//
//    fun toggleSearchBarVisibility(
//        isSearchBarVisible: Boolean ){
//        this.isSearchBarVisible = isSearchBarVisible
//    }
    var isSearchActive by mutableStateOf(false)
    private set


    fun onActiveChanged(newActiveValue: Boolean) {
        isSearchActive = newActiveValue
    }



    fun onSearchQuery(searchQuery: String){
        viewModelScope.launch {
            userPreferences.saveSearchQuery(searchQuery)
        }
    }







    //Get Favorite Route by Iata Codes
    fun getFavoriteRoutesByCodes(depCode: String, destCode: String): Flow<FavoriteRoute?> {
        return Dao.getFavoriteRouteByCodes(depCode, destCode)
    }
    fun searchAirport(searchQuery: String) : Flow<List<Airport>> = Dao.searchAirport(searchQuery)
    //Get All Airports
    fun retrieveAllAirports(): Flow<List<Airport>> = Dao.getAllAirports()
    //Get Favorite Routes
    fun retrieveFavoriteRoutes(): Flow<List<FavoriteRoute>> = Dao.getFavoriteRoutes()
    //Get Favorite Route by Iata Codes

    //Insert and Delete Favorite Routes
    suspend fun insertFavoriteRoute(favoriteRoute: FavoriteRoute) = Dao.insertFavoriteRoute(favoriteRoute)
    suspend fun deleteFavoriteRoute(favoriteRoute: FavoriteRoute) = Dao.deleteFavoriteRoute(favoriteRoute.departureCode,favoriteRoute.destinationCode)





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
