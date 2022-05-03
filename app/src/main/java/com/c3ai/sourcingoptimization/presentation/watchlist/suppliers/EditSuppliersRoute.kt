package com.c3ai.sourcingoptimization.presentation.watchlist.suppliers

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.c3ai.sourcingoptimization.presentation.navigateToSupplierDetails

/**
 * Displays the Edit Suppliers route.
 *
 * Note: AAC ViewModels don't work with Compose Previews currently.
 *
 * @param viewModel ViewModel that handles the business logic of this screen
 * @param scaffoldState (state) state for the [Scaffold] component on this screen
 */

@ExperimentalFoundationApi
@Composable
fun EditSuppliersRoute(
    navController: NavController,
    itemId: String?,
    suppliers: List<String>?,
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    viewModel: EditSuppliersViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    EditSuppliersScreen(
        navController = navController,
        viewModel = viewModel,
        scaffoldState = scaffoldState,
        uiState = uiState,
        itemId = itemId ?: "",
        suppliers = suppliers ?: listOf(),
        onRefreshDetails = { viewModel.refreshDetails(page = 0) },
        onSearchInputChanged = { viewModel.onEvent(EditSuppliersEvent.OnSearchInputChanged(it)) },
        onSupplierClick = { navController.navigateToSupplierDetails(it)},
        onCheckSupplier = { viewModel.onEvent(EditSuppliersEvent.OnSupplierChecked(it)) },
        onUncheckSupplier = { viewModel.onEvent(EditSuppliersEvent.OnSupplierUnchecked(it)) },
        onBackButtonClick = { navController.navigateUp() },
        onRetry = { viewModel.onEvent(EditSuppliersEvent.OnRetry("")) },
        onError = { viewModel.onEvent(EditSuppliersEvent.OnError("")) }
    )
}