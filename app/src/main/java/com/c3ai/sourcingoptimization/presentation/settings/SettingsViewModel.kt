package com.c3ai.sourcingoptimization.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.c3ai.sourcingoptimization.domain.model.C3Vendor
import com.c3ai.sourcingoptimization.domain.settings.C3AppSettingsProvider
import com.c3ai.sourcingoptimization.domain.settings.FakeC3AppSettingsProvider
import com.c3ai.sourcingoptimization.domain.use_case.SuppliersDetailsUseCases
import com.c3ai.sourcingoptimization.presentation.ViewModelState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
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
    override val settings: C3AppSettingsProvider,
) : ViewModelState() {

    /**
     * Converts this [SettingsViewModelState] into
     * a more strongly typed [SettingsUiState] for driving the ui.
     */
    fun toUiState(): SettingsUiState = SettingsUiState(
        currency = settings.getCurrencyType(),
        dateFormat = settings.getDateFormatter().toPattern(),
        searchMode = settings.getSearchMode(),
    )
}

/**
 * ViewModel that handles the business logic of the SuppliersDetails screen
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    settings: C3AppSettingsProvider,
    private val useCases: SuppliersDetailsUseCases
) : ViewModel() {

    private val viewModelState = MutableStateFlow(
        SettingsViewModelState(
            settings = settings,
        )
    )

    // UI state exposed to the UI
    val uiState = viewModelState
        .map { it.toUiState() }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value.toUiState()
        )

    /**
     * Update state by user event.
     */
    fun onEvent(event: SettingsEvent) {
        when (event) {
            is SettingsEvent.OnCurrencyChanged -> {
                viewModelState.value.settings.setCurrencyType(event.newCurrency)
            }
        }
    }
}

@Suppress("FunctionName")
fun PreviewSupplierDetailsUiState(
    supplier: C3Vendor,
    tabIndex: Int = 0,
): SettingsUiState {
    return SettingsViewModelState(
        settings = FakeC3AppSettingsProvider(),
    ).toUiState()
}