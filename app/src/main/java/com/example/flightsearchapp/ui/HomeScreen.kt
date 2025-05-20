package com.example.flightsearchapp.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.flightsearchapp.R
import com.example.flightsearchapp.data.Airport
import com.example.flightsearchapp.data.FavoriteRoute
import com.example.flightsearchapp.ui.navigation.FlightDetailsCard
import com.example.flightsearchapp.ui.theme.FlightSearchAppTheme



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    state: HomeScreenUiState,
    onEvent: (UiEvent) -> Unit,
    ) {

    Column(
        modifier = Modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        ) {

        when (state.favoriteRoutes.isEmpty()) {
            true ->
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxSize()
                ){
                    Image(
                        modifier = Modifier.size(84.dp),
                        painter = painterResource(id = R.drawable.airplane),
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.padding(8.dp))
                    Text(
                        text = "No Favorite Routes yet",
                        style = MaterialTheme.typography.titleLarge,
                        )


                }


            false -> {
                Text(
                    text = "Favorite Routes",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(horizontal = 24.dp, vertical = 4.dp )
                        .align(alignment = Alignment.Start))
                HomeScreenDetailsList(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    state = state,
                    onEvent = onEvent,
                )
            }
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
    innerPadding: PaddingValues = PaddingValues(0.dp),
    state: HomeScreenUiState,
    onEvent: (UiEvent) -> Unit
){

    LazyColumn(
        modifier = modifier,
        contentPadding = innerPadding
    ) {

        items(
            items = state.favoriteRoutes,
            key = { favoriteRoute -> favoriteRoute.id }
        ) { favoriteRoute ->
            val departureAirport = state.allAirports.find { airport ->
                airport.iataCode == favoriteRoute.departureCode
            }
            val arrivalAirport = state.allAirports.find { airport ->
                airport.iataCode == favoriteRoute.destinationCode
            }

            var favorite by remember { mutableStateOf(favoriteRoute.departureCode == departureAirport?.iataCode
                    && favoriteRoute.destinationCode == arrivalAirport?.iataCode) }

            FlightDetailsCard(
                modifier = Modifier,
                arrivalAirport = arrivalAirport!!,
                departureAirport = departureAirport!!,
                onFavoriteClicked = {
                    onEvent( UiEvent.ToggleFavorite(departureAirport.iataCode, arrivalAirport.iataCode))
                    favorite = !favorite
                },
                isFavorite = favorite

            )
        }

    }

}

@Preview
@Composable
fun HomeScreenDetailsListPreview() {
    FlightSearchAppTheme {
        HomeScreenDetailsList(
            state = HomeScreenUiState(

                allAirports =
                    (List(5) { index ->
                        Airport(
                            index,
                            "OPO",
                            "Inernational Aiport", 90)}
                            ),
                List(3) { index ->
                    FavoriteRoute(
                        index,
                        "OPO",
                        "OPO")
                })
            ,
            onEvent ={}


        )
    }
}


@Preview
@Composable
fun HomeScreenPreview() {
    FlightSearchAppTheme {
        HomeScreen(
            state = HomeScreenUiState(
                allAirports =
                    (List(5) { index ->
                        Airport(
                            index,
                            "OPO",
                            "Inernational Aiport", 90)}
                            ),
                List(3) { index ->
                    FavoriteRoute(
                        index,
                        "OPO",
                        "OPO")
                })
            ,
            onEvent ={}


        )
    }
}
