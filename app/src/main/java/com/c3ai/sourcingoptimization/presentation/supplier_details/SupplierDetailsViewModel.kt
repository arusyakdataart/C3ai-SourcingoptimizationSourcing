package com.c3ai.sourcingoptimization.presentation.supplier_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.c3ai.sourcingoptimization.R
import com.c3ai.sourcingoptimization.data.C3Result
import com.c3ai.sourcingoptimization.domain.model.C3Vendor
import com.c3ai.sourcingoptimization.domain.model.PurchaseOrder
import com.c3ai.sourcingoptimization.domain.settings.C3AppSettingsProvider
import com.c3ai.sourcingoptimization.domain.settings.FakeC3AppSettingsProvider
import com.c3ai.sourcingoptimization.domain.use_case.SuppliersDetailsUseCases
import com.c3ai.sourcingoptimization.presentation.ViewModelState
import com.c3ai.sourcingoptimization.presentation.views.UiItem
import com.c3ai.sourcingoptimization.presentation.views.UiPurchaseOrder
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
 * This is derived from [SupplierDetailsUiState], but split into two possible subclasses to more
 * precisely represent the state available to render the UI.
 */
sealed interface SupplierDetailsUiState {

    val isLoading: Boolean
    val errorMessages: List<ErrorMessage>
    val searchInput: String
    val tabIndex: Int

    /**
     * There are no details to render.
     *
     * This could either be because they are still loading or they failed to load, and we are
     * waiting to reload them.
     */
    data class NoDetails(
        override val isLoading: Boolean,
        override val errorMessages: List<ErrorMessage>,
        override val searchInput: String,
        override val tabIndex: Int
    ) : SupplierDetailsUiState

    /**
     * There are details to render, as contained in model[supplier].
     *
     */
    data class HasDetails(
        val supplier: UiVendor,
        val poLines: List<UiPurchaseOrder.Order>,
        val items: List<UiItem> = supplier.items,
        val expandedListItemIds: Set<String> = emptySet(),
        override val isLoading: Boolean,
        override val errorMessages: List<ErrorMessage>,
        override val searchInput: String,
        override val tabIndex: Int
    ) : SupplierDetailsUiState
}

/**
 * An internal representation of the SupplierDetails route state, in a raw form
 */
private data class SupplierDetailsViewModelState(
    override val settings: C3AppSettingsProvider,
    val supplier: C3Vendor? = null,
    val poLines: List<PurchaseOrder.Order>? = null,
    val isLoading: Boolean = false,
    val errorMessages: List<ErrorMessage> = emptyList(),
    val searchInput: String = "",
    val tabIndex: Int = 0,
    val expandedListItemIds: Set<String> = emptySet()
) : ViewModelState() {

    /**
     * Converts this [SupplierDetailsViewModelState] into
     * a more strongly typed [SupplierDetailsUiState] for driving the ui.
     */
    fun toUiState(): SupplierDetailsUiState =
        if (supplier != null && poLines != null) {
            SupplierDetailsUiState.HasDetails(
                supplier = convert(supplier),
                poLines = poLines.map { convert(it) },
                expandedListItemIds = expandedListItemIds,
                isLoading = isLoading,
                errorMessages = errorMessages,
                searchInput = searchInput,
                tabIndex = tabIndex
            )
        } else {
            SupplierDetailsUiState.NoDetails(
                isLoading = isLoading,
                errorMessages = errorMessages,
                searchInput = searchInput,
                tabIndex = 0
            )
        }
}

/**
 * ViewModel that handles the business logic of the Home screen
 */
@HiltViewModel
class SuppliersDetailsViewModel @Inject constructor(
    settings: C3AppSettingsProvider,
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
        getPOs()
    }

    fun getPOs(order: String = "") {
        viewModelState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            val result = useCases.getPOsForSupplier("supplier0", order)
            viewModelState.update {
                when (result) {
                    is C3Result.Success -> it.copy(poLines = result.data, isLoading = false)
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
    fun onEvent(event: SupplierDetailsEvent) {
        viewModelState.update { state ->
            when (event) {
                is SupplierDetailsEvent.OnSearchInputChanged -> {
                    state.copy(searchInput = event.searchInput)
                }
                is SupplierDetailsEvent.OnTabItemClick -> {
                    state.copy(tabIndex = event.tabIndex)
                }
                is SupplierDetailsEvent.OnExpandableItemClick -> {
                    state.copy(
                        expandedListItemIds = state.expandedListItemIds.toMutableSet().apply {
                            val isRemoved = remove(event.itemId)
                            isRemoved || add((event.itemId))
                        })
                }
                else -> {
                    state.copy()
                }
            }
        }

        when (event) {
            is SupplierDetailsEvent.OnSortChanged -> {
                getPOs(event.sortOption)
            }
        }
    }
}

@Suppress("FunctionName")
fun PreviewSupplierDetailsUiState(
    supplier: C3Vendor,
    tabIndex: Int = 0,
): SupplierDetailsUiState {
    return SupplierDetailsViewModelState(
        settings = FakeC3AppSettingsProvider(),
        supplier = supplier,
        isLoading = false,
        errorMessages = emptyList(),
        searchInput = "",
        tabIndex = tabIndex,
    ).toUiState()
}