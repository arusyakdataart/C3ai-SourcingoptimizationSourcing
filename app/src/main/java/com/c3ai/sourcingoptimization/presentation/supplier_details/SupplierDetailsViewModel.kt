package com.c3ai.sourcingoptimization.presentation.supplier_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.c3ai.sourcingoptimization.R
import com.c3ai.sourcingoptimization.data.C3Result
import com.c3ai.sourcingoptimization.domain.model.C3Item
import com.c3ai.sourcingoptimization.domain.model.C3Supplier
import com.c3ai.sourcingoptimization.domain.model.POLine
import com.c3ai.sourcingoptimization.domain.use_case.SuppliersDetailsUseCases
import com.c3ai.sourcingoptimization.utilities.ErrorMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

/**
 * UI state for the Home route.
 *
 * This is derived from [SupplierDetailsUiState], but split into two possible subclasses to more
 * precisely represent the state available to render the UI.
 */
sealed interface SupplierDetailsUiState {

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
    ) : SupplierDetailsUiState

    /**
     * There are details to render, as contained in [supplier].
     *
     */
    data class HasDetails(
        val supplier: C3Supplier,
        val poLines: List<POLine> = emptyList(),
        val items: List<C3Item> = emptyList(),
        override val isLoading: Boolean,
        override val errorMessages: List<ErrorMessage>,
        override val searchInput: String
    ) : SupplierDetailsUiState
}

/**
 * An internal representation of the SupplierDetails route state, in a raw form
 */
private data class SupplierDetailsViewModelState(
    val supplier: C3Supplier? = null,
    val isLoading: Boolean = false,
    val errorMessages: List<ErrorMessage> = emptyList(),
    val searchInput: String = "",
) {

    /**
     * Converts this [SupplierDetailsViewModelState] into
     * a more strongly typed [SupplierDetailsUiState] for driving the ui.
     */
    fun toUiState(): SupplierDetailsUiState =
        if (supplier == null) {
            SupplierDetailsUiState.NoDetails(
                isLoading = isLoading,
                errorMessages = errorMessages,
                searchInput = searchInput
            )
        } else {
            SupplierDetailsUiState.HasDetails(
                supplier = supplier,
                poLines = supplier.purchaseOrders,
                items = supplier.items,
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
class SuppliersDetailsViewModel @Inject constructor(
    private val useCases: SuppliersDetailsUseCases
) : ViewModel() {

    private val viewModelState = MutableStateFlow(SupplierDetailsViewModelState(isLoading = true))

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
            val itemsResult = useCases.getSupplierDetails("supplier0")
            viewModelState.update {
                when (itemsResult) {
                    is C3Result.Success -> it.copy(supplier = itemsResult.data, isLoading = false)
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
     * Notify that the user updated the search query
     */
    fun onSearchInputChanged(searchInput: String) {
        viewModelState.update {
            it.copy(searchInput = searchInput)
        }
    }
}