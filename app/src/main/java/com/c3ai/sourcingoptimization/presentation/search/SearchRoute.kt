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
import com.c3ai.sourcingoptimization.presentation.*
import com.c3ai.sourcingoptimization.presentation.C3Destinations.SETTINGS_ROUTE
import com.c3ai.sourcingoptimization.presentation.alerts.AlertsEvent
import com.c3ai.sourcingoptimization.presentation.alerts.AlertsViewModel
import com.c3ai.sourcingoptimization.presentation.search.SearchScreenType.SearchHome
import com.c3ai.sourcingoptimization.presentation.search.SearchScreenType.SearchWithAlerts
import com.google.gson.Gson

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
    alertsViewModel: AlertsViewModel = hiltViewModel(),
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    selectedCategories: String
) {
    val uiState by viewModel.uiState.collectAsState()
    val alertsUiState by alertsViewModel.uiState.collectAsState()

    when (getSearchScreenType(uiState)) {
        SearchHome -> {
            SearchScreen(
                scaffoldState = scaffoldState,
                uiState = uiState as SearchUiState.SearchResults,
                onRefresh = {},
                onSettingsClick = { navController.navigate(SETTINGS_ROUTE) },
                onAlertClick = { navController.navigateToAlerts() },
                onSearchResultClick = { navController.navigateFromSearch(it) },
                search = { query, filters, offset ->
                    viewModel.useCases.search(query, filters, offset)
                },
            )
        }
        SearchWithAlerts -> {
            SearchWithAlertsScreen(
                scaffoldState = scaffoldState,
                uiState = alertsUiState,
                viewModel = alertsViewModel,
                selectedCategories = Gson().fromJson(selectedCategories, Array<String>::class.java)
                    ?.asList(),
                onCategoriesSelected = {
                    alertsViewModel.onEvent(
                        AlertsEvent.OnFilterChanged(
                            Gson().fromJson(
                                selectedCategories,
                                Array<String>::class.java
                            )?.asList() ?: emptyList()
                        )
                    )
                },
                onRefresh = {alertsViewModel.refreshDetails(page = 0)},
                onSettingsClick = { navController.navigate(SETTINGS_ROUTE) },
                onSearchResultClick = { navController.navigateFromSearch(it) },
                search = { query, filters, offset ->
                    viewModel.useCases.search(query, filters, offset)
                },
                onCollapsableItemClick = { alertsViewModel.onEvent(AlertsEvent.OnCollapsableItemClick(it)) },
                onSupplierClick = { navController.navigateToSupplierDetails(it) },
                onItemClick = { navController.navigateToItemDetails(it) },
                onPOClick = { navController.navigateToPoDetails(it) },
                onContactClick = { alertsViewModel.onEvent(AlertsEvent.OnSupplierContactSelected(it)) },

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
