package com.c3ai.sourcingoptimization.presentation.search

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.c3ai.sourcingoptimization.domain.model.Alert
import com.c3ai.sourcingoptimization.domain.settings.C3AppSettingsProvider
import com.c3ai.sourcingoptimization.domain.settings.C3AppSettingsProviderImpl.Companion.SEARCH_MODE
import com.c3ai.sourcingoptimization.domain.settings.SettingsState
import com.c3ai.sourcingoptimization.domain.use_case.SearchUseCases
import com.c3ai.sourcingoptimization.presentation.ViewModelState
import com.c3ai.sourcingoptimization.utilities.ErrorMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
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
    val selectedFilters: List<Int>

    /**
     * There are search results to render, as contained in [alerts].
     *
     */
    data class SearchResults(
        override val isLoading: Boolean,
        override val errorMessages: List<ErrorMessage>,
        override val selectedFilters: List<Int> = emptyList(),
    ) : SearchUiState

    /**
     * There are no alerts to render.
     *
     * This could either be because they are still loading or they failed to load, and we are
     * waiting to reload them.
     */
    data class NoAlerts(
        override val isLoading: Boolean,
        override val errorMessages: List<ErrorMessage>,
        override val selectedFilters: List<Int> = emptyList(),
    ) : SearchUiState

    /**
     * There are alerts to render, as contained in [alerts].
     *
     */
    data class HasAlerts(
        val alerts: List<Alert>,
        val selectedAlert: Alert?,
        override val isLoading: Boolean,
        override val errorMessages: List<ErrorMessage>,
        override val selectedFilters: List<Int> = emptyList(),
    ) : SearchUiState
}

/**
 * An internal representation of the Search route state, in a raw form
 */
private data class SearchViewModelState(
    override val settings: SettingsState,
    val alerts: List<Alert>? = null,
    val isLoading: Boolean = false,
    val errorMessages: List<ErrorMessage> = emptyList(),
    val selectedFilters: Set<Int> = emptySet(),
) : ViewModelState() {

    /**
     * Converts this [SearchViewModelState] into a more strongly typed [SearchUiState] for driving
     * the ui.
     */
    fun toUiState(): SearchUiState =
        if (settings.searchMode == SEARCH_MODE) {
            SearchUiState.SearchResults(
                isLoading = isLoading,
                errorMessages = errorMessages,
                selectedFilters = selectedFilters.toList(),
            )
        } else {
            if (alerts == null) {
                SearchUiState.NoAlerts(
                    isLoading = isLoading,
                    errorMessages = errorMessages,
                    selectedFilters = selectedFilters.toList(),
                )
            } else {
                SearchUiState.HasAlerts(
                    alerts = alerts,
                    selectedAlert = null,
                    isLoading = isLoading,
                    errorMessages = errorMessages,
                    selectedFilters = selectedFilters.toList(),
                )
            }
        }
}
/**
* ViewModel class which provides all necessary functionality for searching.
* */
@HiltViewModel
class SearchViewModel @Inject constructor(
    settingsProvider: C3AppSettingsProvider,
    val useCases: SearchUseCases
) : ViewModel(), Observer<SettingsState> {

    private val viewModelState = MutableStateFlow(
        SearchViewModelState(
            settings = settingsProvider.state,
            isLoading = true
        )
    )
    private val settingsLiveData = settingsProvider.asLiveData()

    val uiState = viewModelState
        .map { it.toUiState() }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value.toUiState()
        )

    init {
        settingsLiveData.observeForever(this)
    }

    /**
     * Update state by user event.
     */
    fun onEvent(event: SearchEvent) {
        when (event) {
            is SearchEvent.OnFilterClick -> {
                viewModelState.update { state ->
                    state.copy(
                        selectedFilters = state.selectedFilters.toMutableSet().apply {
                            val isRemoved = remove(event.index)
                            isRemoved || add(event.index)
                        }
                    )
                }
            }
            is SearchEvent.OnSearchRecentClick -> {
                viewModelState.update { state ->
                    state.copy(
                        selectedFilters = event.item.filters?.toSet() ?: emptySet()
                    )
                }
            }
        }
    }

    override fun onChanged(settingsState: SettingsState) {
        viewModelState.update { state ->
            state.copy(settings = settingsState)
        }
    }

    override fun onCleared() {
        settingsLiveData.removeObserver(this)
    }

}