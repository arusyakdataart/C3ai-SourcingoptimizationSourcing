package com.c3ai.sourcingoptimization.common.components

import android.widget.AdapterView
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun Spinner(
    items: List<String> = emptyList(),
    onItemSelectedListener: (Int, String) -> Unit,
) {

    // State variables
    var selected: String by remember { mutableStateOf(items[0]) }
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Row(
            Modifier
                .padding(24.dp)
                .clickable {
                    expanded = !expanded
                }
                .padding(8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) { // Anchor view
            Text(
                text = selected,
                fontSize = 18.sp,
                modifier = Modifier.padding(end = 8.dp)
            ) // Country name label
            Icon(imageVector = Icons.Filled.ArrowDropDown, contentDescription = "")

            //
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