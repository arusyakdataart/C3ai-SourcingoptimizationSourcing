package com.c3ai.sourcingoptimization.presentation.item_details.overview

import android.util.Log
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
import com.c3ai.sourcingoptimization.presentation.views.*
import com.c3ai.sourcingoptimization.utilities.PAGINATED_RESPONSE_LIMIT
import com.c3ai.sourcingoptimization.utilities.VISIBLE_THRESHOLD
import com.c3ai.sourcingoptimization.utilities.extentions.formatNumberLocal
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
    val statsTypeSelected: Int

    /**
     * There are no item to render.
     *
     * This could either be because they are still loading or they failed to load, and we are
     * waiting to reload them.
     */
    data class NoItem(
        override val isLoading: Boolean,
        override val itemId: String,
        override val tabIndex: Int,
        override val statsTypeSelected: Int
    ) : ItemDetailsUiState

    /**
     * There are item to render, as contained in [item].
     *
     */
    data class HasItem(
        val item: UiItem,
        val savingsOpportunity: UiSavingsOpportunityItem? = null,
        val ocPOLineQty: UiOpenClosedPOLineQtyItem,
        val suppliersChart: SuppliersChart?,
        val indexes: List<MarketPriceIndex> = emptyList(),
        val itemMarketPriceIndexRelations: List<ItemRelation> = emptyList(),
        val itemMarketPriceIndexRelationMetrics: ItemMarketPriceIndexRelationMetrics? = null,
        val vendorRelationMetrics: Map<String, List<Double>>? = null,
        override val isLoading: Boolean,
        override val itemId: String,
        override val tabIndex: Int,
        override val statsTypeSelected: Int
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
    val marketPriceIndex: List<MarketPriceIndex> = emptyList(),
    val itemMarketPriceIndexRelations: List<ItemRelation> = emptyList(),
    val itemMarketPriceIndexRelationMetrics: ItemMarketPriceIndexRelationMetrics? = null,
    val po_expressions: List<String> = listOf("OpenPOLineQuantity", "ClosedPOLineQuantity"),
    val vendorRelationMetrics: Map<String, List<Double>>? = null,
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
    val dateRangeSelected: Int = 0,
    val statsTypeSelected: Int = 0,
) : ViewModelState() {

    /**
     * Converts this [ItemDetailsViewModelState] into a more strongly typed [ItemDetailsUiState]
     * for driving the ui.
     */
    fun toUiState(): ItemDetailsUiState {
        return if (item != null) {
            ItemDetailsUiState.HasItem(
                item = convert(item),
                savingsOpportunity = savingsOpportunity?.let { convert(savingsOpportunity, item.id) },
                ocPOLineQty = convert(openClosedPOLineQty, item.id),
                suppliersChart = SuppliersChart(
                    categories = suppliers.map { it.name },
                    data = formatSuppliersChartData(),
                    suppliersChartDataMaxValue = formatSuppliersChartData().maxOrNull(),
                    dataLabelsFormat = if (statsTypeSelected == 0) "{point.y:,.2f}M" else "{point.y:,.0f} %"
                ),
                indexes = marketPriceIndex,
                itemMarketPriceIndexRelations = itemMarketPriceIndexRelations,
                itemMarketPriceIndexRelationMetrics = itemMarketPriceIndexRelationMetrics,
                vendorRelationMetrics = vendorRelationMetrics,
                isLoading = isLoading,
                itemId = itemId,
                tabIndex = tabIndex,
                statsTypeSelected = statsTypeSelected
            )
        } else {
            ItemDetailsUiState.NoItem(
                isLoading = isLoading,
                itemId = itemId,
                tabIndex = tabIndex,
                statsTypeSelected = statsTypeSelected
            )
        }
    }

    fun formatSuppliersChartData(): List<Double> {
        if (statsTypeSelected == 0) {
            return suppliers.map { it.spend.value.formatNumberLocal() }
        }

        val total = suppliers.sumOf { it.spend.value }
        if (total == 0.0) {
            return emptyList()
        }
        return suppliers.map { ((it.spend.value / total) * 100) }
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
            viewModelState.update { state ->
                when (itemsResult) {
                    is Success -> {
                        Log.e("itemsResult", "call")
                        offset += PAGINATED_RESPONSE_LIMIT
                        state.copy(
                            item = itemsResult.data,
                            isLoading = false
                        )
                    }
                    is Error -> {
                        state.copy(isLoading = false)
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
            viewModelState.update { state ->
                when (openClosedPOLineQtyResult) {
                    is Success -> {
                        Log.e("openClosedPOLineQty", "call")
                        state.copy(openClosedPOLineQty = openClosedPOLineQtyResult.data)
                    }
                    is Error -> {
                        state.copy()
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
            viewModelState.update { state ->
                when (savingsOpportunityResult) {
                    is Success -> {
                        Log.e("savingsOpportunity", "call")
                        state.copy(savingsOpportunity = savingsOpportunityResult.data)
                    }
                    is Error -> {
                        state.copy()
                    }
                }
            }

            val suppliersResult = repository.getItemDetailsSuppliers(itemId)
            viewModelState.update { state ->
                when (suppliersResult) {
                    is Success -> {
                        val vendorRelationMetrics = getVendorRelationMetrics(
                            itemId,
                            supplierIds = suppliersResult.data.map { it.id },
                            expressions = listOf("OrderLineValue"),
                            startDate = formatDate(date = getYearBackDate(1)),
                            endDate = formatDate(date = getCurrentDate()),
                            interval = "MONTH"
                        )
                        state.copy(
                            suppliers = suppliersResult.data,
                            vendorRelationMetrics = vendorRelationMetrics
                        )
                    }
                    is Error -> {
                        state.copy()
                    }
                }
            }

            val marketPriceIndex = repository.getMarketPriceIndex()
            viewModelState.update { state ->
                when (marketPriceIndex) {
                    is Success -> {
                        state.copy(marketPriceIndex = marketPriceIndex.data)
                    }
                    is Error -> {
                        state.copy()
                    }
                }
            }
        }
    }

    private suspend fun getVendorRelationMetrics(
        itemId: String,
        supplierIds: List<String>,
        expressions: List<String>,
        startDate: String,
        endDate: String,
        interval: String
    ): Map<String, List<Double>> {
        val vendorRelationMetrics = mutableMapOf<String, List<Double>>()
        if (supplierIds.isNotEmpty()) {
            val itemVendorRelations =
                repository.getItemVendorRelation(itemId, supplierIds = supplierIds)
            when (itemVendorRelations) {
                is Success -> {
                    val itemVendorRelationMetrics = repository.getItemVendorRelationMetrics(
                        itemVendorRelations.data.map { it.id },
                        expressions,
                        startDate,
                        endDate,
                        interval
                    )
                    when (itemVendorRelationMetrics) {
                        is Success -> {
                            itemVendorRelations.data.forEach { relation ->
                                vendorRelationMetrics[relation.to.id] =
                                    itemVendorRelationMetrics.data.result[relation.id]
                                        ?.OrderLineValue?.data ?: listOf()
                            }
                        }
                        is Error -> {}
                    }
                }
                is Error -> {}
            }
        }
        return vendorRelationMetrics
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
                is ItemDetailsEvent.OnDateRangeSelected -> {
                    state.copy(dateRangeSelected = event.selected)
                }
                is ItemDetailsEvent.OnStatsTypeSelected -> {
                    state.copy(statsTypeSelected = event.selected)
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
