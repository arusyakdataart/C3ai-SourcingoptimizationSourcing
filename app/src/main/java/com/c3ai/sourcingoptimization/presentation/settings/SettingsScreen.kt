package com.c3ai.sourcingoptimization.presentation.settings

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.c3ai.sourcingoptimization.R
import com.c3ai.sourcingoptimization.common.components.*
import com.c3ai.sourcingoptimization.domain.settings.C3AppSettingsProviderImpl.Companion.DATE_FORMAT_DAY_MONTH_YEAR
import com.c3ai.sourcingoptimization.domain.settings.C3AppSettingsProviderImpl.Companion.DATE_FORMAT_MONTH_DAY_YEAR
import com.c3ai.sourcingoptimization.ui.theme.*

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
    onSearchModeChange: (Int) -> Unit,
    onCurrencyChange: (Int) -> Unit,
    onDateFormatChange: (String) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val bottomState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)

    val dateFormats = listOf(DATE_FORMAT_DAY_MONTH_YEAR, DATE_FORMAT_MONTH_DAY_YEAR, )

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                onBackButtonClick = onBackButtonClick,
            )
        },
        snackbarHost = { C3SnackbarHost(hostState = it) },
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                "BA", // TODO!!! get login
                style = MaterialTheme.typography.h4,
                color = Color.White,
                modifier = Modifier.padding(top = 16.dp, bottom = 32.dp)
            )
            Divider()
            Text(
                text = stringResource(id = R.string.currency),
                style = MaterialTheme.typography.h4,
                color = MaterialTheme.colors.primary,
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
            )

            val currencyOptions = listOf("USD", "Local")
            Column(
                modifier = Modifier.fillMaxWidth(),
            ) {
                currencyOptions.forEachIndexed { index, text ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .clickable(onClick = { onCurrencyChange(index) }),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        RadioButton(
                            modifier = Modifier.size(20.dp),
                            selected = index == uiState.currency,
                            onClick = { onCurrencyChange(index) },
                            colors = RadioButtonDefaults.colors(Blue)
                        )
                        Text(
                            text = text,
                            style = MaterialTheme.typography.body2,
                            color = MaterialTheme.colors.primary,
                            modifier = Modifier.padding(start = 16.dp, top = 2.dp)
                        )
                    }
                }
            }
            Divider(modifier = Modifier.padding(top = 8.dp))

            Text(
                text = stringResource(id = R.string.date_format),
                style = MaterialTheme.typography.h4,
                color = MaterialTheme.colors.primary,
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
            )
            val dateOptions = listOf("MM/DD/YYYY", "DD/MM/YYYY")
            Column(
                modifier = Modifier.fillMaxWidth(),
            ) {
                dateOptions.forEachIndexed { index, text ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .clickable(onClick = { onDateFormatChange(dateFormats[index]) }),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        RadioButton(
                            modifier = Modifier.size(20.dp),
                            selected = uiState.dateFormat == dateFormats[index],
                            onClick = { onDateFormatChange(dateFormats[index]) },
                            colors = RadioButtonDefaults.colors(Blue)
                        )
                        Text(
                            text = text,
                            style = MaterialTheme.typography.body2,
                            color = MaterialTheme.colors.primary,
                            modifier = Modifier.padding(start = 16.dp, top = 2.dp)
                        )
                    }
                }
            }
            Divider(modifier = Modifier.padding(top = 8.dp))

            Text(
                text = stringResource(id = R.string.homepage),
                style = MaterialTheme.typography.h4,
                color = MaterialTheme.colors.primary,
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
            )
            val homeOptions = listOf("Search", "Search + Alerts")
            Column(
                modifier = Modifier.fillMaxWidth(),
            ) {
                homeOptions.forEachIndexed { index, text ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .clickable(onClick = { onSearchModeChange(index) }),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        RadioButton(
                            modifier = Modifier.size(20.dp),
                            selected = index == uiState.searchMode,
                            onClick = { onSearchModeChange(index) },
                            colors = RadioButtonDefaults.colors(Blue)
                        )
                        Text(
                            text = text,
                            style = MaterialTheme.typography.body2,
                            color = MaterialTheme.colors.primary,
                            modifier = Modifier.padding(start = 16.dp, top = 2.dp)
                        )
                    }
                }
            }
            Divider(modifier = Modifier.padding(top = 8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 48.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.logout),
                    contentDescription = "logout"
                )
                Text(
                    text = stringResource(id = R.string.logout),
                    style = MaterialTheme.typography.h3,
                    color = MaterialTheme.colors.primary,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
        }
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
            onSearchModeChange = {},
        )
    }
}