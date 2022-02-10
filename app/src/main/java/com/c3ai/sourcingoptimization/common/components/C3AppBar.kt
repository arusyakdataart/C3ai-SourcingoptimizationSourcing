package com.c3ai.sourcingoptimization.common.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.c3ai.sourcingoptimization.R


/**
 * TopAppBar for the App screens
 */
@Composable
fun C3TopAppBar(
    title: String,
    onBackButtonClick: () -> Unit,
    navigationIcon: @Composable (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                color = MaterialTheme.colors.primary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 4.dp, top = 10.dp)
            )
        },
        navigationIcon = if (navigationIcon == null) {
            {
                IconButton(onClick = onBackButtonClick) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.cd_navigation_back_button),
                    )
                }
            }
        } else {
            navigationIcon
        },
        actions = actions,
        backgroundColor = MaterialTheme.colors.background,
        elevation = 0.dp
    )
}