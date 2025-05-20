package com.example.flightsearchapp.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.Hyphens
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.flightsearchapp.ui.AppUiState

@Composable
fun NoResultsFound(
    uiState: AppUiState
){
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    )
    {
        Column (
            modifier = Modifier.padding(48.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                modifier = Modifier.size(48.dp),
                imageVector = Icons.Sharp.Search,
                contentDescription = null,

                )
            Spacer(modifier = Modifier.padding(4.dp))

            Text(
                text = "No results for \"${uiState.searchQuery}\"",
                style = TextStyle(
                    hyphens = Hyphens.Auto,
                    fontSize = MaterialTheme.typography.titleMedium.fontSize,

                    ),
                overflow = TextOverflow.Visible,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Check the spelling or try a new search",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}