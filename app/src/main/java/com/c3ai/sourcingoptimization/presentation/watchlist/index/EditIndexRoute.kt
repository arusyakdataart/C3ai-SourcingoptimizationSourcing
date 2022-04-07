package com.c3ai.sourcingoptimization.presentation.watchlist.index

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
import com.c3ai.sourcingoptimization.presentation.watchlist.suppliers.EditSuppliersEvent

/**
 * Displays the Edit Index route.
 *
 * Note: AAC ViewModels don't work with Compose Previews currently.
 *
 * @param viewModel ViewModel that handles the business logic of this screen
 * @param scaffoldState (state) state for the [Scaffold] component on this screen
 */

@ExperimentalFoundationApi
@Composable
fun EditIndexRoute(
    navController: NavController,
    indexId: String?,
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    viewModel: EditIndexViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    EditIndexScreen(
        scaffoldState = scaffoldState,
        uiState = uiState,
        indexId = indexId ?: "",
        onRefreshDetails = { viewModel.refreshDetails() },
        onSearchInputChanged = { viewModel.onEvent(EditSuppliersEvent.OnSearchInputChanged(it)) },
        onBackButtonClick = { navController.navigateUp() }
    )
}