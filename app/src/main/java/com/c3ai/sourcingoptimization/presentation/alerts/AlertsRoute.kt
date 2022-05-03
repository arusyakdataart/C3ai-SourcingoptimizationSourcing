package com.c3ai.sourcingoptimization.presentation.alerts

import androidx.compose.foundation.ExperimentalFoundationApi
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
import com.google.gson.Gson

/**
 * Displays the Alerts route.
 *
 * Note: AAC ViewModels don't work with Compose Previews currently.
 *
 * @param viewModel ViewModel that handles the business logic of this screen
 * @param scaffoldState (state) state for the [Scaffold] component on this screen
 */
@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Composable
fun AlertsRoute(
    navController: NavController,
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    viewModel: AlertsViewModel = hiltViewModel(),
    selectedCategories: String
) {
    val uiState by viewModel.uiState.collectAsState()

    AlertsScreen(
        scaffoldState = scaffoldState,
        viewModel = viewModel,
        uiState = uiState,
        selectedCategories = Gson().fromJson(selectedCategories, Array<String>::class.java)?.asList(),
        onRefreshDetails = { viewModel.refreshDetails(page = 0) },
        onSearchInputChanged = { viewModel.onEvent(AlertsEvent.OnSearchInputChanged(it)) },
        onSortChanged = { viewModel.onEvent(AlertsEvent.OnSortChanged(it)) },
        onChangeFilter = { navController.navigateToAlertSettings(it) },
        onBackButtonClick = { navController.navigateUp() },
        onCollapsableItemClick = { viewModel.onEvent(AlertsEvent.OnCollapsableItemClick(it)) },
        onSupplierClick = { navController.navigateToSupplierDetails(it) },
        onItemClick = { navController.navigateToItemDetails(it) },
        onPOClick = { navController.navigateToPoDetails(it) },
        onContactClick = { viewModel.onEvent(AlertsEvent.OnSupplierContactSelected(it)) },
        onRetry = { viewModel.onEvent(AlertsEvent.OnRetry("")) },
        onError = { viewModel.onEvent(AlertsEvent.OnError("")) }
    )
}