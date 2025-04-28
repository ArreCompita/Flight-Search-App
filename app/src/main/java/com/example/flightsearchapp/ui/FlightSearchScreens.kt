package com.example.flightsearchapp.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.example.flightsearchapp.EmbeddedSearchBar
import com.example.flightsearchapp.R
import com.example.flightsearchapp.data.Airport
import com.example.flightsearchapp.FlightSearchTopBar
import com.example.flightsearchapp.TopAppBarSurface
import com.example.flightsearchapp.ui.navigation.NavigationDestination


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlightSearchScreen(
    airportIataCode: String,
    onBackClicked: () -> Unit,
    viewModel: FlightSearchViewmodel ,
    navController: NavHostController = rememberNavController()
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val allAirports by viewModel.allAirports.collectAsStateWithLifecycle(emptyList())
    val currentAirport = remember(airportIataCode){
        allAirports.find { it.iataCode == airportIataCode }
    }


    Scaffold(
        modifier = Modifier,
        topBar = {
            Column(verticalArrangement = Arrangement.spacedBy((-1).dp)) {

                FlightSearchTopBar(
                        title = "FlightSearch Screen",
                        navigateUp = { navController.navigateUp() },
                        scrollBehavior = scrollBehavior,
                        canNavigateBack = true,
                        onBackClicked = {
                            onBackClicked()
                            viewModel.onActiveChanged(false) },
                        isSearchBarVisible = viewModel.isSearchBarVisible

                    )
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
            if (currentAirport == null) {
                Text(
                    text = "FlightSearchScreen Departure airport null or Loading Airport ",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(8.dp)
                )

            } else {

                Text(
                    text = "Flights from ${currentAirport.iataCode}}",
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
                        RouteSearchScreen(
                            onFavoriteClicked = { viewModel.toggleFavorite(currentAirport.iataCode, airport.iataCode) },
                            contentPadding = innerPadding,
                            departureAirport = currentAirport,
                            arrivalAirport = airport,
                            isFavorite = true
                        )
                    }

                }

            }

        }


    }
}


@Composable
fun RouteSearchScreen(
    modifier: Modifier = Modifier,
    onFavoriteClicked:  (() -> Unit),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    departureAirport: Airport ,
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

//@Composable
//fun FlightSearchDetailsScreen(
//    modifier: Modifier = Modifier,
//    onFavoriteClicked: (() -> Unit),
//    contentPadding: PaddingValues = PaddingValues(0.dp),
//    departureAirport: Airport,
//    arrivalAirport: Airport,
//    isFavorite: Boolean
//
//){
//    val layOutDirection = LocalLayoutDirection.current
//    Column(
//        modifier = Modifier.padding(
//            start = contentPadding.calculateStartPadding(layOutDirection),
//            end = contentPadding.calculateStartPadding(layOutDirection)
//        )
//    ) {
//
//        FlightDetailsCard(
//            modifier = modifier,
//            arrivalAirport = arrivalAirport,
//            departureAirport = departureAirport,
//            contentPadding = contentPadding,
//            onFavoriteClicked = onFavoriteClicked,
//            isFavorite = isFavorite
//        )
//
//    }
//
//
//}


//@Composable
//fun FlightSearchDetails (
//    modifier: Modifier = Modifier,
//    arrivalAirports: List<Airport>,
//    departureAirport: Airport ,
//    contentPadding: PaddingValues = PaddingValues(0.dp),
//    onFavoriteClicked: ((String, String) -> Unit),
//    isFavorite: Boolean
//) {
//    LazyColumn(
//        modifier = modifier,
//        contentPadding = contentPadding
//    ) {
//        items(
//            items = arrivalAirports,
//            key = { airport -> airport.id }
//        ) { airport ->
//            Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(16.dp),
//            horizontalArrangement = Arrangement.SpaceBetween
//            ) {
//                Column {
//                    Row {
//                        Text(text = departureAirport.iataCode)
//                        Text(text = departureAirport.airportName)
//                    }
//                    Row {
//                        Text(text = airport.iataCode)
//                        Text(text = airport.airportName)
//
//                    }
//                }
//                FavoriteButton(
//                    isFavorite = isFavorite,
//                    onClick = {
//                        onFavoriteClicked(departureAirport.iataCode.toString(), airport.iataCode.toString(),)
//
//                    }
//                )
//            }
//        }
//    }
//}




