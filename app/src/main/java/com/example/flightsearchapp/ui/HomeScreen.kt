package com.example.flightsearchapp.ui

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.flightsearchapp.data.Airport
import com.example.flightsearchapp.data.FavoriteRoute



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    allAirports: List<Airport>,
    innerPadding: PaddingValues,
    favoriteRoutes: List<FavoriteRoute>,
) {


            Column(
                verticalArrangement = Arrangement.Top,
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primaryContainer),
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

                            FavoriteRoutesScreen(
                                onFavoriteClicked = {

                                },
                                contentPadding = innerPadding,
                                departureAirport = departureAirport!!,
                                arrivalAirport = arrivalAirport!!,
                                isFavorite = true
                            )
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