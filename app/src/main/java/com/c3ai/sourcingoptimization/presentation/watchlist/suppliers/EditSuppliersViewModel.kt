package com.c3ai.sourcingoptimization.presentation.watchlist.suppliers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.c3ai.sourcingoptimization.R
import com.c3ai.sourcingoptimization.data.C3Result
import com.c3ai.sourcingoptimization.domain.model.C3Vendor
import com.c3ai.sourcingoptimization.domain.settings.C3AppSettingsProvider
import com.c3ai.sourcingoptimization.domain.use_case.EditSuppliersUseCases
import com.c3ai.sourcingoptimization.presentation.ViewModelState
import com.c3ai.sourcingoptimization.presentation.views.UiVendor
import com.c3ai.sourcingoptimization.presentation.views.convert
import com.c3ai.sourcingoptimization.utilities.ErrorMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

/**
 * UI state for the Home route.
 *
 * This is derived from [EditSuppliersUiState], but split into two possible subclasses to more
 * precisely represent the state available to render the UI.
 */
sealed interface EditSuppliersUiState {

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
    ) : EditSuppliersUiState

    /**
     * There is data to render, as contained in model[index].
     *
     */
    data class HasData(
        val suppliers: List<C3Vendor>,
        val checkedItemsIds: Set<String> = emptySet(),
        override val isLoading: Boolean,
        override val errorMessages: List<ErrorMessage>,
        override val searchInput: String,
    ) : EditSuppliersUiState
}

/**
 * An internal representation of the EditSuppliers route state, in a raw form
 */
private data class EditSuppliersViewModelState(
    override val settings: C3AppSettingsProvider,
    val suppliers: List<C3Vendor>? = null,
    val isLoading: Boolean = false,
    val errorMessages: List<ErrorMessage> = emptyList(),
    val searchInput: String = "",
    val checkedItemsIds: Set<String> = emptySet()
) : ViewModelState() {

    /**
     * Converts this [EditSuppliersViewModelState] into
     * a more strongly typed [EditSuppliersUiState] for driving the ui.
     */
    fun toUiState(): EditSuppliersUiState =
        if (suppliers != null) {
            EditSuppliersUiState.HasData(
                suppliers = suppliers,
                checkedItemsIds = checkedItemsIds,
                isLoading = isLoading,
                errorMessages = errorMessages,
                searchInput = searchInput
            )
        } else {
            EditSuppliersUiState.NoData(
                isLoading = isLoading,
                errorMessages = errorMessages,
                searchInput = searchInput
            )
        }
}

@HiltViewModel
class EditSuppliersViewModel @Inject constructor(
    settings: C3AppSettingsProvider,
    private val useCases: EditSuppliersUseCases
) : ViewModel() {

    private val viewModelState = MutableStateFlow(
        EditSuppliersViewModelState(
            settings = settings,
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
     * Refresh supplier details and update the UI state accordingly
     */
    fun refreshDetails() {
        viewModelState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            val result = useCases.getSuppliers("item1")
            viewModelState.update {
                when (result) {
                    is C3Result.Success -> it.copy(suppliers = result.data, isLoading = false)
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