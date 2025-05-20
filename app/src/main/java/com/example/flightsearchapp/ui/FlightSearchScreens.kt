package com.example.flightsearchapp.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.flightsearchapp.data.Airport
import com.example.flightsearchapp.data.FavoriteRoute
import com.example.flightsearchapp.ui.theme.FlightSearchAppTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlightSearchScreen(
    state: AppUiState,
    onEvent: (UiEvent) -> Unit
) {

    Column(
        verticalArrangement = Arrangement.Top,
        modifier = Modifier
            .fillMaxWidth()


        ) {
        if (state.currentAirport == null) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            )
            {
                Text(
                    text = "airport loading...",
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = "please wait",
                    style = MaterialTheme.typography.bodyLarge
                )

            }

        } else {

            Text(
                text = "Flights from ${state.currentAirport.iataCode}",
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(horizontal = 24.dp, vertical = 4.dp )
                    .align(alignment = Alignment.Start))
            FlightSearchDetailList(
                modifier = Modifier.padding(horizontal = 8.dp),
                state = state,
                onEvent = onEvent,

            )


        }

    }

}


@Composable
fun FlightSearchDetailList(
    modifier: Modifier = Modifier,

    state: AppUiState,
    onEvent: (UiEvent) -> Unit,
    contentPadding: PaddingValues = PaddingValues(0.dp),

    ){

    LazyColumn(
        modifier = Modifier,
        contentPadding = contentPadding
    ) {
        items(
            items = state.allAirports,
            key = { airport -> airport.id }
        ) { arrivalAirport ->
            val favoriteRoute = state.favoriteRoutes.find { favoriteRoute ->
                favoriteRoute.departureCode == state.currentAirport?.iataCode
                        && favoriteRoute.destinationCode == arrivalAirport.iataCode
            }

            var favorite by remember { mutableStateOf (favoriteRoute != null)}

                FlightDetailsCard(
                    modifier = modifier,
                    arrivalAirport = arrivalAirport,
                    departureAirport = state.currentAirport,
                    onFavoriteClicked = {
                        onEvent( UiEvent.ToggleFavorite(state.currentAirport!!.iataCode, arrivalAirport.iataCode))
                        favorite = !favorite

                    },
                    isFavorite = favorite
                )


        }

    }




}

@Preview
@Composable
fun FlightSearchScreenPreview() {
    FlightSearchAppTheme {
        FlightSearchScreen(
            state = AppUiState(
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
                }),
            onEvent ={}


        )

    }
}

