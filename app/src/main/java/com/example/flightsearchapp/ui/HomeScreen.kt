package com.example.flightsearchapp.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.flightsearchapp.data.Airport
import com.example.flightsearchapp.data.FavoriteRoute
import com.example.flightsearchapp.ui.navigation.FlightDetailsCard
import com.example.flightsearchapp.ui.theme.FlightSearchAppTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    allAirports: List<Airport>,
    innerPadding: PaddingValues = PaddingValues(0.dp),
    favoriteRoutes: List<FavoriteRoute>?,
    onFavoriteClicked: (String, String) -> Unit,

    ) {


    Column(
        modifier = Modifier.padding(innerPadding),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,

    ) {

        when (favoriteRoutes?.isEmpty()) {
            true -> Text(
                text = "No favorite routes123",
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(8.dp)
                    .align(alignment = Alignment.Start)
            )

            false -> HomeScreenDetailsList(
                modifier = Modifier.padding(horizontal = 8.dp),
                allAirports = allAirports,
                innerPadding = innerPadding,
                favoriteRoutes = favoriteRoutes,
                onFavoriteClicked = onFavoriteClicked
            )
            else ->
                Text(
                    text = "Loading favorite routes...",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(8.dp)
                        .align(alignment = Alignment.Start)
                )


        }

    }
}


@Composable
fun HomeScreenDetailsList(
    modifier: Modifier = Modifier,
    allAirports: List<Airport>,
    innerPadding: PaddingValues,
    favoriteRoutes: List<FavoriteRoute>,
    onFavoriteClicked: (String, String) -> Unit,
){
    LazyColumn(
        modifier = modifier,
        contentPadding = innerPadding
    ) {
        items(
            items = favoriteRoutes,
            key = { favoriteRoute -> favoriteRoute.id }
        ) { favoriteRoute ->
            val departureAirport = allAirports.find { airport ->
                airport.iataCode == favoriteRoute.departureCode
            }
            val arrivalAirport = allAirports.find { airport ->
                airport.iataCode == favoriteRoute.destinationCode
            }
            var isFavorite by remember { mutableStateOf(true) }

            FlightDetailsCard(
                modifier = Modifier,
                arrivalAirport = arrivalAirport!!,
                departureAirport = departureAirport!!,
                onFavoriteClicked = {
                    onFavoriteClicked(arrivalAirport.iataCode, departureAirport.iataCode)
                    isFavorite = !isFavorite
                },
                isFavorite = isFavorite

            )
        }

    }

}

@Preview
@Composable
fun HomeScreenDetailsListPreview() {
    FlightSearchAppTheme {
        HomeScreenDetailsList(
            allAirports = List(5) { index ->
                Airport(
                    index,
                    "OPO",
                    "Inernational Aiport", 90)}
            ,
            innerPadding = PaddingValues(0.dp),
            favoriteRoutes = List(3) { index ->
                FavoriteRoute(
                    index,
                    "OPO",
                    "OPO"
                )
            }, onFavoriteClicked = { _, _ -> },


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
