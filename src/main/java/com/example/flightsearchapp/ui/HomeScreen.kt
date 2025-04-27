package com.example.flightsearchapp.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconToggleButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.flightsearchapp.FlightSearchBar
import com.example.flightsearchapp.FlightSearchTopBar
import com.example.flightsearchapp.R
import com.example.flightsearchapp.TopAppBarSurface
import com.example.flightsearchapp.data.Airport
import com.example.flightsearchapp.ui.navigation.NavigationDestination


object HomeDestination : NavigationDestination {
    override val route = "home"
    override val titleRes = R.string.app_name
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navigateToFlightSearch: (String) -> Unit,
    viewModel: FlightSearchViewmodel = viewModel(factory = FlightSearchViewmodel.factory),
    navController: NavHostController = rememberNavController()
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val departureAirport by viewModel.departureAirport2.collectAsStateWithLifecycle(null)
    val allAirports by viewModel.allAirports.collectAsStateWithLifecycle(emptyList())
    val searchResults by viewModel.searchResultsForLongLists.collectAsStateWithLifecycle(emptyList())
    var isExpanded by rememberSaveable { mutableStateOf(false) }
    var isSearchBarVisible by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier,
        topBar = {
            Column(verticalArrangement = Arrangement.spacedBy((-1).dp)) {
                if (!isExpanded) {
                    FlightSearchTopBar(
                        title = "HomeScreen",
                        navigateUp = { navController.navigateUp() },
                        scrollBehavior = scrollBehavior,
                        canNavigateBack = false,
                        onBackClicked = { isSearchBarVisible = false },
                        isSearchBarVisible = isSearchBarVisible,
                        onSearchIconClicked = { isSearchBarVisible = true }
                    )
                }
                AnimatedVisibility(
                    visible = isSearchBarVisible,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    TopAppBarSurface(
                        scrollBehavior = scrollBehavior
                    ) {

                        FlightSearchBar(
                            searchResults = searchResults,
                            onBackClicked = { isSearchBarVisible = false },
                            navController = navController,
                            searchQuery = viewModel.searchQuery,
                            onQueryChanged = {
                                viewModel.onSearchQueryChanged(it)
                            },
                            onSearchQuery = {
                                viewModel.onSearchQuery(viewModel.searchQuery)
                                isExpanded = false
                            }, isExpanded = isExpanded,
                            onExpandedChange = { isExpanded = it },
                            onAirportClick = {
                                isExpanded = false
                                viewModel.departureAirportChanged(it)
                                navigateToFlightSearch }
                        )

                    }
                }

            }
        }
    ) { innerPadding ->

        Column(
            verticalArrangement = Arrangement.Top,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primaryContainer),

            ) {
            if (departureAirport == null) {
                Text(
                    text = "No favorite routes123",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(8.dp)
                )

            } else {

                Text(
                    text = "favorite routes",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(8.dp)
                )
                LazyColumn(
                    modifier = Modifier,
                    contentPadding = innerPadding
                ){
                    items(
                        items = allAirports,
                        key = { airport -> airport.id }
                    ) { airport ->
                        val isFavorite = viewModel.isFavorite(departureAirport!!.iataCode, airport.iataCode)
                        FavoriteRoutesScreen(
                            onFavoriteClicked = {
                                viewModel.destinationAirportChanged(airport.iataCode)
                                viewModel::toggleFavorite
                            },
                            contentPadding = innerPadding,
                            departureAirport = departureAirport!!,
                            arrivalAirport = airport,
                            isFavorite = isFavorite
                        )
                    }


                }

            }

        }
    }
}


@Composable
fun FavoriteRoutesScreen(
    modifier: Modifier = Modifier,
    onFavoriteClicked: () -> Unit,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    departureAirport: Airport,
    arrivalAirport: Airport,
    isFavorite: Boolean
){
    val layOutDirection = LocalLayoutDirection.current
    Column(
        modifier = Modifier.padding(
            start = contentPadding.calculateStartPadding(layOutDirection),
            end = contentPadding.calculateStartPadding(layOutDirection)
        )
    ) {

        FlightDetailsCard(
            modifier = modifier,
            arrivalAirport = arrivalAirport,
            departureAirport = departureAirport,
            onFavoriteClicked = onFavoriteClicked,
            isFavorite = isFavorite
        )

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

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Row {
                Text(text = departureAirport.iataCode)
                Text(text = departureAirport.airportName)
            }
            Row {
                Text(text = arrivalAirport.iataCode)
                Text(text = arrivalAirport.airportName)

            }
        }
        FavoriteButton(
            isFavorite = isFavorite,
            onClick = {
                onFavoriteClicked
            }
        )
    }
}

@Composable
fun FavoriteButton(
    modifier: Modifier = Modifier,
    isFavorite: Boolean,
    onClick: () -> Unit
){
    FilledIconToggleButton(
        checked = isFavorite,
        onCheckedChange = { onClick() },
        modifier = modifier
    ) {
        if (isFavorite) {
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = null
            )
        } else {
            Icon(
                imageVector = Icons.Default.FavoriteBorder,
                contentDescription = null
            )

        }
    }
}