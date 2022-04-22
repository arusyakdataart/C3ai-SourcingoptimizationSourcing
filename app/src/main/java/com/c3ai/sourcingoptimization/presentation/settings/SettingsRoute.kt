package com.c3ai.sourcingoptimization.presentation.settings

import android.app.Activity
import android.content.Intent
import androidx.compose.animation.ExperimentalAnimationApi
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
import com.c3ai.sourcingoptimization.authorization.presentation.LaunchActivity
import com.c3ai.sourcingoptimization.presentation.MainActivity

/**
 * Displays the Supplier Details route.
 *
 * Note: AAC ViewModels don't work with Compose Previews currently.
 *
 * @param viewModel ViewModel that handles the business logic of this screen
 * @param scaffoldState (state) state for the [Scaffold] component on this screen
 */
@OptIn(ExperimentalMaterialApi::class, ExperimentalAnimationApi::class)
@Composable
fun SettingsRoute(
    navController: NavController,
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    SettingsScreen(
        scaffoldState = scaffoldState,
        uiState = uiState,
        onBackButtonClick = { navController.navigateUp() },
        onSearchModeChange = { viewModel.onEvent(SettingsEvent.OnSearchModeChanged(it)) },
        onCurrencyChange = { viewModel.onEvent(SettingsEvent.OnCurrencyChanged(it))},
        onDateFormatChange = {viewModel.onEvent(SettingsEvent.OnDateFormatChanged(it))},
        logout = {
            viewModel.onEvent(SettingsEvent.Logout)
            context.startActivity(Intent(context, LaunchActivity::class.java))
            (context as? Activity)?.finish()
        }
    )
}
