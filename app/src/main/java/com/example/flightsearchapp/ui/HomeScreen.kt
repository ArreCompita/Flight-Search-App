package com.example.flightsearchapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.flightsearchapp.data.Airport
import com.example.flightsearchapp.data.FavoriteRoute
import com.example.flightsearchapp.ui.navigation.FlightDetailsCard



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    allAirports: List<Airport>,
    innerPadding: PaddingValues = PaddingValues(0.dp),
    favoriteRoutes: List<FavoriteRoute>,
) {


            Column(
                verticalArrangement = Arrangement.Top,
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxWidth()
                ) {
                if (favoriteRoutes.isEmpty()) {
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

                            FlightDetailsCard(
                                modifier = Modifier,
                                arrivalAirport = arrivalAirport!!,
                                departureAirport = departureAirport!!,
                                onFavoriteClicked = {},
                                isFavorite = true

                            )
                        }


                    }

                }

            }
}




@Preview
@Composable
fun HomeScreenPreview() {
    HomeScreen(
        allAirports = List(5){index ->
            Airport(
                index,
                "OPO",
                "Inernational Aiport"
                ,90
            )
        },
        favoriteRoutes = List(3){ index ->
            FavoriteRoute(
                index,
                "OPO",
                "OPO"
            )

        }
    )
}
