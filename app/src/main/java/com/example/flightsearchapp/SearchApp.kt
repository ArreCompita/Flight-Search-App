package com.example.flightsearchapp

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.flightsearchapp.data.Airport

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlightSearchTopBar(
    title: String,
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    canNavigateBack: Boolean,
    onBackClicked: () -> Unit = {},
    isSearchBarVisible: Boolean,
    onSearchIconClicked: () -> Unit = {}
) {
    TopAppBar(
        title = { Text(title) },
        modifier = modifier,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = onBackClicked) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back)
                    )
                }
            } else{
                if (!isSearchBarVisible) {
                    IconButton(
                        onClick =  onSearchIconClicked
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null
                        )
                    }

                }
                else {
                    IconButton(onClick = onBackClicked ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }

                }
            }
        }, colors = TopAppBarDefaults.topAppBarColors(
            navigationIconContentColor = MaterialTheme.colorScheme.primary
        ), scrollBehavior = scrollBehavior
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBarSurface(
    modifier: Modifier = Modifier,
    colors: TopAppBarColors = TopAppBarDefaults.topAppBarColors(),
    scrollBehavior: TopAppBarScrollBehavior? = null,
    content: @Composable () -> Unit
) {
    val colorTransitionFraction = scrollBehavior?.state?.overlappedFraction ?: 0f
    val fraction = if (colorTransitionFraction > 0.01f) 1f else 0f
    val appBarContainerColor by animateColorAsState(
        targetValue = lerp(
            colors.containerColor,
            colors.scrolledContainerColor,
            FastOutLinearInEasing.transform(fraction)
        ),
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "TopBarSurfaceContainerColorAnimation"
    )
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = appBarContainerColor,
        content = content
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmbeddedSearchBar(
    modifier: Modifier = Modifier,
    onQueryChanged: (String) -> Unit,
    onAirportClick: (Airport) -> Unit,
    isSearchActive: Boolean,
    onActiveChanged: (Boolean) -> Unit,
    searchQuery: String,
    searchResults: List<Airport>,
    onSearch: ((String) -> Unit)? = null,
    onBackClicked: () -> Unit
) {
    SearchBar(
        inputField = {
            SearchBarDefaults.InputField(
                query = searchQuery,
                onQueryChange = { onQueryChanged(it) },
                onSearch = { onSearch },
                expanded = isSearchActive,
                onExpandedChange = onActiveChanged,
                modifier = if (isSearchActive) {
                    modifier.animateContentSize((spring(stiffness = Spring.StiffnessHigh)))
                } else {
                    modifier
                        .padding(start = 12.dp, top = 2.dp, end = 12.dp, bottom = 12.dp)
                        .fillMaxWidth()
                        .animateContentSize(spring(stiffness = Spring.StiffnessHigh))
                },
                placeholder = { Text("Search by airport name or IATA code") },
                leadingIcon = {
                    if (isSearchActive) {
                        IconButton(
                            onClick = {
                                onActiveChanged(false)
                                onBackClicked()
                            },
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                            )
                        }
                    } else {
                        Icon(
                            imageVector = Icons.Rounded.Search,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }, trailingIcon = if (isSearchActive && searchQuery.isNotEmpty()) {
                    {
                        IconButton(
                            onClick = {
                                onQueryChanged("")
                            },
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Close,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                            )
                        }
                    }
                } else {
                    null
                }
            )
        },
        expanded = isSearchActive,
        onExpandedChange = onActiveChanged,
        colors = SearchBarDefaults.colors(
            containerColor = if (isSearchActive) {
                MaterialTheme.colorScheme.background
            } else {
                MaterialTheme.colorScheme.surfaceContainerLow
            },
        ),
        tonalElevation = 0.dp,
        windowInsets = if (isSearchActive) {
            SearchBarDefaults.windowInsets
        } else {
            WindowInsets(0.dp)
        }
    ) {
        if (searchQuery.isNotEmpty()) {

            if (searchResults.isEmpty()) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxSize()
                )
                {
                    Text(
                        text = "No airports found",
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text(
                        text = "Try adjusting your search",
                        style = MaterialTheme.typography.bodyLarge
                    )

                }

            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(searchResults) { airport ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                                .clickable(
                                    onClick = {
                                        onAirportClick(airport)

                                    }
                                ),
                            horizontalArrangement = Arrangement.Start
                        ) {
                            Text(
                                text = airport.iataCode,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .padding(end = 8.dp)
                            )
                            Text(text = airport.airportName)

                        }

                    }
                }
            }
        } else {
            LazyColumn(){}
        }

    }
}