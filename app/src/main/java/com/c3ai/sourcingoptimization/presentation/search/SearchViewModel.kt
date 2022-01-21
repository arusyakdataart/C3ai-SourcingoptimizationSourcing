package com.c3ai.sourcingoptimization.presentation.search

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.c3ai.sourcingoptimization.domain.model.Alert
import com.c3ai.sourcingoptimization.domain.use_case.SearchUseCases
import com.c3ai.sourcingoptimization.utilities.ErrorMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
/**
 * UI state for the Search route.
 *
 * This is derived from [SearchViewModelState], but split into two possible subclasses to more
 * precisely represent the state available to render the UI.
 */
sealed interface SearchUiState {

    val isLoading: Boolean
    val errorMessages: List<ErrorMessage>
    val searchInput: String

    /**
     * There are no alerts to render.
     *
     * This could either be because they are still loading or they failed to load, and we are
     * waiting to reload them.
     */
    data class NoAlerts(
        override val isLoading: Boolean,
        override val errorMessages: List<ErrorMessage>,
        override val searchInput: String
    ) : SearchUiState

    /**
     * There are alerts to render, as contained in [alerts].
     *
     */
    data class HasAlerts(
        val alerts: List<Alert>,
        override val isLoading: Boolean,
        override val errorMessages: List<ErrorMessage>,
        override val searchInput: String
    ) : SearchUiState
}

/**
 * An internal representation of the Search route state, in a raw form
 */
private data class SearchViewModelState(
    val alerts: List<Alert>? = null,
    val isLoading: Boolean = false,
    val errorMessages: List<ErrorMessage> = emptyList(),
    val searchInput: String = "",
) {

    /**
     * Converts this [SearchViewModelState] into a more strongly typed [SearchUiState] for driving
     * the ui.
     */
    fun toUiState(): SearchUiState =
        if (alerts == null) {
            SearchUiState.NoAlerts(
                isLoading = isLoading,
                errorMessages = errorMessages,
                searchInput = searchInput
            )
        } else {
            SearchUiState.HasAlerts(
                alerts = alerts,
                isLoading = isLoading,
                errorMessages = errorMessages,
                searchInput = searchInput
            )
        }
}

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchUseCases: SearchUseCases
) : ViewModel() {

    private val viewModelState = MutableStateFlow(SearchViewModelState(isLoading = true))
    val uiState = viewModelState
        .map { it.toUiState() }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value.toUiState()
        )

    init {

        // Observe for favorite changes in the repo layer
        viewModelScope.launch {
        }
    }

}