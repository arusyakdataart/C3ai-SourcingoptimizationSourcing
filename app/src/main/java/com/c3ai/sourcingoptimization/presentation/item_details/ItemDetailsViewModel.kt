package com.c3ai.sourcingoptimization.presentation.item_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.c3ai.sourcingoptimization.R
import com.c3ai.sourcingoptimization.common.*
import com.c3ai.sourcingoptimization.data.C3Result
import com.c3ai.sourcingoptimization.data.C3Result.Error
import com.c3ai.sourcingoptimization.data.C3Result.Success
import com.c3ai.sourcingoptimization.data.repository.C3Repository
import com.c3ai.sourcingoptimization.domain.model.*
import com.c3ai.sourcingoptimization.domain.settings.C3AppSettingsProvider
import com.c3ai.sourcingoptimization.domain.settings.FakeC3AppSettingsProvider
import com.c3ai.sourcingoptimization.domain.use_case.SuppliersDetailsUseCases
import com.c3ai.sourcingoptimization.presentation.ViewModelState
import com.c3ai.sourcingoptimization.presentation.views.*
import com.c3ai.sourcingoptimization.presentation.views.itemdetails.IndexPriceCharts
import com.c3ai.sourcingoptimization.presentation.views.itemdetails.SuppliersCharts
import com.c3ai.sourcingoptimization.utilities.ErrorMessage
import com.c3ai.sourcingoptimization.utilities.PAGINATED_RESPONSE_LIMIT
import com.c3ai.sourcingoptimization.utilities.VISIBLE_THRESHOLD
import com.c3ai.sourcingoptimization.utilities.extentions.formatNumberLocal
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
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
    val dateRangeSelected: Int
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
        override val dateRangeSelected: Int,
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
        val suppliersChart: SuppliersCharts?,
        val indexPriceChart: IndexPriceCharts?,
        val vendorRelationMetrics: Map<String, List<Double>>? = null,
        val chartsHashCode: Int,
        val poLineItems: List<UiPurchaseOrder.Line>,
        override val isLoading: Boolean,
        override val itemId: String,
        override val tabIndex: Int,
        override val dateRangeSelected: Int,
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
    val indexPrice: IndexPrice? = null,
    val vendorRelationMetrics: Map<String, List<Double>>? = null,
    val saStartDate: String = formatDate(date = getMonthBackDate(3)),
    val saEndDate: String = formatDate(date = getCurrentDate()),
    val poLineItems: List<PurchaseOrder.Line> = emptyList(),
    val isLoading: Boolean = false,
    val itemId: String = "",
    val tabIndex: Int = 0,
    val dateRangeSelected: Int = 0,
    val statsTypeSelected: Int = 0,
    var selectedChartsCrosshairIndex: Int = -1,
    val chartsHashCode: Int = -1,
) : ViewModelState() {

    /**
     * Converts this [ItemDetailsViewModelState] into a more strongly typed [ItemDetailsUiState]
     * for driving the ui.
     */
    fun toUiState(): ItemDetailsUiState {
        return if (item != null) {
            ItemDetailsUiState.HasItem(
                item = convert(item),
                savingsOpportunity = savingsOpportunity?.let {
                    convert(
                        savingsOpportunity,
                        item.id
                    )
                },
                ocPOLineQty = convert(openClosedPOLineQty, item.id),
                suppliersChart = SuppliersCharts(
                    categories = suppliers.map { it.name },
                    data = formatSuppliersChartData(),
                    maxValue = formatSuppliersChartData().maxOrNull(),
                    dataLabelsFormat = if (statsTypeSelected == 0) "{point.y:,.2f}M" else "{point.y:,.0f} %",
                    suppliers = vendorRelationMetrics?.let { metrics ->
                        val textsMap = mutableMapOf<String, String>()
                        if (selectedChartsCrosshairIndex != -1) {
                            suppliers.forEach {
                                textsMap[it.name] = String.format(
                                    "%s%s", "$",
                                    String.format(
                                        "%.2f", metrics[it.id]?.get(selectedChartsCrosshairIndex)
                                    )
                                )
                            }
                        }
                        textsMap
                    }
                ),
                indexPriceChart = IndexPriceCharts(
                    categories = indexPrice?.dates?.map { getMonth(it) } ?: emptyList(),
                    data = indexPrice?.data ?: emptyList(),
                    maxValue = indexPrice?.data?.maxOrNull() ?: 100.0,
                    graphYearFormat = indexPrice?.let {
                        val firstYear = getYear(it.dates[0])
                        val lastYear = getYear(it.dates[it.dates.size.minus(1) ?: 0])
                        if (firstYear == lastYear) firstYear.toString()
                        else String.format(
                            "%s%s%s",
                            firstYear.toString(), " - ", lastYear.toString()
                        )
                    } ?: "",
                    dateText = indexPrice?.dates?.let { dates ->
                        if (selectedChartsCrosshairIndex != -1) {
                            val date = dates[selectedChartsCrosshairIndex]
                            getMonth(date) + " " + getYear(date)
                        } else {
                            ""
                        }
                    } ?: "",
                    nameText = item.name ?: "",
                    priceText = indexPrice?.data?.let {
                        if (selectedChartsCrosshairIndex != -1) {
                            String.format(
                                "%s%s", "$",
                                String.format("%.2f", it[selectedChartsCrosshairIndex])
                            )
                        } else {
                            ""
                        }
                    } ?: "",
                ),
                vendorRelationMetrics = vendorRelationMetrics,
                isLoading = isLoading,
                itemId = itemId,
                tabIndex = tabIndex,
                dateRangeSelected = dateRangeSelected,
                statsTypeSelected = statsTypeSelected,
                chartsHashCode = chartsHashCode,
                poLineItems = poLineItems.map { convert(it) }
            )
        } else {
            ItemDetailsUiState.NoItem(
                isLoading = isLoading,
                itemId = itemId,
                tabIndex = tabIndex,
                dateRangeSelected = dateRangeSelected,
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
    private val useCases: SuppliersDetailsUseCases
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
            when (viewModelState.value.tabIndex) {
                0 -> loadOverview()
                1 -> loadPOLines()
                2 -> loadSuppliers()
            }
        }
    }

    private suspend fun loadOverview() {
        val itemsResult = repository.getItemDetails(itemId)
        viewModelState.update { state ->
            when (itemsResult) {
                is Success -> {
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

        var expressions: List<String> = listOf("OpenPOLineQuantity", "ClosedPOLineQuantity")
        var startDate: String = formatDate(date = getYearBackDate(1))
        var endDate: String = formatDate(date = getCurrentDate())
        var interval = "YEAR"
        val openClosedPOLineQtyResult = repository.getEvalMetricsForPOLineQty(
            itemId,
            expressions,
            startDate,
            endDate,
            interval
        )
        viewModelState.update { state ->
            when (openClosedPOLineQtyResult) {
                is Success -> {
                    state.copy(openClosedPOLineQty = openClosedPOLineQtyResult.data)
                }
                is Error -> {
                    state.copy()
                }
            }
        }

        expressions = listOf("SavingsOpportunityCompound")
        startDate = formatDate(date = getMonthBackDate(3))
        endDate = formatDate(date = getCurrentDate())
        interval = "MONTH"
        val savingsOpportunityResult = repository.getEvalMetricsForSavingsOpportunity(
            itemId,
            expressions,
            startDate,
            endDate,
            interval
        )
        viewModelState.update { state ->
            when (savingsOpportunityResult) {
                is Success -> {
                    state.copy(savingsOpportunity = savingsOpportunityResult.data)
                }
                is Error -> {
                    state.copy()
                }
            }
        }

        updateSourcingAnalysis(
            viewModelState.value.saStartDate,
            viewModelState.value.saEndDate
        )
    }

    private suspend fun loadPOLines() {
        val result = repository.getPOLines(
            itemId = itemId,
            orderId = null,
            order = ""
        )
        viewModelState.update {
            when (result) {
                is Success -> {
                    it.copy(
                        poLineItems = result.data,
                        isLoading = false
                    )
                }
                is Error -> {
                    it.copy(isLoading = false)
                }
            }
        }
    }

    private suspend fun loadSuppliers() {

    }

    private suspend fun updateSourcingAnalysis(startDate: String, endDate: String) {
        val suppliersResult = repository.getItemDetailsSuppliers(itemId)
        viewModelState.update { state ->
            when (suppliersResult) {
                is Success -> {
                    val vendorRelationMetrics = getVendorRelationMetrics(
                        itemId,
                        supplierIds = suppliersResult.data.map { it.id },
                        expressions = listOf("OrderLineValue"),
                        startDate = startDate,
                        endDate = endDate,
                        interval = "MONTH"
                    )
                    state.copy(
                        suppliers = suppliersResult.data,
                        vendorRelationMetrics = vendorRelationMetrics,
                        chartsHashCode = vendorRelationMetrics.hashCode()
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
                    val indexId = marketPriceIndex.data[0].id
                    val indexPrice = getMarketPriceIndexRelationMetrics(
                        itemId,
                        indexId,
                        listOf(indexId),
                        expressions = listOf("IndexPrice"),
                        startDate = startDate,
                        endDate = endDate,
                        interval = "MONTH"
                    )
                    state.copy(
                        marketPriceIndex = marketPriceIndex.data,
                        indexPrice = indexPrice,
                        chartsHashCode = indexPrice.hashCode()
                    )
                }
                is Error -> {
                    state.copy()
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

    private suspend fun getMarketPriceIndexRelationMetrics(
        itemId: String,
        indexId: String,
        ids: List<String>,
        expressions: List<String>,
        startDate: String,
        endDate: String,
        interval: String
    ): IndexPrice? {
        when (repository.getItemMarketPriceIndexRelation(itemId, indexId)) {
            is Success -> {
                val marketPriceIndexRelationMetrics =
                    repository.getItemMarketPriceIndexRelationMetrics(
                        ids,
                        expressions,
                        startDate,
                        endDate,
                        interval
                    )
                when (marketPriceIndexRelationMetrics) {
                    is Success -> {
                        return marketPriceIndexRelationMetrics.data.result[indexId]?.indexPrice
                    }
                    is Error -> {}
                }
            }
            is Error -> {}
        }
        return null
    }

    /**
     * Update state by user event.
     */
    fun onEvent(event: ItemDetailsEvent) {
        viewModelState.update { state ->
            when (event) {
                is ItemDetailsEvent.OnTabItemClick -> {
                    viewModelScope.launch {
                        when (event.tabIndex) {
                            0 -> loadOverview()
                            1 -> loadPOLines()
                            2 -> loadSuppliers()
                        }
                    }
                    state.copy(tabIndex = event.tabIndex)
                }
                is ItemDetailsEvent.OnDateRangeSelected -> {
                    var saStartDate: String = formatDate(date = getMonthBackDate(3))
                    val saEndDate: String = formatDate(date = getCurrentDate())
                    when (event.selected) {
                        1 -> {
                            saStartDate = formatDate(date = getMonthBackDate(6))
                        }
                        2 -> {
                            saStartDate = formatDate(date = getYearBackDate(1))
                        }
                        3 -> {
                            saStartDate = formatDate(date = getYearBackDate(6))
                        }
                    }
                    viewModelScope.launch {
                        updateSourcingAnalysis(saStartDate, saEndDate)
                    }
                    state.copy(
                        saStartDate = saStartDate,
                        saEndDate = saEndDate,
                        dateRangeSelected = event.selected
                    )
                }
                is ItemDetailsEvent.OnStatsTypeSelected -> {
                    state.copy(
                        statsTypeSelected = event.selected,
                        chartsHashCode = event.selected.hashCode()
                    )
                }
                is ItemDetailsEvent.UpdateSourcingAnalysis -> {
                    if (state.selectedChartsCrosshairIndex != event.index) {
                        state.copy(selectedChartsCrosshairIndex = event.index)
                    } else {
                        state
                    }
                }
                is ItemDetailsEvent.OnSortChanged -> {
                    when (state.tabIndex) {
                        1 -> {
                            viewModelState.update { it.copy(isLoading = true) }
                            viewModelScope.launch {
                                val result = useCases.getPOsForSupplier("supplier0", order)
                                viewModelState.update {
                                    when (result) {
                                        is C3Result.Success -> it.copy(
                                            poLines = result.data,
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
                            state
                        }
                        2 -> {

                            state
                        }
                        else -> {
                            state
                        }
                    }
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
