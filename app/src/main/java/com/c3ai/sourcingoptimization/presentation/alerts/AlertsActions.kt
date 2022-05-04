package com.c3ai.sourcingoptimization.presentation.alerts

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Sort
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.c3ai.sourcingoptimization.R
import com.c3ai.sourcingoptimization.common.SortType
import com.c3ai.sourcingoptimization.ui.theme.Blue
import com.google.gson.Gson

@Composable
fun AlertsActions(
    uiState: AlertsUiState,
    onSortChanged: (String) -> Unit,
    onChangeFilter: (String) -> Unit

) {
    var sortMenuExpanded by remember { mutableStateOf(false) }
    var sortApplied by remember { mutableStateOf("") }
    var sortType by remember { mutableStateOf(SortType.ASCENDING) }

    Row {
        IconButton(onClick = { sortMenuExpanded = true }) {
            Icon(
                imageVector = Icons.Filled.Sort,
                contentDescription = stringResource(R.string.cd_sort_menu)
            )
        }

        DropdownMenu(
            modifier = Modifier,
            expanded = sortMenuExpanded,
            onDismissRequest = { sortMenuExpanded = false }
        ) {
            val resources = listOf(
                "timestamp" to "Alert Creation Date",
                "flagged" to "Alert Flag Status",
                "currentState" to "Alert State",
                "readStatus" to "Alert Status"
            )
            resources.map { it ->
                DropdownMenuItem(
                    onClick = {
                        sortMenuExpanded = false
                        if (sortApplied == it.first) {
                            sortType =
                                if (sortType == SortType.ASCENDING) SortType.DESCENDING else SortType.ASCENDING
                        } else {
                            sortType = SortType.DESCENDING
                        }
                        val orderType =
                            if (sortType == SortType.DESCENDING) "descending" else "ascending"
                        sortApplied = it.first

                        onSortChanged(orderType + "(" + it.first + ")")
                    },
                ) {
                    Row(modifier = Modifier.fillMaxSize()) {
                        if (sortApplied == it.first) {
                            Icon(
                                imageVector = if (sortType == SortType.ASCENDING) Icons.Filled.ArrowUpward else Icons.Filled.ArrowDownward,
                                contentDescription = "",
                                tint = Blue
                            )
                        } else {
                            Spacer(modifier = Modifier.width(24.dp))
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            it.second,
                            style = MaterialTheme.typography.subtitle1,
                            color = if (sortApplied == it.first) Blue else MaterialTheme.colors.secondaryVariant,
                        )
                    }
                }
            }
        }

        IconButton(
            onClick = {
                onChangeFilter(Gson().toJson((uiState as AlertsUiState.HasData).selectedCategoriesList))
            }
        ) {
            Icon(
                imageVector = Icons.Filled.Settings,
                contentDescription = stringResource(R.string.cd_settings_menu)
            )
        }
    }
}