package com.c3ai.sourcingoptimization.presentation.search

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.c3ai.sourcingoptimization.R
import com.c3ai.sourcingoptimization.common.components.*
import com.c3ai.sourcingoptimization.data.C3Result
import com.c3ai.sourcingoptimization.domain.model.Alert
import com.c3ai.sourcingoptimization.domain.model.RecentSearchItem
import com.c3ai.sourcingoptimization.domain.model.SearchItem
import com.c3ai.sourcingoptimization.modifiers.interceptKey
import com.c3ai.sourcingoptimization.presentation.alerts.*
import com.c3ai.sourcingoptimization.presentation.common.search.FiltersGridLayout
import com.c3ai.sourcingoptimization.presentation.common.search.SearchBar
import com.c3ai.sourcingoptimization.presentation.navigateToItemDetails
import com.c3ai.sourcingoptimization.presentation.navigateToSupplierDetails
import com.c3ai.sourcingoptimization.ui.theme.PrimaryColor

@OptIn(ExperimentalMaterialApi::class, ExperimentalAnimationApi::class)
@Composable
fun SearchScreen(
    scaffoldState: ScaffoldState,
    uiState: SearchUiState.SearchResults,
    onRefresh: () -> Unit,
    onSettingsClick: () -> Unit,
    onFilterClick: (Int) -> Unit,
    onRecentSearchClick: (RecentSearchItem) -> Unit,
    onSearchResultClick: (SearchItem) -> Unit,
    search: suspend (String, List<Int>?, offset: Int) -> C3Result<List<SearchItem>>,
) {
    var oppened by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            AnimatedVisibility(
                visible = !oppened,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically(),
            ) {
                HomeTopAppBar(
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
            AnimatedVisibility(visible = !oppened) {
                Icon(
                    painter = painterResource(R.drawable.ic_home_logo),
                    contentDescription = "",
                    tint = MaterialTheme.colors.primary,
                    modifier = Modifier.padding(vertical = 80.dp)
                )
            }
            SearchBar(
                onStateChanged = { oppened = it },
                onRecentSearchClick = onRecentSearchClick,
                onSearchResultClick = onSearchResultClick,
                search = search,
                selectedFilters = uiState.selectedFilters,
                modifier = Modifier.fillMaxWidth(),
            ) {
                FiltersGridLayout(
                    filters = stringArrayResource(R.array.searchFilters).toList(),
                    selected = uiState.selectedFilters,
                    modifier = Modifier.padding(top = if (oppened) 10.dp else 30.dp)
                ) {
                    onFilterClick(it)
                }
            }
        }
    }
}

/**
 * The home screen displaying the feed along with an article details.
 */
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
@Composable
fun SearchWithAlertsScreen(
    scaffoldState: ScaffoldState,
    uiState: SearchUiState,
    viewModel: AlertsViewModel,
    alertsUiState: AlertsUiState,
    selectedCategories: List<String>?,
    onCategoriesSelected: () -> Unit,
    onAlertsSortChanged: (String) -> Unit,
    onChangeAlertsFilter: (String) -> Unit,
    onRefresh: () -> Unit,
    onSettingsClick: () -> Unit,
    onFilterClick: (Int) -> Unit,
    onRecentSearchClick: (RecentSearchItem) -> Unit,
    onSearchResultClick: (SearchItem) -> Unit,
    search: suspend (String, List<Int>?, offset: Int) -> C3Result<List<SearchItem>>,
    onCollapsableItemClick: (String) -> Unit,
    onSupplierClick: (String) -> Unit,
    onItemClick: (String) -> Unit,
    onPOClick: (String) -> Unit,
    onContactClick: (String) -> Unit,
) {
    val context = LocalContext.current

    val coroutineScope = rememberCoroutineScope()
    val bottomState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)

    if (selectedCategories != null) {
        onCategoriesSelected()
    }

    ModalBottomSheetLayout(
        sheetState = bottomState,
        sheetContent = {
            ContactSupplierBottomSheetContent(
                alertsUiState.selectedSupplierContact?.phone ?: "",
                alertsUiState.selectedSupplierContact?.email ?: "",
            )
        }
    ) {
        Scaffold(
            scaffoldState = scaffoldState,
            topBar = {
                SearchTopAppBar(
                    uiState = uiState,
                    alertsUiState = alertsUiState,
                    onAlertsSortChanged = onAlertsSortChanged,
                    onChangeAlertsFilter = onChangeAlertsFilter,
                    onSettingsClick = onSettingsClick,
                    onFilterClick = onFilterClick,
                    onRecentSearchClick = onRecentSearchClick,
                    onSearchResultClick = onSearchResultClick,
                    search = search
                )
            },
            snackbarHost = { C3SnackbarHost(hostState = it) },
        ) { innerPadding ->

            val contentModifier = Modifier.padding(innerPadding)
            LoadingContent(
                empty = when (alertsUiState) {
                    is AlertsUiState.HasData -> false
                    is AlertsUiState.NoData -> uiState.isLoading
                },
                emptyContent = { FullScreenLoading() },
                loading = uiState.isLoading,
                onRefresh = onRefresh,
                content = {
                    Column(modifier = Modifier.fillMaxSize()) {

                        Text(
                            text = stringResource(id = R.string.alerts_for_you),
                            style = MaterialTheme.typography.caption,
                            color = PrimaryColor,
                            modifier = Modifier.padding(all = 16.dp)
                        )

                        AlertsContent(
                            uiState = alertsUiState,
                            viewModel = viewModel,
                            coroutineScope = coroutineScope,
                            bottomState = bottomState,
                            modifier = contentModifier,
                            onRefreshDetails = onRefresh,
                            onCollapsableItemClick = onCollapsableItemClick,
                            onSupplierClick = onSupplierClick,
                            onItemClick = onItemClick,
                            onPOClick = onPOClick,
                            onContactClick = onContactClick
                        )
                    }
                }
            )
        }
    }
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
@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun HomeTopAppBar(
    onSettingsClick: () -> Unit,
) {
    C3TopAppBar(
        title = "",
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
            IconButton(onClick = { /* TODO: Open search */ }) {
                Icon(
                    imageVector = Icons.Filled.WarningAmber,
                    contentDescription = stringResource(R.string.cd_search)
                )
            }
        },
    )
}

/**
 * TopAppBar for the Home+Alerts screen
 */
@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun SearchTopAppBar(
    uiState: SearchUiState,
    alertsUiState: AlertsUiState,
    onAlertsSortChanged: (String) -> Unit,
    onChangeAlertsFilter: (String) -> Unit,
    onSettingsClick: () -> Unit,
    onFilterClick: (Int) -> Unit,
    onRecentSearchClick: (RecentSearchItem) -> Unit,
    onSearchResultClick: (SearchItem) -> Unit,
    search: suspend (String, List<Int>?, offset: Int) -> C3Result<List<SearchItem>>,
) {
    C3SearchAppBar(
        title = "",
        showLogo = true,
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
            AlertsActions(
                uiState = alertsUiState,
                onSortChanged = onAlertsSortChanged,
                onChangeFilter = onChangeAlertsFilter
            )
        },
        onRecentSearchClick = onRecentSearchClick,
        onSearchResultClick = onSearchResultClick,
        selectedFilters = uiState.selectedFilters,
        search = search,
    ) {
        FiltersGridLayout(
            filters = stringArrayResource(R.array.searchFilters).toList(),
            selected = uiState.selectedFilters,
            modifier = Modifier
                .padding(top = 10.dp)
                .horizontalScroll(rememberScrollState())
        ) {
            onFilterClick(it)
        }
    }
}