package com.example.flightsearchapp.ui.navigation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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

sealed class Destination(val route: String) {
    object Home : Destination("home")
    object FlightSearch : Destination("flightList/{airportIataCode}")
    fun createRoute(airportIataCode: String) = "flightList/$airportIataCode"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlightApp() {
    val viewmodel: FlightSearchViewmodel = viewModel ()
    val navController: NavHostController = rememberNavController()

    NavHost(navController = navController, startDestination = Destination.Home.route){

        //Home Screen
        composable(Destination.Home.route){
            HomeScreen(
                onAirportSelected = { airport ->
                    viewmodel.selectAirport(airport)
                    navController.navigate(
                        Destination.FlightSearch.createRoute(airport.iataCode)
                    )
                }, viewModel = viewmodel
            )
        }
        composable(
            route = Destination.FlightSearch.route,
            arguments = listOf(navArgument("airportIataCode") {
                type = NavType.StringType
            })
        ){
            val airportIataCode = it.arguments?.getString("airportIataCode") ?: ""
            FlightSearchScreen(
                airportIataCode = airportIataCode,
                onBackClicked = { navController.popBackStack() },
                viewModel = viewmodel
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