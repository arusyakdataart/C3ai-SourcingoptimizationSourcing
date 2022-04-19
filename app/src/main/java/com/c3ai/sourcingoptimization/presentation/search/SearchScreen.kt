package com.c3ai.sourcingoptimization.presentation.search

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.c3ai.sourcingoptimization.R
import com.c3ai.sourcingoptimization.common.components.*
import com.c3ai.sourcingoptimization.domain.model.Alert
import com.c3ai.sourcingoptimization.modifiers.interceptKey
import com.c3ai.sourcingoptimization.presentation.alerts.AlertCardSimple
import com.c3ai.sourcingoptimization.presentation.common.search.SearchBar
import com.c3ai.sourcingoptimization.presentation.common.search.rememberSearchState

@OptIn(ExperimentalMaterialApi::class, ExperimentalAnimationApi::class)
@Composable
fun SearchScreen(
    navController: NavController,
    scaffoldState: ScaffoldState,
    uiState: SearchUiState,
    onRefresh: () -> Unit,
    onSettingsClick: () -> Unit,
) {
    val context = LocalContext.current

    val state = rememberSearchState(
        initialResults = emptyList<Any>(),
        suggestions = emptyList<Any>(),
        timeoutMillis = 600,
    ) { query: TextFieldValue ->
        emptyList<Any>()
    }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            AnimatedVisibility(
                visible = !state.focused,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically(),
            ) {
                TopAppBar(
                    onSettingsClick = onSettingsClick
                )
            }
        },
        snackbarHost = { C3SnackbarHost(hostState = it) },
    ) { _ ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            AnimatedVisibility(visible = !state.focused) {
                Icon(
                    painter = painterResource(R.drawable.ic_home_logo),
                    contentDescription = "",
                    tint = MaterialTheme.colors.primary,
                    modifier = Modifier.padding(vertical = 80.dp)
                )
            }
            SearchBar(
                query = state.query,
                onQueryChange = { state.query = it },
                onSearchFocusChange = { state.focused = it },
                onClearQuery = { state.query = TextFieldValue("") },
                onBack = { state.query = TextFieldValue("") },
                searching = state.searching,
                focused = state.focused,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

/**
 * The home screen displaying the feed along with an article details.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SearchWithAlertsScreen(
    navController: NavController,
    scaffoldState: ScaffoldState,
    uiState: SearchUiState,
    onRefresh: () -> Unit,
) {

}

/**
 * Display a feed of alerts.
 *
 * When a alert is clicked on, [onAlertTapped] will be called.
 *
 * @param alerts (state) the feed to display
 * @param onAlertTapped (event) request navigation to Alert screen
 * @param modifier modifier for the root element
 */
@Composable
private fun AlertList(
    alerts: List<Alert>,
    showExpandedSearch: Boolean,
    onAlertTapped: (postId: String) -> Unit,
    onToggleAlert: (String) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    state: LazyListState = rememberLazyListState(),
    searchInput: String = "",
    onSearchInputChanged: (String) -> Unit,
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding,
        state = state
    ) {
        if (showExpandedSearch) {
            item {
                HomeSearch(
                    Modifier.padding(horizontal = 16.dp),
                    searchInput = searchInput,
                    onSearchInputChanged = onSearchInputChanged,
                )
            }
        }
        item {
            AlertsListSimpleSection(
                alerts,
                onAlertTapped,
                onToggleAlert
            )
        }
    }
}

/**
 * Full-width list items for [AlertList]
 *
 * @param alerts (state) to display
 * @param navigateToAlert (event) request navigation to Alerts screen
 */
@Composable
private fun AlertsListSimpleSection(
    alerts: List<Alert>,
    navigateToAlert: (String) -> Unit,
    onToggleAlert: (String) -> Unit
) {
    Column {
        alerts.forEach { alert ->
            AlertCardSimple(
                alert = alert,
                navigateTo = navigateToAlert,
                onToggleFavorite = { onToggleAlert(alert.id) }
            )
            ListDivider()
        }
    }
}

/**
 * Full-width list items that display "based on your history" for [PostList]
 *
 * @param posts (state) to display
 * @param navigateToArticle (event) request navigation to Article screen
 */
//@Composable
//private fun ListHistorySection(
//    posts: List<Post>,
//    navigateToArticle: (String) -> Unit
//) {
//    Column {
//        posts.forEach { post ->
//            PostCardHistory(post, navigateToArticle)
//            PostListDivider()
//        }
//    }
//}

/**
 * Full-width divider with padding
 */
@Composable
private fun ListDivider() {
    Divider(
        modifier = Modifier.padding(horizontal = 14.dp),
        color = MaterialTheme.colors.onSurface.copy(alpha = 0.08f)
    )
}

/**
 * Expanded search UI - includes support for enter-to-send and escape-to-dismiss on the search field
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun HomeSearch(
    modifier: Modifier = Modifier,
    searchInput: String = "",
    onSearchInputChanged: (String) -> Unit,
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(Dp.Hairline, MaterialTheme.colors.onSurface.copy(alpha = .6f)),
        elevation = 4.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp)
        ) {
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                IconButton(onClick = { /* Functionality not supported yet */ }) {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = stringResource(R.string.cd_search)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                val context = LocalContext.current
                val focusManager = LocalFocusManager.current
                val keyboardController = LocalSoftwareKeyboardController.current
                OutlinedTextField(
                    value = searchInput,
                    onValueChange = { onSearchInputChanged(it) },
                    placeholder = {
                        Text(
                            text = stringResource(R.string.search_supplier_item_po),
                            color = MaterialTheme.colors.secondary
                        )
                    },
                    singleLine = true,
                    textStyle = TextStyle(fontSize = 16.sp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        unfocusedBorderColor = MaterialTheme.colors.secondary
                    ),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    // keyboardActions submits the search query when the search key is pressed
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            submitSearch(onSearchInputChanged, context)
                            keyboardController?.hide()
                        }
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .interceptKey(Key.Enter) { // submit a search query when Enter is pressed
                            submitSearch(onSearchInputChanged, context)
                        }
                        .interceptKey(Key.Escape) { // dismiss focus when Escape is pressed
                            focusManager.clearFocus()
                        }
                )
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = { /* Functionality not supported yet */ }) {
                    Icon(
                        imageVector = Icons.Filled.Clear,
                        contentDescription = stringResource(R.string.cd_more_actions)
                    )
                }
            }
        }
    }
}

/**
 * Stub helper function to submit a user's search query
 */
private fun submitSearch(
    onSearchInputChanged: (String) -> Unit,
    context: Context
) {
    onSearchInputChanged("")
    Toast.makeText(
        context,
        "Search is not yet implemented",
        Toast.LENGTH_SHORT
    ).show()
}

/**
 * TopAppBar for the Home screen
 */
@Composable
private fun TopAppBar(
    showSearchButtons: Boolean = false,
    onSettingsClick: () -> Unit,
) {
    C3TopAppBar(
        title = "",
        showLogo = showSearchButtons,
        navigationIcon = {
            IconButton(onClick = onSettingsClick) {
                Icon(
                    imageVector = Icons.Outlined.Settings,
                    contentDescription = stringResource(R.string.cd_settings),
                    tint = MaterialTheme.colors.primary
                )
            }
        },
        actions = {
            if (showSearchButtons) {
                IconButton(onClick = { /* TODO: Open search */ }) {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = stringResource(R.string.cd_search)
                    )
                }
            } else {
                IconButton(onClick = { /* TODO: Open search */ }) {
                    Icon(
                        imageVector = Icons.Filled.WarningAmber,
                        contentDescription = stringResource(R.string.cd_search)
                    )
                }
            }
        },
    )
}