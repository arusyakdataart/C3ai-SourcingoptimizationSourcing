package com.c3ai.sourcingoptimization.common.components

import androidx.compose.animation.*
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.c3ai.sourcingoptimization.R
import com.c3ai.sourcingoptimization.domain.model.RecentSearchItem
import com.c3ai.sourcingoptimization.presentation.common.search.SearchBar


/**
 * TopAppBar for the App screens
 */
@Composable
fun C3TopAppBar(
    title: String,
    showLogo: Boolean = false,
    onBackButtonClick: () -> Unit = {},
    navigationIcon: @Composable (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
) {
    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (showLogo) {
                    Icon(
                        painter = painterResource(R.drawable.ic_app_logo),
                        contentDescription = stringResource(R.string.home),
                        tint = MaterialTheme.colors.onBackground,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Text(
                    text = title,
                    style = MaterialTheme.typography.caption,
                    color = MaterialTheme.colors.primary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 4.dp, top = 10.dp)
                )
            }
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

/**
 * TopAppBar for the Home+Alerts screen
 */
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun C3SearchAppBar(
    title: String,
    showLogo: Boolean = false,
    navigationIcon: @Composable (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    suggestions: List<RecentSearchItem> = emptyList(),
    onQueryChange: (String) -> Unit = {},
    onSearch: () -> Unit,
    subContent: @Composable (() -> Unit)?
) {
    val transitionState = remember { MutableTransitionState(initialState = false) }
    Box {
        C3TopAppBar(
            title = title,
            showLogo = showLogo,
            navigationIcon = navigationIcon,
            actions = {
                actions()
                AnimatedVisibility(
                    visible = !transitionState.currentState,
                    enter = scaleIn(),
                    exit = scaleOut(),
                ) {
                    IconButton(onClick = { transitionState.targetState = true }) {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = stringResource(R.string.cd_search)
                        )
                    }
                }
            },
        )
        AnimatedVisibility(
            visibleState = transitionState,
            enter = slideInHorizontally(initialOffsetX = { it }),
            exit = slideOutHorizontally(targetOffsetX = { it }),
        ) {
            SearchBar(
                suggestions = suggestions,
                fixed = true,
                onQueryChange = { onQueryChange(it.text) },
                onBackClick = { transitionState.targetState = false },
                onSearch = onSearch,
                subContent = subContent,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}