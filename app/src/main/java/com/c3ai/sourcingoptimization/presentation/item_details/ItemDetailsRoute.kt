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
import com.c3ai.sourcingoptimization.domain.model.C3Vendor
import com.c3ai.sourcingoptimization.domain.model.MarketPriceIndex
import com.c3ai.sourcingoptimization.presentation.navigateToAlerts
import com.c3ai.sourcingoptimization.presentation.navigateToEditIndex
import com.c3ai.sourcingoptimization.presentation.navigateToEditSuppliers
import com.c3ai.sourcingoptimization.presentation.navigateToSupplierDetails
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
fun ItemDetailsRoute(
    navController: NavController,
    itemId: String?,
    suppliers: String?,
    index: String?,
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
        loadData = {
            itemId?.let {
                viewModel.loadData(
                    it,
                    suppliers = Gson().fromJson(suppliers, Array<C3Vendor>::class.java)?.asList(),
                    index = Gson().fromJson(index, MarketPriceIndex::class.java)
                )
            }
        },
        onDateRangeSelected = { viewModel.onEvent(ItemDetailsEvent.OnDateRangeSelected(it)) },
        onStatsTypeSelected = { viewModel.onEvent(ItemDetailsEvent.OnStatsTypeSelected(it)) },
        onSupplierClick = { navController.navigateToSupplierDetails(it) },
        onEditSuppliersClick = { navController.navigateToEditSuppliers(itemId ?: "", it) },
        onEditIndexClick = { navController.navigateToEditIndex(it) },
        onChartViewMoveOver = { viewModel.onEvent(ItemDetailsEvent.UpdateSourcingAnalysis(it)) },
        onSortChanged = { viewModel.onEvent(ItemDetailsEvent.OnSortChanged(it)) },
        onAlertsClick = { navController.navigateToAlerts() },
        onContactClick = { viewModel.onEvent(ItemDetailsEvent.OnSupplierContactSelected(it)) },
    )
}
