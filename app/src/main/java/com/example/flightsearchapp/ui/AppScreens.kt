package com.example.flightsearchapp.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.flightsearchapp.FlightSearchTextField
import com.example.flightsearchapp.FlightSearchTopBar
import com.example.flightsearchapp.R
import com.example.flightsearchapp.components.AirportsComponent
import com.example.flightsearchapp.components.NoResultsFound
import com.example.flightsearchapp.components.SearchResults
import com.example.flightsearchapp.data.Airport
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
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior() //or EnterAlwaysScrollBehavior()
    val homeScreenTitle = stringResource(R.string.HomeScreen_Title)
    val focusManager = LocalFocusManager.current
    var topBarTitle by rememberSaveable { mutableStateOf( homeScreenTitle) }
    val uiState by viewmodel.flightSearchUiState.collectAsStateWithLifecycle()
    Scaffold(

        modifier = Modifier,
        topBar = {

            FlightSearchTopBar(
                title = topBarTitle,
                scrollBehavior = scrollBehavior,
                onBackClicked = {
                    navController.popBackStack()
                    topBarTitle = homeScreenTitle
                                },
                canNavigateBack = navController.previousBackStackEntry != null
            )

        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding)
        ) {
            FlightSearchTextField(
                value = uiState.searchQuery,
                onValueChange = { viewmodel.onEvent(event = UiEvent.OnSearchQueryChange(it)) },
                label = { Text(text = "Flight Search") },
                placeholder = { Text(text = "Search by airport name or IATA code") },
                searchQuery = uiState.searchQuery,
                onSearchQueryChanged = { viewmodel.onEvent(event = UiEvent.OnSearchQueryChange(it))},
                onSearch = { viewmodel.onEvent(event = UiEvent.OnSearchQuery(uiState.searchQuery))}
            )

            Box {
                when{
                    uiState.searchQuery.isNotEmpty() && uiState.isSearchActive && uiState.searchResults.isNotEmpty() -> {
                       SearchResults(
                           uiState = uiState,
                           onClick = { airport ->
                               topBarTitle = "Flights from ${airport.iataCode}"
                               viewmodel.onEvent(event = UiEvent.SelectAirport(airport))
                               if(navController.currentDestination?.route != Destination.FlightSearch.name) navController.navigate(Destination.FlightSearch.name)
                               focusManager.clearFocus()


                           }
                       )
                    }

                    uiState.searchQuery.isNotEmpty() && uiState.isSearchActive && uiState.searchResults.isEmpty() -> {
                        NoResultsFound(uiState)
                    }
                    else -> {
                        AirportsComponent(
                            navController = navController,
                            uiState = uiState,
                            onEvent = { viewmodel.onEvent(it) },
                        )

                    }

                }

            }



        }

    }

}


@Composable
fun FlightDetailsCard(
    modifier: Modifier = Modifier,
    arrivalAirport: Airport,
    departureAirport: Airport?,
    onFavoriteClicked: () -> Unit,
    isFavorite: Boolean
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
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
                    iataCode = departureAirport!!.iataCode,
                    airportName = departureAirport.airportName
                )

                CardDetails(
                    label = "Arrival",
                    iataCode = arrivalAirport.iataCode,
                    airportName = arrivalAirport.airportName
                )

            }
            FavoriteButton(
                modifier = Modifier,
                isFavorite = isFavorite,
                onClick = onFavoriteClicked

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
            modifier = Modifier.size(42.dp),
            imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
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


