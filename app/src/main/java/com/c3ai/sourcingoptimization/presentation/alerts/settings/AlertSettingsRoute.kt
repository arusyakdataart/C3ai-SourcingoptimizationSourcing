package com.c3ai.sourcingoptimization.presentation.alerts.settings

import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.navigation.NavController

/**
 * Displays the Alerts Settings route.
 *
 * @param scaffoldState (state) state for the [Scaffold] component on this screen
 */
@Composable
fun AlertSettingsRoute(
    navController: NavController,
    categories: List<String>,
    scaffoldState: ScaffoldState = rememberScaffoldState(),
) {
    AlertSettingsScreen(
        scaffoldState,
        selectedCategories = categories,
        onBackButtonClick = { navController.navigateUp() }
    )
}