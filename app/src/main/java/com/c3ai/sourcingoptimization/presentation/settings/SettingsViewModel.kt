package com.c3ai.sourcingoptimization.presentation.settings

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.c3ai.sourcingoptimization.domain.settings.C3AppSettingsProvider
import com.c3ai.sourcingoptimization.domain.settings.FakeC3AppSettingsProvider
import com.c3ai.sourcingoptimization.domain.settings.SettingsState
import com.c3ai.sourcingoptimization.domain.use_case.SuppliersDetailsUseCases
import com.c3ai.sourcingoptimization.presentation.ViewModelState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

/**
 * UI state for the Settings route.
 *
 * This is derived from [SettingsUiState]
 */
data class SettingsUiState(
    val currency: Int,
    val dateFormat: String,
    val searchMode: Int,
)

/**
 * An internal representation of the Settings route state, in a raw form
 */
private data class SettingsViewModelState(
    override val settings: SettingsState,
) : ViewModelState() {

    /**
     * Converts this [SettingsViewModelState] into
     * a more strongly typed [SettingsUiState] for driving the ui.
     */
    fun toUiState(): SettingsUiState = SettingsUiState(
        currency = settings.currencyType,
        dateFormat = settings.dateFormat,
        searchMode = settings.searchMode,
    )
}

/**
 * ViewModel that handles the business logic of the SuppliersDetails screen
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    val settingsProvider: C3AppSettingsProvider,
    private val useCases: SuppliersDetailsUseCases
) : ViewModel(), Observer<SettingsState> {

    private val viewModelState = MutableStateFlow(
        SettingsViewModelState(
            settings = settingsProvider.state,
        )
    )
    private val settingsLiveData = settingsProvider.asLiveData()

    // UI state exposed to the UI
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
    fun onEvent(event: SettingsEvent) {
        when (event) {
            is SettingsEvent.OnCurrencyChanged -> {
                settingsProvider.setCurrencyType(event.newCurrency)
            }
            is SettingsEvent.OnDateFormatChanged -> {
                settingsProvider.setDateFormatter(event.dateFormat)
            }
            is SettingsEvent.OnSearchMode -> {
                settingsProvider.setSearchMode(event.mode)
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

@Suppress("FunctionName")
fun PreviewSettingsUiState(
): SettingsUiState {
    return SettingsViewModelState(
        settings = FakeC3AppSettingsProvider().state,
    ).toUiState()
}