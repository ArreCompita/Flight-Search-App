package com.example.flightsearchapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconToggleButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.flightsearchapp.data.Airport
import com.example.flightsearchapp.data.FavoriteRoute
import com.example.flightsearchapp.ui.theme.FlightSearchAppTheme


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
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Column(
                modifier = Modifier
                    .weight(1f, true)
                    .padding(end = 20.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = "Departure Airport",
                    style = MaterialTheme.typography.labelMedium,
                    textDecoration = TextDecoration.Underline
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                ) {

                    Text(
                        text = departureAirport.iataCode,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = departureAirport.airportName,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Text(
                    text = "Arrival Airport",
                    style = MaterialTheme.typography.labelMedium,
                    textDecoration = TextDecoration.Underline
                )
                Row (
                    modifier = Modifier.fillMaxWidth(),
                ) {

                    Text(
                        text = arrivalAirport.iataCode,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
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
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
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

}