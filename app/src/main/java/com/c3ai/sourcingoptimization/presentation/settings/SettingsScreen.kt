package com.c3ai.sourcingoptimization.presentation.settings

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.c3ai.sourcingoptimization.R
import com.c3ai.sourcingoptimization.common.SortType
import com.c3ai.sourcingoptimization.common.components.*
import com.c3ai.sourcingoptimization.data.C3Result
import com.c3ai.sourcingoptimization.data.repository.C3MockRepositoryImpl
import com.c3ai.sourcingoptimization.presentation.views.UiItem
import com.c3ai.sourcingoptimization.presentation.views.UiPurchaseOrder
import com.c3ai.sourcingoptimization.ui.theme.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 * A display of the supplier details screen that has the lists.
 *
 * This sets up the scaffold with the top app bar, and surrounds the content with refresh,
 * loading and error handling.
 *
 * This helper function exists because [SupplierDetailsScreen] is big and have two states,
 * so we need to decompose it with additional function [SupplierDetailsDataScreen].
 */
@OptIn(
    ExperimentalComposeUiApi::class,
    ExperimentalAnimationApi::class,
    ExperimentalMaterialApi::class
)
@Composable
fun SettingsScreen(
    scaffoldState: ScaffoldState,
    uiState: SettingsUiState,
    onBackButtonClick: () -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val bottomState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)

        Scaffold(
            scaffoldState = scaffoldState,
            topBar = {
                TopAppBar(
                    onBackButtonClick = onBackButtonClick,
                )
            },
            snackbarHost = { C3SnackbarHost(hostState = it) },
        ) { innerPadding ->
            val contentModifier = Modifier.padding(innerPadding)
    }

}


/**
 * TopAppBar for the Settings screen
 */
@Composable
private fun TopAppBar(
    onBackButtonClick: () -> Unit,
) {
    C3TopAppBar(
        title = stringResource(R.string.settings),
        onBackButtonClick = onBackButtonClick,
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Preview
@Composable
fun SettingsScreenPreview() {
    val supplier = runBlocking {
        (C3MockRepositoryImpl().getSupplierDetails("") as C3Result.Success).data
    }
    C3AppTheme {
        SettingsScreen(
            scaffoldState = rememberScaffoldState(),
            uiState = PreviewSupplierDetailsUiState(supplier, 1),
            onBackButtonClick = {},
        )
    }
}