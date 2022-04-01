package com.c3ai.sourcingoptimization.common.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun Spinner(
    modifier: Modifier = Modifier,
    items: List<String> = emptyList(),
    selectedPosition: Int = 0,
    onItemSelectedListener: (Int, String) -> Unit,
) {

    // State variables
    var selected: String by remember { mutableStateOf(items[selectedPosition]) }
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Row(
            Modifier.clickable { expanded = !expanded },
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = selected,
                style = MaterialTheme.typography.h3,
                color = MaterialTheme.colors.secondary,
            )
            Icon(imageVector = Icons.Filled.ArrowDropDown, contentDescription = "")
            DropdownMenu(expanded = expanded, onDismissRequest = {
                expanded = false
            }) {
                items.forEachIndexed { index, text ->
                    DropdownMenuItem(onClick = {
                        expanded = false
                        selected = text
                        onItemSelectedListener(index, text)
                    }) {
                        Text(text = text)
                    }
                }
            }
        }
    }

}