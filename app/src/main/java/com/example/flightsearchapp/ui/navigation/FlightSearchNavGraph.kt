package com.example.flightsearchapp.ui.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.sharp.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.material3.IconToggleButton
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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
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
    val allAirports by viewmodel.allAirports.collectAsStateWithLifecycle(emptyList())
    val searchResults by viewmodel.searchResultsForLongLists.collectAsStateWithLifecycle(emptyList())
    val favoriteRoutes by viewmodel.favoriteRoutes.collectAsStateWithLifecycle(emptyList())


    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior() //or EnterAlwaysScrollBehavior()
    val homeScreenTitle = stringResource(R.string.HomeScreen_Title)
    var topBarTitle by rememberSaveable { mutableStateOf(homeScreenTitle) }
    val onBackHandler = {
        topBarTitle = homeScreenTitle
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
                        title = topBarTitle,
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
                            searchResults = searchResults,
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
                                navController.navigate(Destination.FlightSearch.name)
                                topBarTitle = " FlightSearchScreen"
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
                    allAirports = allAirports,
                    innerPadding = innerPadding,
                    favoriteRoutes = favoriteRoutes,
                    onFavoriteClicked = viewmodel::toggleFavorite
                )
            }
            composable(
                route = Destination.FlightSearch.name
            ) { backstackEntry ->

                FlightSearchScreen(
                    allAirports = allAirports,
                    innerPadding = innerPadding,
                    currentAirport = viewmodel.selectedAirport,
                    toggleFavorite = viewmodel::toggleFavorite
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
        modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp, pressedElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            Column(
                modifier = Modifier.weight(1f, true),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {

                CardDetails(
                    label = "Departure",
                    iataCode = departureAirport.iataCode,
                    airportName = departureAirport.airportName
                )

                CardDetails(
                    label = "Arrival",
                    iataCode = arrivalAirport.iataCode,
                    airportName = arrivalAirport.airportName
                )

            }
            FavoriteButton(
                modifier = Modifier ,
                isFavorite = isFavorite,
                onClick = {
                    onFavoriteClicked
                }
            )
        }
    }
}

@Composable
fun CardDetails(
    label: String,
    iataCode: String,
    airportName: String
) {
    Column {
        Text(
            text = label,
            textDecoration = TextDecoration.Underline,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        )
        {
            Text(
                text = iataCode,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = airportName,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
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
    IconToggleButton(
        modifier = modifier,
        checked = isFavorite,
        onCheckedChange = { onClick() },

        ) {
        Icon(
            modifier = Modifier.size(48.dp),
            imageVector = if (isFavorite) Icons.Outlined.Star else Icons.Sharp.Star,
            contentDescription = null,
            tint = if (isFavorite) Color.Red else MaterialTheme.colorScheme.onSurfaceVariant
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
        ) { _, _ -> }
    }
}
