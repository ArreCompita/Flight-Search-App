package com.example.flightsearchapp.ui

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.flightsearchapp.data.Airport


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlightSearchScreen(
    allAirports: List<Airport>,
    innerPadding: PaddingValues = PaddingValues(0.dp),
    currentAirport: Airport?,
    toggleFavorite: (String, String) -> Unit,
) {

    Column(
        verticalArrangement = Arrangement.Top,
        modifier = Modifier
            .padding(innerPadding)
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer),

        ) {
        if (currentAirport == null) {
            Text(
                text = "Cargando aeropuertos... // aropuerto no encontrado",
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(8.dp)
            )

        } else {

            Text(
                text = "Flights from ${currentAirport.iataCode}",
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(8.dp)
            )
            LazyColumn(
                modifier = Modifier,
                contentPadding = innerPadding
            ) {
                items(
                    items = allAirports,
                    key = { airport -> airport.id }
                ) { airport ->
                    RouteSearchScreen(
                        onFavoriteClicked = {
                            toggleFavorite(
                                currentAirport.iataCode,
                                airport.iataCode
                            )
                        },
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



