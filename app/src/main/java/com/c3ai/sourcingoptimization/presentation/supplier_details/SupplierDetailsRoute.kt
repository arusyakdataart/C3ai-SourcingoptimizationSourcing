package com.c3ai.sourcingoptimization.presentation.supplier_details

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
@Composable
fun SupplierDetailsRoute(
    navController: NavController,
    supplierId: String?,
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    viewModel: SuppliersDetailsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    SupplierDetailsScreen(
        navController = navController,
        scaffoldState = scaffoldState,
        uiState = uiState,
        onRefreshDetails = { viewModel.refreshDetails() },
        onSearchInputChanged = { viewModel.onSearchInputChanged(it) },
        supplierId = supplierId ?: "",
        onExpandableItemClick = { viewModel.onExpandableItemClick(it) }
    )
}
