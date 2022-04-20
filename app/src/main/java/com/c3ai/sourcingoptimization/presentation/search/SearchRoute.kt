package com.c3ai.sourcingoptimization.presentation.search

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.c3ai.sourcingoptimization.presentation.C3Destinations.SETTINGS_ROUTE
import com.c3ai.sourcingoptimization.presentation.search.SearchScreenType.*

/**
 * Displays the Home route.
 *
 * Note: AAC ViewModels don't work with Compose Previews currently.
 *
 * @param viewModel ViewModel that handles the business logic of this screen
 * @param scaffoldState (state) state for the [Scaffold] component on this screen
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SearchRoute(
    navController: NavController,
    viewModel: SearchViewModel = hiltViewModel(),
    scaffoldState: ScaffoldState = rememberScaffoldState()
) {
    val uiState by viewModel.uiState.collectAsState()

    when (getSearchScreenType(uiState)) {
        SearchHome -> {
            SearchScreen(
                navController = navController,
                scaffoldState = scaffoldState,
                uiState = uiState,
                onRefresh = {},
                onSettingsClick = { navController.navigate(SETTINGS_ROUTE) },
                onSearch = { searchInput, selectedFilters ->
                    viewModel.search(searchInput, selectedFilters)
                }
            )
        }
        SearchWithAlerts -> {
            SearchWithAlertsScreen(
                navController = navController,
                scaffoldState = scaffoldState,
                uiState = uiState,
                onRefresh = {},
                onSettingsClick = { navController.navigate(SETTINGS_ROUTE) },
                onSearch = { searchInput, selectedFilters ->
                    viewModel.search(searchInput, selectedFilters)
                }
            )
        }
    }
}

/**
 * A precise enumeration of which type of screen to display at the home route.
 *
 * There are 3 options:
 * - [SearchHome], which displays home screen.
 * - [SearchWithAlerts], which displays both a list of alerts and a search.
 */
private enum class SearchScreenType {
    SearchHome,
    SearchWithAlerts,
}

/**
 * Returns the current [SearchScreenType] to display, based on whether or not the screen is expanded
 * and the [SearchUiState].
 */
@Composable
private fun getSearchScreenType(
    uiState: SearchUiState
): SearchScreenType = when (uiState) {
    is SearchUiState.SearchResults -> SearchHome
    else -> SearchWithAlerts
}
