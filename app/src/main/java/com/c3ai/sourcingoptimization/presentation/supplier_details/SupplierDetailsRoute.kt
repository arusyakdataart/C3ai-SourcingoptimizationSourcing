package com.c3ai.sourcingoptimization.presentation.supplier_details

import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.c3ai.sourcingoptimization.presentation.navigateToItemDetails
import com.c3ai.sourcingoptimization.presentation.navigateToAlerts
import com.c3ai.sourcingoptimization.presentation.navigateToPoDetails

/**
 * Displays the Supplier Details route.
 *
 * Note: AAC ViewModels don't work with Compose Previews currently.
 *
 * @param viewModel ViewModel that handles the business logic of this screen
 * @param scaffoldState (state) state for the [Scaffold] component on this screen
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SupplierDetailsRoute(
    navController: NavController,
    supplierId: String?,
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    viewModel: SuppliersDetailsViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    SupplierDetailsScreen(
        scaffoldState = scaffoldState,
        uiState = uiState,
        onRefreshDetails = { viewModel.refreshDetails() },
        onSearchInputChanged = { viewModel.onEvent(SupplierDetailsEvent.OnSearchInputChanged(it)) },
        supplierId = supplierId ?: "",
        onTabItemClick = { viewModel.onEvent(SupplierDetailsEvent.OnTabItemClick(it)) },
        onExpandableItemClick = { viewModel.onEvent(SupplierDetailsEvent.OnExpandableItemClick(it)) },
        onPOItemClick = { navController.navigateToPoDetails(it) },
        onC3ItemClick = { navController.navigateToItemDetails(it) },
        onAlertsClick = {
//            navController.navigateToEditSuppliers(
//                it,
//                Gson().toJson(
//                    listOf(
//                        "supplier42",
//                        "supplier32",
//                        "supplier48",
//                        "supplier8",
//                        "supplier38"
//                    )
//                )
//            )

//            navController.navigateToEditIndex("mpi0")
            navController.navigateToAlerts()
        },
        onSortChanged = { viewModel.onEvent(SupplierDetailsEvent.OnSortChanged(it)) },
        onBackButtonClick = { navController.navigateUp() },
    )
}
