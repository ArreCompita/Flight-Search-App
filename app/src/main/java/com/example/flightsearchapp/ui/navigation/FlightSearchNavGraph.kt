package com.example.flightsearchapp.ui.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconToggleButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
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
import com.example.flightsearchapp.data.Airport
import com.example.flightsearchapp.data.FavoriteRoute
import com.example.flightsearchapp.ui.FlightSearchScreen
import com.example.flightsearchapp.ui.FlightSearchViewmodel
import com.example.flightsearchapp.ui.HomeScreen
import com.example.flightsearchapp.ui.theme.FlightSearchAppTheme

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
                        scrollBehavior = scrollBehavior,
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
                            onAirportClick = { airport ->
                                viewmodel.selectAirport(airport)
                                navController.navigate(
                                    "${Destination.FlightSearch.name}/${airport.iataCode}"
                                )
                                topBarTitle.value = " FlightSearchScreen"
                                viewmodel.onActiveChanged(false)
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
                    modifier = Modifier.fillMaxSize(),
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
                    innerPadding = innerPadding,
                    allAirports = allAirports.value,
                    currentAirport = currentAirport.value,
                    toggleFavorite = {  },
                )
            }


        }


    }

}


@Composable
fun FlightDetailsCard(
    modifier: Modifier = Modifier,
    arrivalAirport: Airport,
    departureAirport: Airport,
    onFavoriteClicked: () -> Unit,
    isFavorite: Boolean
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, true)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = stringResource(R.string.depart_details_card),
                    style = MaterialTheme.typography.labelMedium,
                    textDecoration = TextDecoration.Underline
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = departureAirport.iataCode,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.padding(2.dp))
                    Text(
                        text = departureAirport.airportName,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Text(
                    text = stringResource(R.string.arrive_details_card),
                    style = MaterialTheme.typography.labelMedium,
                    textDecoration = TextDecoration.Underline
                )
                Row (
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = arrivalAirport.iataCode,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.padding(2.dp))
                    Text(
                        text = arrivalAirport.airportName,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                }
            }
            FavoriteButton(
                modifier = Modifier.size(36.dp),
                isFavorite = isFavorite,
                onClick = {
                    onFavoriteClicked
                }
            )
        }
    }
}

@Composable
fun FavoriteButton(
    modifier: Modifier = Modifier,
    isFavorite: Boolean,
    onClick: () -> Unit
){
    FilledIconToggleButton(
        modifier = modifier,
        checked = isFavorite,
        onCheckedChange = { onClick() },

        ) {
        Icon(
            imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
            contentDescription = null,
            tint = if (isFavorite) Color.Red else MaterialTheme.colorScheme.onSurface
        )




    }
}

@Preview
@Composable
fun FlightDetailsCardPreview() {
    FlightSearchAppTheme {
        FlightDetailsCard(
            arrivalAirport = Airport(
                id = 1,
                airportName = "Sabiha Gokcen International Airport 1231412512343151125",
                passengers = 12,
                iataCode = "OPO"
            ),

            departureAirport = Airport(
                id = 2,
                airportName = "Sheremetyevo International Airport 12312412124124121241241",
                passengers = 15,
                iataCode = "SVO"
            ),
            onFavoriteClicked = {},
            isFavorite = true
        )
    }
}



@Preview
@Composable
fun HomeScreenPreview() {
    FlightSearchAppTheme {
        HomeScreen(
            allAirports = List(5) { index ->
                Airport(
                    index,
                    "OPO",
                    "Inernational Aiport", 90
                )
            },
            favoriteRoutes = List(3) { index ->
                FavoriteRoute(
                    index,
                    "OPO",
                    "OPO"
                )

            }
        )
    }
}