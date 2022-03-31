package com.c3ai.sourcingoptimization.presentation.alerts.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.c3ai.sourcingoptimization.R
import com.c3ai.sourcingoptimization.common.AlertTypes
import com.c3ai.sourcingoptimization.common.components.C3SnackbarHost
import com.c3ai.sourcingoptimization.common.components.C3TopAppBar
import com.c3ai.sourcingoptimization.ui.theme.Blue

@Composable
fun AlertSettingsScreen(
    scaffoldState: ScaffoldState,
    selectedCategories: List<String>,
    onBackButtonClick: () -> Unit
) {

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            C3TopAppBar(
                title = stringResource(id = R.string.alert_types_settings),
                onBackButtonClick = onBackButtonClick
            )
        },
        snackbarHost = { C3SnackbarHost(hostState = it) },
    ) {
        val categories = listOf(
            AlertTypes.NEW_LOWEST_PRICE.categoryName,
            AlertTypes.UNEXPECTED_PRICE_INCREASE.categoryName,
            AlertTypes.REQUESTED_DELIVERY_DATE_CHANGE.categoryName,
            AlertTypes.SHORT_CYCLED_PURCHASE_ORDER.categoryName,
            AlertTypes.INDEX_PRICE_CHANGE.categoryName,
            AlertTypes.CORRELATED_INDEX_PRICING_ANOMALY.categoryName,
            AlertTypes.D_U_N_S_RISK.categoryName,
            AlertTypes.RAPID_RATINGS_RISK.categoryName
        )
        val selectedCategoriesList = mutableListOf<String>()
        if (selectedCategories.isEmpty()) {
            selectedCategoriesList.addAll(categories)
        } else {
            selectedCategoriesList.addAll(selectedCategories)
        }
        val listState = rememberLazyListState()
        LazyColumn(modifier = Modifier.fillMaxSize(), listState) {
            items(categories) { category ->

                val isChecked = selectedCategoriesList.contains(category)
                val checkedState = remember { mutableStateOf(isChecked) }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = category,
                        style = MaterialTheme.typography.h3,
                        color = MaterialTheme.colors.primary
                    )
                    Checkbox(
                        checked = checkedState.value,
                        onCheckedChange = { isChecked ->
                            checkedState.value = isChecked
                            if (isChecked) {
                                selectedCategoriesList.add(category)
                            } else {
                                selectedCategoriesList.remove(category)
                            }
                        },
                        colors = CheckboxDefaults.colors(Blue)
                    )
                }
            }
        }
    }
}