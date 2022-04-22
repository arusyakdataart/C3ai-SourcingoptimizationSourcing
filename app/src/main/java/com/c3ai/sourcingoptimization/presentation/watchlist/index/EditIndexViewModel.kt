package com.c3ai.sourcingoptimization.presentation.watchlist.index

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.c3ai.sourcingoptimization.R
import com.c3ai.sourcingoptimization.data.C3Result
import com.c3ai.sourcingoptimization.domain.model.MarketPriceIndex
import com.c3ai.sourcingoptimization.domain.settings.C3AppSettingsProvider
import com.c3ai.sourcingoptimization.domain.settings.SettingsState
import com.c3ai.sourcingoptimization.domain.use_case.EditIndexUseCases
import com.c3ai.sourcingoptimization.presentation.ViewModelState
import com.c3ai.sourcingoptimization.presentation.watchlist.suppliers.EditSuppliersEvent
import com.c3ai.sourcingoptimization.utilities.ErrorMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

/**
 * UI state for the Home route.
 *
 * This is derived from [EditIndexUiState], but split into two possible subclasses to more
 * precisely represent the state available to render the UI.
 */
sealed interface EditIndexUiState {

    val isLoading: Boolean
    val errorMessages: List<ErrorMessage>
    val searchInput: String

    /**
     * There is no data to render.
     *
     * This could either be because they are still loading or they failed to load, and we are
     * waiting to reload them.
     */
    data class NoData(
        override val isLoading: Boolean,
        override val errorMessages: List<ErrorMessage>,
        override val searchInput: String
    ) : EditIndexUiState

    /**
     * There is data to render, as contained in model[indexes].
     *
     */
    data class HasData(
        val indexes: List<MarketPriceIndex>,
        val checkedItemId: String = "",
        override val isLoading: Boolean,
        override val errorMessages: List<ErrorMessage>,
        override val searchInput: String,
    ) : EditIndexUiState
}

/**
 * An internal representation of the EditIndex route state, in a raw form
 */
private data class EditIndexViewModelState(
    override val settings: SettingsState,
    val indexes: List<MarketPriceIndex>? = null,
    val isLoading: Boolean = false,
    val errorMessages: List<ErrorMessage> = emptyList(),
    val searchInput: String = "",
    val checkedItemId: String = ""
) : ViewModelState() {

    /**
     * Converts this [EditIndexViewModelState] into
     * a more strongly typed [EditIndexUiState] for driving the ui.
     */
    fun toUiState(): EditIndexUiState =
        if (indexes != null) {
            EditIndexUiState.HasData(
                indexes = indexes,
                checkedItemId = checkedItemId,
                isLoading = isLoading,
                errorMessages = errorMessages,
                searchInput = searchInput
            )
        } else {
            EditIndexUiState.NoData(
                isLoading = isLoading,
                errorMessages = errorMessages,
                searchInput = searchInput
            )
        }
}

@HiltViewModel
class EditIndexViewModel @Inject constructor(
    settingsProvider: C3AppSettingsProvider,
    private val useCases: EditIndexUseCases
) : ViewModel() {

    private val viewModelState = MutableStateFlow(
        EditIndexViewModelState(
            settings = settingsProvider.state,
            isLoading = true
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

    init {
        refreshDetails()
    }

    /**
     * Refresh index data and update the UI state accordingly
     */
    fun refreshDetails() {
        viewModelState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            val result = useCases.getIndexes()
            viewModelState.update {
                when (result) {
                    is C3Result.Success -> it.copy(indexes = result.data, isLoading = false)
                    is C3Result.Error -> {
                        val errorMessages = it.errorMessages + ErrorMessage(
                            id = UUID.randomUUID().mostSignificantBits,
                            messageId = R.string.load_error
                        )
                        it.copy(errorMessages = errorMessages, isLoading = false)
                    }
                }
            }
        }
    }

    /**
     * Update state by user event.
     */
    fun onEvent(event: EditSuppliersEvent) {

    }
}