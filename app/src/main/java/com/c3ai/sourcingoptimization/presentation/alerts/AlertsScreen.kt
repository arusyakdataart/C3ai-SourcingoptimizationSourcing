package com.c3ai.sourcingoptimization.presentation.alerts

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ScaffoldState
import androidx.compose.runtime.Composable

/**
 * A display and edit of the alerts list.
 *
 * This sets up the scaffold with the top app bar, and surrounds the content with refresh,
 * loading and error handling.
 */

@ExperimentalFoundationApi
@Composable
fun AlertsScreen(
    scaffoldState: ScaffoldState,
    uiState: AlertsUiState,
    onRefreshDetails: () -> Unit,
    onSearchInputChanged: (String) -> Unit,
    onSortChanged: (String) -> Unit,
    onFilterChanged: (String) -> Unit,
    onBackButtonClick: () -> Unit,
) {

}