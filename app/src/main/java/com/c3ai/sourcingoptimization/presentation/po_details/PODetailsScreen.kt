package com.c3ai.sourcingoptimization.presentation.po_details

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.c3ai.sourcingoptimization.R
import com.c3ai.sourcingoptimization.common.components.*
import com.c3ai.sourcingoptimization.data.C3Result
import com.c3ai.sourcingoptimization.data.repository.C3MockRepositoryImpl
import com.c3ai.sourcingoptimization.presentation.supplier_details.SupplierDetailsScreen
import com.c3ai.sourcingoptimization.ui.theme.C3AppTheme
import kotlinx.coroutines.runBlocking

/**
 * A display of the purchase order details screen that has the lists.
 *
 * This sets up the scaffold with the top app bar, and surrounds the content with refresh,
 * loading and error handling.
 *
 * This helper function exists because [PODetailsScreen] is big and have two states,
 * so we need to decompose it with additional function [PODetailsDataScreen].
 */
@OptIn(ExperimentalComposeUiApi::class, ExperimentalAnimationApi::class)
@Composable
fun PODetailsScreen(
    scaffoldState: ScaffoldState,
    orderId: String,
    uiState: PODetailsUiState,
    onRefreshDetails: () -> Unit,
    onBackButtonClick: () -> Unit,
) {
    Scaffold(
        scaffoldState = scaffoldState,
        snackbarHost = { C3SnackbarHost(hostState = it) },
        topBar = {
            PODetailsAppBar(
                title = stringResource(R.string.po_, orderId),
                onBackButtonClick = onBackButtonClick
            )
        },
    ) { innerPadding ->
        val contentModifier = Modifier.padding(innerPadding)

        LoadingContent(
            empty = when (uiState) {
                is PODetailsUiState.HasDetails -> false
                is PODetailsUiState.NoDetails -> uiState.isLoading
            },
            emptyContent = { FullScreenLoading() },
            loading = uiState.isLoading,
            onRefresh = onRefreshDetails,
            content = {
                when (uiState) {
                    is PODetailsUiState.HasDetails -> PODetailsDataScreen(
                        uiState = uiState,
                    )
                    is PODetailsUiState.NoDetails -> {
                        if (uiState.errorMessages.isEmpty()) {
                            // if there are no posts, and no error, let the user refresh manually
                            PButton(
                                modifier = Modifier.fillMaxWidth(),
                                text = stringResource(id = R.string.tap_to_load_content),
                                onClick = onRefreshDetails,
                            )
                        } else {
                            // there's currently an error showing, don't show any content
                            Box(contentModifier.fillMaxSize()) { /* empty screen */ }
                        }
                    }
                }
            }
        )
    }

    // Process one error message at a time and show them as Snackbars in the UI
    if (uiState.errorMessages.isNotEmpty()) {
        // Remember the errorMessage to display on the screen
        val errorMessage = remember(uiState) { uiState.errorMessages[0] }

        // Get the text to show on the message from resources
        val errorMessageText: String = stringResource(errorMessage.messageId)
        val retryMessageText = stringResource(id = R.string.retry)

        // If onRefreshPosts or onErrorDismiss change while the LaunchedEffect is running,
        // don't restart the effect and use the latest lambda values.
        val onRefreshPostsState by rememberUpdatedState({ })
        val onErrorDismissState by rememberUpdatedState({ })

        // Effect running in a coroutine that displays the Snackbar on the screen
        // If there's a change to errorMessageText, retryMessageText or scaffoldState,
        // the previous effect will be cancelled and a new one will start with the new values
        LaunchedEffect(errorMessageText, retryMessageText, scaffoldState) {
            val snackbarResult = scaffoldState.snackbarHostState.showSnackbar(
                message = errorMessageText,
                actionLabel = retryMessageText
            )
            if (snackbarResult == SnackbarResult.ActionPerformed) {
                onRefreshPostsState()
            }
            // Once the message is displayed and dismissed, notify the ViewModel
            onErrorDismissState()
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalAnimationApi::class)
@Composable
private fun PODetailsDataScreen(
    uiState: PODetailsUiState.HasDetails,
) {
}

/**
 * TopAppBar for the suppliers details screen[SupplierDetailsScreen]
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun PODetailsAppBar(
    title: String,
    onBackButtonClick: () -> Unit,
) {
    var showClearButton by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }

    C3TopAppBar(
        title = title,
        onBackButtonClick = onBackButtonClick,
        actions = {}
    )
}

@Preview
@Composable
fun ComposablePreview() {
    val order = runBlocking {
        (C3MockRepositoryImpl().getPODetails("") as C3Result.Success).data
    }
    C3AppTheme {
        PODetailsScreen(
            scaffoldState = rememberScaffoldState(),
            orderId = order.id,
            uiState = PreviewPODetailsUiState(order),
            onRefreshDetails = {},
            onBackButtonClick = {},
        )
    }
}