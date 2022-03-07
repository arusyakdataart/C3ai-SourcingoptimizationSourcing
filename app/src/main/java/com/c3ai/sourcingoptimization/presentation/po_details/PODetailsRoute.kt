package com.c3ai.sourcingoptimization.presentation.po_details

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

/**
 * Displays the Home route.
 *
 * Note: AAC ViewModels don't work with Compose Previews currently.
 *
 * @param viewModel ViewModel that handles the business logic of this screen
 * @param scaffoldState (state) state for the [Scaffold] component on this screen
 */
@ExperimentalMaterialApi
@Composable
fun PODetailsRoute(
    navController: NavController,
    orderId: String?,
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    viewModel: PODetailsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    PODetailsScreen(
        scaffoldState = scaffoldState,
        uiState = uiState,
        orderId = orderId ?: "",
        onRefreshDetails = { viewModel.refreshDetails() },
        onSearchInputChanged = { viewModel.onEvent(PODetailsEvent.OnSearchInputChanged(it)) },
        onSortChanged = { viewModel.onEvent(PODetailsEvent.OnSortChanged(it)) },
        onBackButtonClick = { navController.navigateUp() },
    )
}
