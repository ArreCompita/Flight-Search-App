package com.example.flightsearchapp.ui.navigation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.flightsearchapp.ui.FlightSearchScreen
import com.example.flightsearchapp.ui.FlightSearchViewmodel
import com.example.flightsearchapp.ui.HomeScreen

enum class Destination {
    Home,
    FlightSearch
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlightApp(
    viewmodel: FlightSearchViewmodel = viewModel(factory = FlightSearchViewmodel.factory)
) {
    val navController: NavHostController = rememberNavController()
    val allAirports = viewmodel.allAirports.collectAsStateWithLifecycle(emptyList())
    val searchResults = viewmodel.searchResultsForLongLists.collectAsStateWithLifecycle(emptyList())
    val favoriteRoutes = viewmodel.favoriteRoutes.collectAsStateWithLifecycle(emptyList())

    NavHost(navController = navController, startDestination = Destination.Home.name){

        //Home Screen
        composable(Destination.Home.name){
            HomeScreen(
                onAirportSelected = { airport ->
                    viewmodel.selectAirport(airport)
                    navController.navigate("${Destination.FlightSearch.name}/${airport.iataCode}"
                    )
                },
                allAirports = allAirports.value,
                searchResults = searchResults.value,
                favoriteRoutes = favoriteRoutes.value,
                isSearchActive = viewmodel.isSearchActive,
                toggleSearchBarVisibility = viewmodel::toggleSearchBarVisibility,
                isSearchBarVisible = viewmodel.isSearchBarVisible,
                onActiveChanged = viewmodel::onActiveChanged,
                onSearchQueryChanged = viewmodel::onSearchQueryChanged,
                onSearchQuery = viewmodel::onSearchQuery,
                searchQuery = viewmodel.searchQuery,
            )
        }
        val airportIataCodeArgument = "airportIataCode"
        composable(
            route = Destination.FlightSearch.name + "/{$airportIataCodeArgument}",
            arguments = listOf(navArgument(airportIataCodeArgument) { type = NavType.StringType })
        ){ backstackEntry ->
            val airportIataCode = backstackEntry.arguments?.getString("airportIataCode") ?: ""
            val currentAirport = viewmodel.getAirportByIataCode(airportIataCode)
                .collectAsStateWithLifecycle(null)

            FlightSearchScreen(
                onBackClicked = { navController.popBackStack() },
                allAirports = allAirports.value,
                currentAirport = currentAirport.value,
                onActiveChanged = viewmodel::onActiveChanged,
                isSearchBarVisible =  viewmodel.isSearchBarVisible,
                toggleFavorite = viewmodel::toggleFavorite,
            )
        }





    }





}



//NavHost(
//navController = navController,
//startDestination = FlightSearchScreens.HomeScreen.name,
//) {
//
//    val airportIataCodeArgument = "airportIataCode"
//    composable(
//        route = FlightSearchScreens.FlightSearchScreen.name + "/{$airportIataCodeArgument}",
//        arguments = listOf(navArgument(airportIataCodeArgument) {
//            type = NavType.StringType
//        })
//    ) { backStackEntry ->
//        val airportIataCode =
//            backStackEntry.arguments?.getString(airportIataCodeArgument)
//                ?: error("Airport ID cannot be null")
//
//        val airport by viewModel.getAirportByIataCode(airportIataCode)
//            .collectAsState(null)
//
//        if (departureAirport == null) {
//            Text(text = "Airport not found 123")
//        } else {
//            Column {
//                Text(
//                    text = "Flights from  ${departureAirport!!.iataCode} ",
//                    fontWeight = FontWeight.Bold,
//                    modifier = Modifier.padding(8.dp)
//                )
//                RouteSearchScreen(
//                    onFavoriteClicked = { },
//                    departureAirport = departureAirport!!,
//                    arrivalAirport = airport!!,
//                    isFavorite = true
//                )
//
//            }
//
//        }
//    }
//}