package com.example.flightsearchapp.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.flightsearchapp.data.Airport
import com.example.flightsearchapp.ui.AppUiState

@Composable
fun SearchResults(
    onClick: (Airport) -> Unit,
    uiState: AppUiState,

    ){
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        items(uiState.searchResults) { airport ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .clickable(
                        onClick = {
                            onClick(airport)




                        }
                    ),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = airport.iataCode,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier

                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = airport.airportName,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

            }

        }

    }

}