package com.c3ai.sourcingoptimization.presentation.supplier_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.c3ai.sourcingoptimization.R
import com.c3ai.sourcingoptimization.data.C3Result
import com.c3ai.sourcingoptimization.domain.model.C3Item
import com.c3ai.sourcingoptimization.domain.model.C3Vendor
import com.c3ai.sourcingoptimization.domain.settings.C3AppSettingsProvider
import com.c3ai.sourcingoptimization.domain.use_case.SuppliersDetailsUseCases
import com.c3ai.sourcingoptimization.presentation.ViewModelState
import com.c3ai.sourcingoptimization.presentation.models.UiPurchaseOrder
import com.c3ai.sourcingoptimization.presentation.models.UiVendor
import com.c3ai.sourcingoptimization.presentation.models.convert
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
        val supplier: UiVendor,
        val poLines: List<UiPurchaseOrder.Order> = supplier.purchaseOrders,
        val items: List<C3Item> = supplier.items,
        val expandedListItemIds: Set<String> = emptySet(),
        override val isLoading: Boolean,
        override val errorMessages: List<ErrorMessage>,
        override val searchInput: String
    ) : SupplierDetailsUiState
}

/**
 * An internal representation of the SupplierDetails route state, in a raw form
 */
data class SupplierDetailsViewModelState(
    override val settings: C3AppSettingsProvider,
    val supplier: C3Vendor? = null,
    val isLoading: Boolean = false,
    val errorMessages: List<ErrorMessage> = emptyList(),
    val searchInput: String = "",
    val expandedListItemIds: Set<String> = emptySet()
) : ViewModelState() {

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
                supplier = convert(supplier),
                expandedListItemIds = expandedListItemIds,
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
    private val settings: C3AppSettingsProvider,
    private val useCases: SuppliersDetailsUseCases
) : ViewModel() {

    private val viewModelState = MutableStateFlow(
        SupplierDetailsViewModelState(
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

    /**
     * Add or remove item id from list of expanded items.
     */
    fun onExpandableItemClick(itemId: String) {
        viewModelState.update {
            it.copy(expandedListItemIds = it.expandedListItemIds.toMutableSet().apply {
                val isRemoved = remove(itemId)
                isRemoved || add((itemId))
            })
        }
    }
}