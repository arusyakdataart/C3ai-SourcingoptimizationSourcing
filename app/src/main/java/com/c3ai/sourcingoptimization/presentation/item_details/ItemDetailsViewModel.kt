package com.c3ai.sourcingoptimization.presentation.item_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.c3ai.sourcingoptimization.common.*
import com.c3ai.sourcingoptimization.data.C3Result.Error
import com.c3ai.sourcingoptimization.data.C3Result.Success
import com.c3ai.sourcingoptimization.domain.model.*
import com.c3ai.sourcingoptimization.domain.settings.C3AppSettingsProvider
import com.c3ai.sourcingoptimization.domain.settings.FakeC3AppSettingsProvider
import com.c3ai.sourcingoptimization.domain.use_case.ItemDetailsUseCases
import com.c3ai.sourcingoptimization.presentation.ViewModelState
import com.c3ai.sourcingoptimization.presentation.views.*
import com.c3ai.sourcingoptimization.presentation.views.itemdetails.IndexPriceCharts
import com.c3ai.sourcingoptimization.presentation.views.itemdetails.SuppliersCharts
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
    val dateRangeSelected: Int
    val statsTypeSelected: Int
    val selectedSupplierContact: C3VendorContact?

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
        override val statsTypeSelected: Int,
        override val selectedSupplierContact: C3VendorContact? = null,
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
        val suppliers: List<UiVendor>,
        override val isLoading: Boolean,
        override val itemId: String,
        override val tabIndex: Int,
        override val dateRangeSelected: Int,
        override val statsTypeSelected: Int,
        override val selectedSupplierContact: C3VendorContact? = null,
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
    val itemDetailsSuppliers: List<C3Vendor> = emptyList(),
    val marketPriceIndex: List<MarketPriceIndex> = emptyList(),
    val indexPrice: IndexPrice? = null,
    val vendorRelationMetrics: Map<String, List<Double>>? = null,
    val saStartDate: String = formatDate(date = getMonthBackDate(3)),
    val saEndDate: String = formatDate(date = getCurrentDate()),
    val poLineItems: List<PurchaseOrder.Line> = emptyList(),
    val suppliers: List<C3Vendor> = emptyList(),
    val selectedSupplierContact: C3VendorContact? = null,
    val isLoading: Boolean = false,
    val itemId: String = "",
    val tabIndex: Int = 0,
    val dateRangeSelected: Int = 0,
    val statsTypeSelected: Int = 0,
    var selectedChartsCrosshairIndex: Int = -1,
    val chartsHashCode: Int = -1,
    val polineSortOption: String = "",
    val suppliersSortOption: String = "",
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
                    categories = itemDetailsSuppliers.map { it.name },
                    data = formatSuppliersChartData(),
                    maxValue = formatSuppliersChartData().maxOrNull(),
                    dataLabelsFormat = if (statsTypeSelected == 0) "{point.y:,.2f}M" else "{point.y:,.0f} %",
                    suppliers = vendorRelationMetrics?.let { metrics ->
                        val textsMap = mutableMapOf<String, String>()
                        val supplierNames = getSupplierNameAbbr(itemDetailsSuppliers.map { it.name })
                        if (selectedChartsCrosshairIndex != -1) {
                            itemDetailsSuppliers.forEachIndexed { index, c3Vendor ->
                                textsMap[supplierNames[index]] = String.format(
                                    "%s%s", "$",
                                    String.format(
                                        "%.2f", metrics[c3Vendor.id]?.get(selectedChartsCrosshairIndex)
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
                poLineItems = poLineItems.map { convert(it) },
                suppliers = suppliers.map { convert(it) },
                selectedSupplierContact = selectedSupplierContact
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

    private fun getSupplierNameAbbr(names: List<String>): List<String> {
        val abbr = mutableListOf<String>()
        val occurrences = mutableMapOf<String, Int>()

        names.forEach {
            if (it.length <= 3) {
                abbr.add(it)
            } else {
                if (!it.contains(" ")) {
                    val short = it.substring(0, 3).uppercase()
                    if (abbr.contains(short)) {
                        var number = occurrences.get(short) ?: 0
                        occurrences.put(short, ++number)
                        abbr.add(short.substring(0,2) + number)
                    } else {
                        occurrences.put(short, 0)
                        abbr.add(short)
                    }
                } else {
                    var short = it.split(" ").joinToString("") { it[0].toString() }.uppercase()
                    if (short.length > 3) {
                        short = short.substring(0, 3)
                    }
                    var number = occurrences.get(short) ?: -1
                    occurrences.put(short, ++number)
                    if (number == 0) {
                        abbr.add(short)
                    } else {
                        if (short.length == 3) {
                            abbr.add(short.substring(0, 2) + number)
                        } else {
                            abbr.add(short + number)
                        }
                    }
                }
            }
        }
        return abbr
    }

    fun formatSuppliersChartData(): List<Double> {
        if (statsTypeSelected == 0) {
            return itemDetailsSuppliers.map { it.spend.value.formatNumberLocal() }
        }

        val total = itemDetailsSuppliers.sumOf { it.spend.value }
        if (total == 0.0) {
            return emptyList()
        }
        return itemDetailsSuppliers.map { ((it.spend.value / total) * 100) }
    }
}

/**
 * ViewModel that handles the business logic of the ItemDetails screen
 */
@HiltViewModel
class ItemDetailsViewModel @Inject constructor(
    settings: C3AppSettingsProvider,
    private val useCases: ItemDetailsUseCases,
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
        load()
    }

    /**
     * Refresh posts and update the UI state accordingly
     */
    fun refresh() {
        viewModelState.update { it.copy(isLoading = true) }
        load()
    }

    private fun load() {
        viewModelScope.launch {
            when (viewModelState.value.tabIndex) {
                0 -> loadOverview()
                1 -> loadPOLines()
                2 -> loadSuppliers()
            }
        }
    }

    private suspend fun loadOverview() {
        val itemsResult = useCases.getItemDetails(itemId)
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
        val openClosedPOLineQtyResult = useCases.getEvalMetricsForPOLineQty(
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
        val savingsOpportunityResult = useCases.getEvalMetricsForSavingsOpportunity(
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
        val result = useCases.getPOLines(itemId, viewModelState.value.polineSortOption)
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
        val result = useCases.getSuppliers(itemId, viewModelState.value.suppliersSortOption)
        viewModelState.update { state ->
            when (result) {
                is Success -> {
                    state.copy(
                        suppliers = result.data,
                        isLoading = false
                    )
                }
                is Error -> {
                    state.copy(isLoading = false)
                }
            }
        }
    }

    private suspend fun updateSourcingAnalysis(startDate: String, endDate: String) {
        val suppliersResult = useCases.getItemDetailsSuppliers(itemId, limit = 5)
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
                        itemDetailsSuppliers = suppliersResult.data,
                        vendorRelationMetrics = vendorRelationMetrics,
                        chartsHashCode = vendorRelationMetrics.hashCode()
                    )
                }
                is Error -> {
                    state.copy()
                }
            }
        }

        val marketPriceIndex = useCases.getMarketPriceIndex()
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
        var vendorRelationMetrics = mapOf<String, List<Double>>()
        if (supplierIds.isNotEmpty()) {
            val result = useCases.getVendorRelationMetrics(
                itemId,
                supplierIds,
                expressions,
                startDate,
                endDate,
                interval
            )
            when (result) {
                is Success -> {
                    vendorRelationMetrics = result.data
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
        val result = useCases.getMarketPriceIndexRelationMetrics(
            itemId,
            indexId,
            ids,
            expressions,
            startDate,
            endDate,
            interval
        )
        when (result) {
            is Success -> {
                result.data.result[indexId]?.indexPrice
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
                    viewModelState.update { it.copy(isLoading = true) }
                    when (state.tabIndex) {
                        1 -> {
                            viewModelScope.launch {
                                val result = useCases.getPOLines(itemId, event.sortOption)
                                viewModelState.update { state ->
                                    when (result) {
                                        is Success -> state.copy(
                                            polineSortOption = event.sortOption,
                                            poLineItems = result.data,
                                            isLoading = false
                                        )
                                        is Error -> {
                                            state.copy(isLoading = false)
                                        }
                                    }
                                }
                            }
                            state
                        }
                        2 -> {
                            viewModelScope.launch {
                                val result = useCases.getSuppliers(itemId, event.sortOption)
                                viewModelState.update { state ->
                                    when (result) {
                                        is Success -> state.copy(
                                            suppliersSortOption = event.sortOption,
                                            suppliers = result.data,
                                            isLoading = false
                                        )
                                        is Error -> {
                                            state.copy(isLoading = false)
                                        }
                                    }
                                }
                            }
                            state
                        } else -> {
                            state
                        }
                    }
                }
                is ItemDetailsEvent.OnSupplierContactSelected -> {
                    viewModelScope.launch {
                        val result = useCases.getSupplierContacts(event.supplierId)
                        viewModelState.update { state ->
                            when (result) {
                                is Success -> state.copy(
                                    selectedSupplierContact = result.data
                                )
                                is Error -> {
                                    state.copy(isLoading = false)
                                }
                            }
                        }
                    }
                    state
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
