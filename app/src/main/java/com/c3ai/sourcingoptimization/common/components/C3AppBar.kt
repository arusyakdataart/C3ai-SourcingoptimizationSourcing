package com.c3ai.sourcingoptimization.common.components

import androidx.compose.animation.*
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.c3ai.sourcingoptimization.R
import com.c3ai.sourcingoptimization.data.C3Result
import com.c3ai.sourcingoptimization.domain.model.RecentSearchItem
import com.c3ai.sourcingoptimization.domain.model.SearchItem
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
    onRecentSearchClick: (RecentSearchItem) -> Unit,
    onSearchResultClick: (SearchItem) -> Unit,
    search: suspend (String, List<Int>?, offset: Int) -> C3Result<List<SearchItem>>,
    selectedFilters: List<Int>? = null,
    subContent: @Composable (() -> Unit)?
) {
    var oppened by rememberSaveable { mutableStateOf(false) }
    Box {
        C3TopAppBar(
            title = title,
            showLogo = showLogo,
            navigationIcon = navigationIcon,
            actions = {
                actions()
                AnimatedVisibility(
                    visible = !oppened,
                    enter = scaleIn(animationSpec = spring(stiffness = Spring.StiffnessLow)),
                    exit = scaleOut(animationSpec = spring(stiffness = Spring.StiffnessLow)),
                ) {
                    IconButton(onClick = { oppened = true }) {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = stringResource(R.string.cd_search)
                        )
                    }
                }
            },
        )
        AnimatedVisibility(
            visible = oppened,
            enter = slideInHorizontally(initialOffsetX = { it }),
            exit = slideOutHorizontally(targetOffsetX = { it }),
        ) {
            SearchBar(
                fixed = true,
                onBackClick = { oppened = false },
                onRecentSearchClick = onRecentSearchClick,
                onSearchResultClick = onSearchResultClick,
                search = search,
                selectedFilters = selectedFilters,
                subContent = subContent,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}