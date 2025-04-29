package com.example.flightsearchapp.ui.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.flightsearchapp.EmbeddedSearchBar
import com.example.flightsearchapp.FlightSearchTopBar
import com.example.flightsearchapp.R
import com.example.flightsearchapp.TopAppBarSurface
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
    val navController = rememberNavController()
    val allAirports = viewmodel.allAirports.collectAsStateWithLifecycle(emptyList())
    val searchResults = viewmodel.searchResultsForLongLists.collectAsStateWithLifecycle(emptyList())
    val favoriteRoutes = viewmodel.favoriteRoutes.collectAsStateWithLifecycle(emptyList())

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior() //or EnterAlwaysScrollBehavior()
    val homeScreenTitle = stringResource(R.string.HomeScreen_Title)
    var topBarTitle = rememberSaveable { mutableStateOf(homeScreenTitle) }
    val onBackHandler = {
        topBarTitle.value = homeScreenTitle
        viewmodel.onActiveChanged(false)
        viewmodel.toggleSearchBarVisibility(false)
        navController.navigateUp()

    }
    Scaffold(
        modifier = Modifier,
        topBar = {
            Column(verticalArrangement = Arrangement.spacedBy((-1).dp)) {
                AnimatedVisibility(
                    visible = !viewmodel.isSearchActive,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    FlightSearchTopBar(
                        title = topBarTitle.value,
                        navigateUp = { navController.navigateUp() },
                        scrollBehavior =  scrollBehavior,
                        canNavigateBack = navController.previousBackStackEntry != null,
                        onBackClicked = { onBackHandler() },
                        isSearchBarVisible = viewmodel.isSearchBarVisible,
                        onSearchIconClicked = {
                            viewmodel.toggleSearchBarVisibility(true)
                            viewmodel.onActiveChanged(true)
                        }
                    )
                }

                AnimatedVisibility(
                    visible = viewmodel.isSearchBarVisible,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    TopAppBarSurface(
                        scrollBehavior = scrollBehavior,
                    ) {
                        EmbeddedSearchBar(
                            searchResults = searchResults.value,
                            onBackClicked = { onBackHandler() },
                            searchQuery = viewmodel.searchQuery,
                            onQueryChanged = {
                                viewmodel.onSearchQueryChanged(it)
                            },
                            onSearch = {
                                viewmodel.onSearchQuery(viewmodel.searchQuery)


                            }, isSearchActive = viewmodel.isSearchActive,
                            onActiveChanged = { viewmodel.onActiveChanged(it) },
                            onAirportClick = {
                                airport ->
                                viewmodel.selectAirport(airport)
                                navController.navigate(
                                    "${Destination.FlightSearch.name}/${airport.iataCode}"
                                )
                                topBarTitle.value = " FlightSearchScreen"
                                viewmodel.onActiveChanged( false)
                                viewmodel.toggleSearchBarVisibility(false)



                            }
                        )

                    }
                }

            }
        }
    ) { innerPadding ->


        NavHost(
            navController = navController,
            startDestination = Destination.Home.name
        )
        {
            //Home Screen
            composable(Destination.Home.name) {
                HomeScreen(
                    innerPadding = innerPadding,
                    allAirports = allAirports.value,
                    favoriteRoutes = favoriteRoutes.value,
                )
            }
            val airportIataCodeArgument = "airportIataCode"
            composable(
                route = Destination.FlightSearch.name + "/{$airportIataCodeArgument}",
                arguments = listOf(navArgument(airportIataCodeArgument) {
                    type = NavType.StringType
                })
            ) { backstackEntry ->
                val airportIataCode = backstackEntry.arguments?.getString("airportIataCode") ?: ""
                val currentAirport = viewmodel.getAirportByIataCode(airportIataCode)
                    .collectAsStateWithLifecycle(null)
                FlightSearchScreen(
                    allAirports = allAirports.value,
                    innerPadding = innerPadding,
                    currentAirport = currentAirport.value,
                    toggleFavorite = viewmodel::toggleFavorite,
                )
            }


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