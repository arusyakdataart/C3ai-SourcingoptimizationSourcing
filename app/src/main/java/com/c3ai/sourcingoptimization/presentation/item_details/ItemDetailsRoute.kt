package com.c3ai.sourcingoptimization.presentation.item_details

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.c3ai.sourcingoptimization.presentation.item_details.overview.ItemDetailsViewModel

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
fun ItemDetailsRoute(
    navController: NavController,
    itemId: String?,
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    viewModel: ItemDetailsViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    ItemDetailsScreen(
        scaffoldState = scaffoldState,
        uiState = uiState,
        onRefreshDetails = { viewModel.refresh() },
        itemId = itemId ?: "",
        onTabItemClick = { viewModel.onEvent(ItemDetailsEvent.OnTabItemClick(it)) },
        onBackButtonClick = { navController.navigateUp() },
        loadData = { itemId?.let { viewModel.loadData(it) } },
        onAlertsClick = {},
        onDateRangeSelected = { viewModel.onEvent(ItemDetailsEvent.OnDateRangeSelected(it)) },
        onStatsTypeSelected = { viewModel.onEvent(ItemDetailsEvent.OnStatsTypeSelected(it)) },
        onSupplierClick = {},
        onIndexClick = {},
    )
}
