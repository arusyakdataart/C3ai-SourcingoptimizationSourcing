package com.c3ai.sourcingoptimization.presentation.search

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.Toast
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.c3ai.sourcingoptimization.R
import com.c3ai.sourcingoptimization.common.components.JetnewsSnackbarHost
import com.c3ai.sourcingoptimization.common.components.MButton
import com.c3ai.sourcingoptimization.domain.model.Alert
import com.c3ai.sourcingoptimization.modifiers.interceptKey
import com.c3ai.sourcingoptimization.presentation.MainActivity
import com.c3ai.sourcingoptimization.presentation.alerts.AlertCardSimple
import com.c3ai.sourcingoptimization.presentation.rememberContentPaddingForScreen
import com.google.accompanist.insets.imePadding
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.isActive

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SearchScreen(
    uiState: SearchUiState,
    viewModel: SearchViewModel
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_app_logo),
            contentDescription = null
        )
        MButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            text = "ItemDetails",
            onClick = {
                context.startActivity(Intent(context, MainActivity::class.java))
                (context as? Activity)?.finish()
            }
        )
    }
}

/**
 * The home screen displaying the feed along with an article details.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SearchWithAlertsScreen(
    uiState: SearchUiState,
    viewModel: SearchViewModel,
    showTopAppBar: Boolean,
    onRefresh: () -> Unit,
    onErrorDismiss: (Long) -> Unit,
    openDrawer: () -> Unit,
    homeListLazyListState: LazyListState,
    alertDetailLazyListStates: Map<String, LazyListState>,
    scaffoldState: ScaffoldState,
    modifier: Modifier = Modifier,
    onSearchInputChanged: (String) -> Unit,
) {
    HomeScreenWithList(
        uiState = uiState,
        viewModel = viewModel,
        showTopAppBar = showTopAppBar,
        onRefresh = onRefresh,
        onErrorDismiss = onErrorDismiss,
        openDrawer = openDrawer,
        homeListLazyListState = homeListLazyListState,
        scaffoldState = scaffoldState,
        modifier = modifier,
    ) { hasPostsUiState, contentModifier ->
        val contentPadding = rememberContentPaddingForScreen(additionalTop = 8.dp)
        Row(contentModifier) {
            AlertList(
                alerts = emptyList(),
                showExpandedSearch = !showTopAppBar,
                onAlertTapped = {},
                onToggleAlert = {},
                contentPadding = contentPadding,
                modifier = Modifier
                    .width(334.dp)
                    .notifyInput({})
                    .imePadding(), // add padding for the on-screen keyboard
                state = homeListLazyListState,
                searchInput = hasPostsUiState.searchInput,
                onSearchInputChanged = onSearchInputChanged,
            )
            // Crossfade between different detail posts
//            Crossfade(targetState = hasPostsUiState.selectedAlert) { detailAlert ->
//                // Get the lazy list state for this detail view
//                val detailLazyListState by derivedStateOf {
//                    alertDetailLazyListStates.getValue(detailAlert?.id)
//                }
//
//                // Key against the post id to avoid sharing any state between different posts
//                key(detailPost.id) {
//                    LazyColumn(
//                        state = detailLazyListState,
//                        contentPadding = contentPadding,
//                        modifier = Modifier
//                            .padding(horizontal = 16.dp)
//                            .fillMaxSize()
//                            .notifyInput {
//                                onInteractWithDetail(detailPost.id)
//                            }
//                            .imePadding() // add padding for the on-screen keyboard
//                    ) {
//                        stickyHeader {
//                            val context = LocalContext.current
//                            PostTopBar(
//                                isFavorite = hasPostsUiState.favorites.contains(detailPost.id),
//                                onToggleFavorite = { onToggleFavorite(detailPost.id) },
//                                onSharePost = { sharePost(detailPost, context) },
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                                    .wrapContentWidth(Alignment.End)
//                            )
//                        }
//                        postContentItems(detailPost)
//                    }
//                }
//            }
        }
    }
}

/**
 * A [Modifier] that tracks all input, and calls [block] every time input is received.
 */
private fun Modifier.notifyInput(block: () -> Unit): Modifier =
    composed {
        val blockState = rememberUpdatedState(block)
        pointerInput(Unit) {
            while (currentCoroutineContext().isActive) {
                awaitPointerEventScope {
                    awaitPointerEvent(PointerEventPass.Initial)
                    blockState.value()
                }
            }
        }
    }

/**
 * A display of the home screen that has the list.
 *
 * This sets up the scaffold with the top app bar, and surrounds the [hasPostsContent] with refresh,
 * loading and error handling.
 *
 * This helper functions exists because [HomeScreenWithList] and [HomeFeedScreen] are
 * extremely similar, except for the rendered content when there are posts to display.
 */
@Composable
private fun HomeScreenWithList(
    uiState: SearchUiState,
    viewModel: SearchViewModel,
    showTopAppBar: Boolean,
    onRefresh: () -> Unit,
    onErrorDismiss: (Long) -> Unit,
    openDrawer: () -> Unit,
    homeListLazyListState: LazyListState,
    scaffoldState: ScaffoldState,
    modifier: Modifier = Modifier,
    hasPostsContent: @Composable (
        uiState: SearchUiState.HasAlerts,
        modifier: Modifier
    ) -> Unit
) {
    Scaffold(
        scaffoldState = scaffoldState,
        snackbarHost = { JetnewsSnackbarHost(hostState = it) },
        topBar = {
//            if (showTopAppBar) {
//                SearchTopAppBar(
//                    openDrawer = openDrawer,
//                    elevation = if (!homeListLazyListState.isScrolled) 0.dp else 4.dp
//                )
//            }
        },
        modifier = modifier
    ) { innerPadding ->
        val contentModifier = Modifier.padding(innerPadding)

//        LoadingContent(
//            empty = when (uiState) {
//                is HomeUiState.HasPosts -> false
//                is HomeUiState.NoPosts -> uiState.isLoading
//            },
//            emptyContent = { FullScreenLoading() },
//            loading = uiState.isLoading,
//            onRefresh = onRefreshPosts,
//            content = {
//                when (uiState) {
//                    is HomeUiState.HasPosts -> hasPostsContent(uiState, contentModifier)
//                    is HomeUiState.NoPosts -> {
//                        if (uiState.errorMessages.isEmpty()) {
//                            // if there are no posts, and no error, let the user refresh manually
//                            TextButton(
//                                onClick = onRefreshPosts,
//                                modifier.fillMaxSize()
//                            ) {
//                                Text(
//                                    stringResource(id = R.string.home_tap_to_load_content),
//                                    textAlign = TextAlign.Center
//                                )
//                            }
//                        } else {
//                            // there's currently an error showing, don't show any content
//                            Box(contentModifier.fillMaxSize()) { /* empty screen */ }
//                        }
//                    }
//                }
//            }
//        )
    }

    // Process one error message at a time and show them as Snackbars in the UI
    if (uiState.errorMessages.isNotEmpty()) {
        // Remember the errorMessage to display on the screen
        val errorMessage = remember(uiState) { uiState.errorMessages[0] }

        // Get the text to show on the message from resources
        val errorMessageText: String = stringResource(errorMessage.messageId)
        val retryMessageText = stringResource(R.string.retry)

        // If onRefreshPosts or onErrorDismiss change while the LaunchedEffect is running,
        // don't restart the effect and use the latest lambda values.
        val onRefreshPostsState by rememberUpdatedState(onRefresh)
        val onErrorDismissState by rememberUpdatedState(onErrorDismiss)

        // Effect running in a coroutine that displays the Snackbar on the screen
        // If there's a change to errorMessageText, retryMessageText or scaffoldState,
        // the previous effect will be cancelled and a new one will start with the new values
        LaunchedEffect(errorMessageText, retryMessageText, scaffoldState) {
            val snackbarResult = scaffoldState.snackbarHostState.showSnackbar(
                message = errorMessageText,
                actionLabel = retryMessageText
            )
            if (snackbarResult == SnackbarResult.ActionPerformed) {
                onRefreshPostsState()
            }
            // Once the message is displayed and dismissed, notify the ViewModel
            onErrorDismissState(errorMessage.id)
        }
    }
}

/**
 * Display an initial empty state or swipe to refresh content.
 *
 * @param empty (state) when true, display [emptyContent]
 * @param emptyContent (slot) the content to display for the empty state
 * @param loading (state) when true, display a loading spinner over [content]
 * @param onRefresh (event) event to request refresh
 * @param content (slot) the main content to show
 */
@Composable
private fun LoadingContent(
    empty: Boolean,
    emptyContent: @Composable () -> Unit,
    loading: Boolean,
    onRefresh: () -> Unit,
    content: @Composable () -> Unit
) {
    if (empty) {
        emptyContent()
    } else {
        SwipeRefresh(
            state = rememberSwipeRefreshState(loading),
            onRefresh = onRefresh,
            content = content,
        )
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
 * Full screen circular progress indicator
 */
@Composable
private fun FullScreenLoading() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.Center)
    ) {
        CircularProgressIndicator()
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
 * Horizontal scrolling cards for [PostList]
 *
 * @param posts (state) to display
 * @param navigateToArticle (event) request navigation to Article screen
 */
//@Composable
//private fun PostListPopularSection(
//    posts: List<Post>,
//    navigateToArticle: (String) -> Unit
//) {
//    Column {
//        Text(
//            modifier = Modifier.padding(16.dp),
//            text = stringResource(id = R.string.home_popular_section_title),
//            style = MaterialTheme.typography.subtitle1
//        )
//
//        LazyRow(modifier = Modifier.padding(end = 16.dp)) {
//            items(posts) { post ->
//                PostCardPopular(
//                    post,
//                    navigateToArticle,
//                    Modifier.padding(start = 16.dp, bottom = 16.dp)
//                )
//            }
//        }
//        PostListDivider()
//    }
//}

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
private fun SearchTopAppBar(
    elevation: Dp,
) {
    val title = stringResource(id = R.string.app_name)
    TopAppBar(
        title = {
            Icon(
                painter = painterResource(R.drawable.ic_app_logo),
                contentDescription = title,
                tint = MaterialTheme.colors.onBackground,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 4.dp, top = 10.dp)
            )
        },
        navigationIcon = {
            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = stringResource(R.string.cd_settings),
                    tint = MaterialTheme.colors.primary
                )
            }
        },
        actions = {
            IconButton(onClick = { /* TODO: Open search */ }) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = stringResource(R.string.cd_search)
                )
            }
        },
        backgroundColor = MaterialTheme.colors.surface,
        elevation = elevation
    )
}