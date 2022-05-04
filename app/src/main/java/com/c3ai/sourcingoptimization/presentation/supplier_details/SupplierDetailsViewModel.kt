package com.c3ai.sourcingoptimization.presentation.supplier_details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.c3ai.sourcingoptimization.R
import com.c3ai.sourcingoptimization.data.C3Result
import com.c3ai.sourcingoptimization.domain.model.C3Item
import com.c3ai.sourcingoptimization.domain.model.C3Vendor
import com.c3ai.sourcingoptimization.domain.model.PurchaseOrder
import com.c3ai.sourcingoptimization.domain.settings.C3AppSettingsProvider
import com.c3ai.sourcingoptimization.domain.settings.FakeC3AppSettingsProvider
import com.c3ai.sourcingoptimization.domain.use_case.SuppliersDetailsUseCases
import com.c3ai.sourcingoptimization.presentation.ViewModelState
import com.c3ai.sourcingoptimization.presentation.ViewModelWithPagination
import com.c3ai.sourcingoptimization.presentation.views.UiItem
import com.c3ai.sourcingoptimization.presentation.views.UiPurchaseOrder
import com.c3ai.sourcingoptimization.presentation.views.UiVendor
import com.c3ai.sourcingoptimization.presentation.views.convert
import com.c3ai.sourcingoptimization.utilities.ErrorMessage
import com.c3ai.sourcingoptimization.utilities.PAGINATED_RESPONSE_LIMIT
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

/**
 * UI state for the Supplier Details route.
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
        val items: List<UiItem>,
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
    var poLines: List<PurchaseOrder.Order>? = null,
    var items: List<C3Item>? = null,
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
        if (supplier != null && poLines != null && items != null) {
            SupplierDetailsUiState.HasDetails(
                supplier = convert(supplier),
                poLines = poLines!!.map { convert(it) },
                items = items!!.map { convert(it) },
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
 * ViewModel that handles the business logic of the SuppliersDetails screen
 */
@HiltViewModel
class SuppliersDetailsViewModel @Inject constructor(
    settings: C3AppSettingsProvider,
    private val savedStateHandle: SavedStateHandle,
    private val useCases: SuppliersDetailsUseCases
) : ViewModelWithPagination() {

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
        refreshDetails(page = 0)
    }

    /**
     * Refresh supplier details and update the UI state accordingly
     */
    override fun refreshDetails(sortOrder: String, page: Int) {
        if (page == 0) {
            viewModelState.update { it.copy(isLoading = true) }
        }

        val supplierId = savedStateHandle.get<String>("supplierId") ?: "supplier0"

        viewModelScope.launch {
            val itemsResult = useCases.getSupplierDetails(supplierId)
            viewModelState.update {
                when (itemsResult) {
                    is C3Result.Success -> it.copy(
                        supplier = itemsResult.data,
                        isLoading = viewModelState.value.items == null || viewModelState.value.poLines == null
                    )
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
        getPOs(sortOrder, page)
        getSuppliedItems(sortOrder, page)
    }

    override fun refreshDetails(sortOrder: String, page: Int, index: Int) {
        if (page == 0) {
            viewModelState.update { it.copy(isLoading = true) }
        }
        if (index == 0) {
            getPOs(sortOrder, page)
        } else {
            getSuppliedItems(sortOrder, page)
        }
    }

    override fun setSize() {
        size = 2
    }

    private fun getPOs(order: String = "", page: Int) {
        if (page == 0) {
            viewModelState.update { it.copy(isLoading = true) }
        }

        val supplierId = savedStateHandle.get<String>("supplierId") ?: "supplier0"

        viewModelScope.launch {
            val result =
                useCases.getPOsForSupplier(supplierId, order, page * PAGINATED_RESPONSE_LIMIT)
            viewModelState.update {
                when (result) {
                    is C3Result.Success -> it.copy(
                        poLines = appendPOLines(result.data),
                        isLoading = viewModelState.value.items == null || viewModelState.value.supplier == null
                    )
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

    private fun appendPOLines(poLines: List<PurchaseOrder.Order>): MutableList<PurchaseOrder.Order> {
        if (viewModelState.value.poLines == null) {
            viewModelState.value.poLines = listOf()
        }
        val appendedList = viewModelState.value.poLines!!.toMutableList()
        appendedList.addAll(poLines)
        return appendedList
    }

    private fun getSuppliedItems(order: String = "", page: Int) {
        if (page == 0) {
            viewModelState.update { it.copy(isLoading = true) }
        }

        val supplierId = savedStateHandle.get<String>("supplierId") ?: "supplier0"

        viewModelScope.launch {
            val result =
                useCases.getSuppliedItems(supplierId, order, page * PAGINATED_RESPONSE_LIMIT)
            viewModelState.update {
                when (result) {
                    is C3Result.Success -> it.copy(
                        items = appendSuppliedItems(result.data),
                        isLoading = viewModelState.value.supplier == null || viewModelState.value.poLines == null
                    )
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

    private fun appendSuppliedItems(items: List<C3Item>): MutableList<C3Item> {
        if (viewModelState.value.items == null) {
            viewModelState.value.items = listOf()
        }
        val appendedList = viewModelState.value.items!!.toMutableList()
        appendedList.addAll(items)
        return appendedList
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
                is SupplierDetailsEvent.OnRetry -> {
                    refreshDetails("", pages[viewModelState.value.tabIndex].value)
                    state.copy(isLoading = true)
                }
                is SupplierDetailsEvent.OnError -> {
                    state.copy(errorMessages = emptyList())
                }
                is SupplierDetailsEvent.OnSortChanged -> {
                    if (uiState.value.tabIndex == 0) {
                        getPOs(event.sortOption, page = 0)
                    } else {
                        getSuppliedItems(event.sortOption, page = 0)
                    }
                    state.copy()
                }
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