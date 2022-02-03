package com.c3ai.sourcingoptimization.presentation.search

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel

/**
 * Displays the Home route.
 *
 * Note: AAC ViewModels don't work with Compose Previews currently.
 *
 * @param viewModel ViewModel that handles the business logic of this screen
 * @param isExpandedScreen (state) whether the screen is expanded
 * @param scaffoldState (state) state for the [Scaffold] component on this screen
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SearchRoute(
    viewModel: SearchViewModel = hiltViewModel(),
    scaffoldState: ScaffoldState = rememberScaffoldState()
) {
    val uiState by viewModel.uiState.collectAsState()
    val isExpandedScreen = false

    when (getSearchScreenType(isExpandedScreen, uiState)) {
        SearchScreenType.Search -> {
            SearchScreen(uiState, viewModel)
        }
        SearchScreenType.SearchWithAlerts -> {
            SearchScreen(uiState, viewModel)
        }
        SearchScreenType.SearchWithResults -> {
            SearchScreen(uiState, viewModel)
        }
    }
}

/**
 * A precise enumeration of which type of screen to display at the home route.
 *
 * There are 3 options:
 * - [Search], which displays just search.
 * - [SearchWithAlerts], which displays both a list of alerts and a search.
 * - [SearchWithResults], which displays both a list of alerts and a search.
 */
private enum class SearchScreenType {
    Search,
    SearchWithAlerts,
    SearchWithResults,
}

/**
 * Returns the current [SearchScreenType] to display, based on whether or not the screen is expanded
 * and the [SearchUiState].
 */
@Composable
private fun getSearchScreenType(
    isExpandedScreen: Boolean,
    uiState: SearchUiState
): SearchScreenType = when (isExpandedScreen) {
    false -> {
        when (uiState) {
            is SearchUiState.HasAlerts -> SearchScreenType.SearchWithAlerts
            is SearchUiState.NoAlerts -> SearchScreenType.Search
        }
    }
    true -> SearchScreenType.SearchWithResults
}
