package com.c3ai.sourcingoptimization.common.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.c3ai.sourcingoptimization.R

@Composable
fun CardMenu(
    modifier: Modifier = Modifier,
    items: List<String> = emptyList(),
    onItemSelectedListener: (Int, String) -> Unit,
) {

    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Row(
            Modifier
                .size(24.dp)
                .clickable { expanded = !expanded },
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.MoreVert,
                contentDescription = stringResource(R.string.cd_read_more),
                tint = MaterialTheme.colors.primary
            )
            DropdownMenu(expanded = expanded, onDismissRequest = {
                expanded = false
            }) {
                items.forEachIndexed { index, text ->
                    DropdownMenuItem(onClick = {
                        expanded = false
                        onItemSelectedListener(index, text)
                    }) {
                        Text(text = text)
                    }
                }
            }
        }
    }

}