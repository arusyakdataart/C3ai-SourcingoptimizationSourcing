package com.c3ai.sourcingoptimization.presentation.item_details.overview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.c3ai.sourcingoptimization.common.formatDate
import com.c3ai.sourcingoptimization.common.getCurrentDate
import com.c3ai.sourcingoptimization.common.getMonthBackDate
import com.c3ai.sourcingoptimization.common.getYearBackDate
import com.c3ai.sourcingoptimization.data.C3Result.Error
import com.c3ai.sourcingoptimization.data.C3Result.Success
import com.c3ai.sourcingoptimization.data.repository.C3Repository
import com.c3ai.sourcingoptimization.domain.model.*
import com.c3ai.sourcingoptimization.domain.settings.C3AppSettingsProvider
import com.c3ai.sourcingoptimization.domain.settings.FakeC3AppSettingsProvider
import com.c3ai.sourcingoptimization.presentation.ViewModelState
import com.c3ai.sourcingoptimization.presentation.item_details.ItemDetailsEvent
import com.c3ai.sourcingoptimization.presentation.views.UiItem
import com.c3ai.sourcingoptimization.presentation.views.convert
import com.c3ai.sourcingoptimization.utilities.PAGINATED_RESPONSE_LIMIT
import com.c3ai.sourcingoptimization.utilities.VISIBLE_THRESHOLD
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UI state for the ItemDetails route.
 *
 * This is derived from [ItemDetailsViewModelState], but split into two possible subclasses to more
 * precisely represent the state available to render the UI.
 */
sealed interface ItemDetailsUiState {

    val isLoading: Boolean
    val itemId: String
    val tabIndex: Int

    /**
     * There are no item to render.
     *
     * This could either be because they are still loading or they failed to load, and we are
     * waiting to reload them.
     */
    data class NoItem(
        override val isLoading: Boolean,
        override val itemId: String,
        override val tabIndex: Int
    ) : ItemDetailsUiState

    /**
     * There are item to render, as contained in [item].
     *
     */
    data class HasItem(
        val item: UiItem,
        val poLineQty: OpenClosedPOLineQtyItem? = null,
        val savingsOpportunity: SavingsOpportunityItem? = null,
        val suppliers: List<C3Vendor> = emptyList(),
        val vendorRelations: List<ItemRelation> = emptyList(),
        val itemVendorRelationMetrics: ItemVendorRelationMetrics? = null,
        val indexes: List<MarketPriceIndex> = emptyList(),
        val itemMarketPriceIndexRelations: List<ItemRelation> = emptyList(),
        val itemMarketPriceIndexRelationMetrics: ItemMarketPriceIndexRelationMetrics? = null,
        override val isLoading: Boolean,
        override val itemId: String,
        override val tabIndex: Int
    ) : ItemDetailsUiState
}

/**
 * An internal representation of the Home route state, in a raw form
 */
private data class ItemDetailsViewModelState(
    override val settings: C3AppSettingsProvider,
    val item: C3Item? = null,
    val openClosedPOLineQty: OpenClosedPOLineQtyItem? = null,
    val savingsOpportunity: SavingsOpportunityItem? = null,
    val suppliers: List<C3Vendor> = emptyList(),
    val itemVendorRelations: List<ItemRelation> = emptyList(),
    val itemVendorRelationMetrics: ItemVendorRelationMetrics? = null,
    val marketPriceIndex: List<MarketPriceIndex> = emptyList(),
    val itemMarketPriceIndexRelations: List<ItemRelation> = emptyList(),
    val itemMarketPriceIndexRelationMetrics: ItemMarketPriceIndexRelationMetrics? = null,
    val po_expressions: List<String> = listOf("OpenPOLineQuantity", "ClosedPOLineQuantity"),
    val po_startDate: String = formatDate(date = getYearBackDate(1)),
    val po_endDate: String = formatDate(date = getCurrentDate()),
    val po_interval: String = "YEAR",
    val so_expressions: List<String> = listOf("SavingsOpportunityCompound"),
    val so_startDate: String = formatDate(date = getMonthBackDate(1)),
    val so_endDate: String = formatDate(date = getCurrentDate()),
    val so_interval: String = "MONTH",
    val isLoading: Boolean = false,
    val itemId: String = "",
    val tabIndex: Int = 0,
) : ViewModelState() {

    /**
     * Converts this [ItemDetailsViewModelState] into a more strongly typed [ItemDetailsUiState]
     * for driving the ui.
     */
    fun toUiState(): ItemDetailsUiState {
        return if (item != null) {
            ItemDetailsUiState.HasItem(
                item = convert(item),
                poLineQty = openClosedPOLineQty,
                savingsOpportunity = savingsOpportunity,
                suppliers = suppliers,
                vendorRelations = itemVendorRelations,
                itemVendorRelationMetrics = itemVendorRelationMetrics,
                indexes = marketPriceIndex,
                itemMarketPriceIndexRelations = itemMarketPriceIndexRelations,
                itemMarketPriceIndexRelationMetrics = itemMarketPriceIndexRelationMetrics,
                isLoading = isLoading,
                itemId = itemId,
                tabIndex = tabIndex
            )
        } else {
            ItemDetailsUiState.NoItem(
                isLoading = isLoading,
                itemId = itemId,
                tabIndex = tabIndex
            )
        }
    }
}

/**
 * ViewModel that handles the business logic of the ItemDetails screen
 */
@HiltViewModel
class ItemDetailsViewModel @Inject constructor(
    settings: C3AppSettingsProvider,
    private val repository: C3Repository,
) : ViewModel() {

    private val viewModelState = MutableStateFlow(ItemDetailsViewModelState(settings))
    private var offset = 0
    private var itemId: String = ""

    // UI state exposed to the UI
    val uiState = viewModelState
        .map { it.toUiState() }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value.toUiState()
        )

    fun loadData(itemId: String) {
        this.itemId = itemId
        refresh()
    }

    /**
     * Refresh posts and update the UI state accordingly
     */
    fun refresh() {
        viewModelState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            val itemsResult = repository.getItemDetails(itemId)
            viewModelState.update {
                when (itemsResult) {
                    is Success -> {
                        offset += PAGINATED_RESPONSE_LIMIT
                        it.copy(
                            item = itemsResult.data,
                            isLoading = false
                        )
                    }
                    is Error -> {
                        it.copy(isLoading = false)
                    }
                }
            }

            val openClosedPOLineQtyResult = repository.getEvalMetricsForPOLineQty(
                itemId,
                viewModelState.value.po_expressions,
                viewModelState.value.po_startDate,
                viewModelState.value.po_endDate,
                viewModelState.value.po_interval
            )
            viewModelState.update {
                when (openClosedPOLineQtyResult) {
                    is Success -> {
                        it.copy(openClosedPOLineQty = openClosedPOLineQtyResult.data)
                    }
                    is Error -> {
                        it.copy()
                    }
                }
            }

            val savingsOpportunityResult = repository.getEvalMetricsForSavingsOpportunity(
                itemId,
                viewModelState.value.so_expressions,
                viewModelState.value.so_startDate,
                viewModelState.value.so_endDate,
                viewModelState.value.so_interval
            )
            viewModelState.update {
                when (savingsOpportunityResult) {
                    is Success -> {
                        it.copy(savingsOpportunity = savingsOpportunityResult.data)
                    }
                    is Error -> {
                        it.copy()
                    }
                }
            }

            val suppliersResult = repository.getItemDetailsSuppliers(itemId)
            viewModelState.update {
                when (suppliersResult) {
                    is Success -> {
                        it.copy(suppliers = suppliersResult.data)
                    }
                    is Error -> {
                        it.copy()
                    }
                }
            }

            val marketPriceIndex = repository.getMarketPriceIndex()
            viewModelState.update {
                when (marketPriceIndex) {
                    is Success -> {
                        it.copy(marketPriceIndex = marketPriceIndex.data)
                    }
                    is Error -> {
                        it.copy()
                    }
                }
            }
        }
    }

    fun getItemVendorRelation(itemId: String, supplierIds: List<String>) {
        if (supplierIds.isNotEmpty()) {
            viewModelScope.launch {
                val itemVendorRelations =
                    repository.getItemVendorRelation(itemId, supplierIds = supplierIds)
                viewModelState.update {
                    when (itemVendorRelations) {
                        is Success -> {
                            it.copy(itemVendorRelations = itemVendorRelations.data)
                        }
                        is Error -> {
                            it.copy()
                        }
                    }
                }
            }
        }
    }

    fun getItemVendorRelationMetrics(
        ids: List<String>,
        expressions: List<String>,
        startDate: String,
        endDate: String,
        interval: String
    ) {
        viewModelScope.launch {
            val itemVendorRelationMetrics = repository.getItemVendorRelationMetrics(
                ids,
                expressions,
                startDate,
                endDate,
                interval
            )
            viewModelState.update {
                when (itemVendorRelationMetrics) {
                    is Success -> {
                        it.copy(itemVendorRelationMetrics = itemVendorRelationMetrics.data)
                    }
                    is Error -> {
                        it.copy()
                    }
                }
            }
        }
    }

    fun getItemMarketPriceIndexRelation(itemId: String, indexId: String) {
        viewModelScope.launch {
            val itemMarketPriceIndexRelations = repository.getItemMarketPriceIndexRelation(
                itemId, indexId
            )
            viewModelState.update {
                when (itemMarketPriceIndexRelations) {
                    is Success -> {
                        it.copy(itemMarketPriceIndexRelations = itemMarketPriceIndexRelations.data)
                    }
                    is Error -> {
                        it.copy()
                    }
                }
            }
        }
    }

    fun getItemMarketPriceIndexRelationMetrics(
        ids: List<String>,
        expressions: List<String>,
        startDate: String,
        endDate: String,
        interval: String
    ) {
        viewModelScope.launch {
            val itemMarketPriceIndexRelationMetrics =
                repository.getItemMarketPriceIndexRelationMetrics(
                    ids,
                    expressions,
                    startDate,
                    endDate,
                    interval
                )
            viewModelState.update {
                when (itemMarketPriceIndexRelationMetrics) {
                    is Success -> {
                        it.copy(
                            itemMarketPriceIndexRelationMetrics = itemMarketPriceIndexRelationMetrics.data
                        )
                    }
                    is Error -> {
                        it.copy()
                    }
                }
            }
        }
    }

    /**
     * Update state by user event.
     */
    fun onEvent(event: ItemDetailsEvent) {
        viewModelState.update { state ->
            when (event) {
                is ItemDetailsEvent.OnTabItemClick -> {
                    state.copy(tabIndex = event.tabIndex)
                }
            }
        }
    }
}

fun UiAction.Scroll.shouldFetchMore(offset: Int): Boolean {
    return offset == totalItemCount * PAGINATED_RESPONSE_LIMIT
            && visibleItemCount + lastVisibleItemPosition + VISIBLE_THRESHOLD >= totalItemCount
}

sealed class UiAction {
    data class Scroll(
        val visibleItemCount: Int,
        val lastVisibleItemPosition: Int,
        val totalItemCount: Int
    ) : UiAction()
}

@Suppress("FunctionName")
fun PreviewItemDetailsUiState(
    item: C3Item,
    tabIndex: Int = 0,
): ItemDetailsUiState {
    return ItemDetailsViewModelState(
        settings = FakeC3AppSettingsProvider(),
        item = item,
        isLoading = false,
        tabIndex = tabIndex,
    ).toUiState()
}
