package com.example.flightsearchapp.ui.navigation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.flightsearchapp.ui.FlightSearchDestinations
import com.example.flightsearchapp.ui.FlightSearchScreen
import com.example.flightsearchapp.ui.HomeDestination
import com.example.flightsearchapp.ui.HomeScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlightSearchNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = HomeDestination.route,
        modifier = modifier
    ) {
        composable(route = HomeDestination.route) {
            HomeScreen(
                navController = navController,
                navigateToFlightSearch = {
                    navController.navigate(FlightSearchDestinations.route + "/${it}")
                }
            )
        }

        composable(
            route = FlightSearchDestinations.routeWithArgs,
            arguments = listOf(navArgument(FlightSearchDestinations.departureAirportArg) {
                type = NavType.StringType
            })
        ) { backStackEntry ->
            val departureAirport =
                backStackEntry.arguments?.getString(FlightSearchDestinations.departureAirportArg)
                    ?: error("Airport ID cannot be null")

            FlightSearchScreen(
                navController = navController
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