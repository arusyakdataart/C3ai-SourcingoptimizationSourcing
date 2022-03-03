package com.c3ai.sourcingoptimization.presentation.po_details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.c3ai.sourcingoptimization.R
import com.c3ai.sourcingoptimization.data.C3Result
import com.c3ai.sourcingoptimization.domain.model.PurchaseOrder
import com.c3ai.sourcingoptimization.domain.settings.C3AppSettingsProvider
import com.c3ai.sourcingoptimization.domain.settings.FakeC3AppSettingsProvider
import com.c3ai.sourcingoptimization.domain.use_case.PODetailsUseCases
import com.c3ai.sourcingoptimization.presentation.ViewModelState
import com.c3ai.sourcingoptimization.presentation.views.UiPurchaseOrder
import com.c3ai.sourcingoptimization.presentation.views.convert
import com.c3ai.sourcingoptimization.utilities.ErrorMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

/**
 * UI state for the PO details route.
 *
 * This is derived from [PODetailsUiState], but split into two possible subclasses to more
 * precisely represent the state available to render the UI.
 */
sealed interface PODetailsUiState {
    val isLoading: Boolean
    val errorMessages: List<ErrorMessage>
    val searchInput: String

    /**
     * There are no details to render.
     *
     * This could either be because they are still loading or they failed to load, and we are
     * waiting to reload them.
     */
    data class NoDetails(
        override val isLoading: Boolean,
        override val errorMessages: List<ErrorMessage>,
        override val searchInput: String
    ) : PODetailsUiState

    /**
     * There are details to render, as contained in model[UiPurchaseOrder.Order].
     *
     */
    data class HasDetails(
        val order: UiPurchaseOrder.Order,
        val poLines: List<UiPurchaseOrder.Line>,
        override val isLoading: Boolean,
        override val errorMessages: List<ErrorMessage>,
        override val searchInput: String
    ) : PODetailsUiState
}

/**
 * An internal representation of the PO details route[PODetailsRoute] state, in a raw form
 */
private data class PODetailsViewModelState(
    override val settings: C3AppSettingsProvider,
    val order: PurchaseOrder.Order? = null,
    val poLines: List<PurchaseOrder.Line>? = null,
    val isLoading: Boolean = false,
    val errorMessages: List<ErrorMessage> = emptyList(),
    val searchInput: String = "",
    val expandedListItemIds: Set<String> = emptySet()
) : ViewModelState() {

    /**
     * Converts this [PODetailsViewModelState] into
     * a more strongly typed [PODetailsUiState] for driving the ui.
     */
    fun toUiState(): PODetailsUiState =
        if (order != null && poLines != null) {
            PODetailsUiState.HasDetails(
                order = convert(order),
                poLines = poLines.map { convert(it) },
                isLoading = isLoading,
                errorMessages = errorMessages,
                searchInput = searchInput
            )
        } else {
            PODetailsUiState.NoDetails(
                isLoading = isLoading,
                errorMessages = errorMessages,
                searchInput = searchInput
            )
        }
}

/**
 * ViewModel that handles the business logic of the Home screen
 */
@HiltViewModel
class PODetailsViewModel @Inject constructor(
    settings: C3AppSettingsProvider,
    private val savedStateHandle: SavedStateHandle,
    private val useCases: PODetailsUseCases
) : ViewModel() {

    private val viewModelState = MutableStateFlow(
        PODetailsViewModelState(
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
            val orderId = savedStateHandle.get<String>("orderId") ?: ""
            val poDetails = useCases.getPODetails(orderId)
            viewModelState.update {
                when (poDetails) {
                    is C3Result.Success -> it.copy(order = poDetails.data, isLoading = false)
                    is C3Result.Error -> {
                        val errorMessages = it.errorMessages + ErrorMessage(
                            id = UUID.randomUUID().mostSignificantBits,
                            messageId = R.string.load_error
                        )
                        it.copy(errorMessages = errorMessages, isLoading = false)
                    }
                }
            }

            val itemsResult = useCases.getPoLines(orderId, "")
            viewModelState.update {
                when (itemsResult) {
                    is C3Result.Success -> it.copy(poLines = itemsResult.data, isLoading = false)
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

    private fun reloadPOLines(sortOption: String) {
        val orderId = savedStateHandle.get<String>("orderId") ?: ""
        viewModelState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            val itemsResult = useCases.getPoLines(orderId, sortOption)
            viewModelState.update {
                when (itemsResult) {
                    is C3Result.Success -> it.copy(poLines = itemsResult.data, isLoading = false)
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

    fun onEvent(event: PODetailsEvent) {
        when (event) {
            is PODetailsEvent.OnSortChanged -> {
                reloadPOLines(event.sortOption)
            }
        }
    }
}

@Suppress("FunctionName")
fun PreviewPODetailsUiState(order: PurchaseOrder.Order): PODetailsUiState {
    return PODetailsViewModelState(
        settings = FakeC3AppSettingsProvider(),
        order = order,
        isLoading = false,
        errorMessages = emptyList(),
        searchInput = ""
    ).toUiState()
}