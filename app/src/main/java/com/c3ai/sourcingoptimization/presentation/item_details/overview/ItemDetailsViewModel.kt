package com.c3ai.sourcingoptimization.presentation.item_details.overview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.c3ai.sourcingoptimization.data.C3Result.Error
import com.c3ai.sourcingoptimization.data.C3Result.Success
import com.c3ai.sourcingoptimization.data.repository.C3Repository
import com.c3ai.sourcingoptimization.domain.model.*
import com.c3ai.sourcingoptimization.utilities.PAGINATED_RESPONSE_LIMIT
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * UI state for the ItemDetails route.
 *
 * This is derived from [ItemDetailsViewModelState], but split into two possible subclasses to more
 * precisely represent the state available to render the UI.
 */
sealed interface ItemDetailsUiState {

    val isLoading: Boolean
    val itemId: String

    /**
     * There are no item to render.
     *
     * This could either be because they are still loading or they failed to load, and we are
     * waiting to reload them.
     */
    data class NoItem(
        override val isLoading: Boolean,
        override val itemId: String
    ) : ItemDetailsUiState

    /**
     * There are item to render, as contained in [item].
     *
     */
    data class HasItem(
        val item: C3Item,
        override val isLoading: Boolean,
        override val itemId: String
    ) : ItemDetailsUiState

    data class HasPOLinesQtyMetrics(
        val poLineQty: OpenClosedPOLineQtyItem,
        override val isLoading: Boolean,
        override val itemId: String
    ) : ItemDetailsUiState

    data class HasSavingsOpportunity(
        val savingsOpportunity: SavingsOpportunityItem,
        override val isLoading: Boolean,
        override val itemId: String
    ) : ItemDetailsUiState

    data class HasSuppliers(
        val suppliers: List<C3Vendor>,
        override val isLoading: Boolean,
        override val itemId: String
    ) : ItemDetailsUiState

    data class HasItemVendorRelation(
        val relations: List<ItemRelation>,
        override val isLoading: Boolean,
        override val itemId: String
    ) : ItemDetailsUiState

    data class HasItemVendorRelationMetrics(
        val relationMetrics: ItemVendorRelationMetrics,
        override val isLoading: Boolean,
        override val itemId: String
    ) : ItemDetailsUiState

    data class HasMarketPriceIndex(
        val indexes: List<MarketPriceIndex>,
        override val isLoading: Boolean,
        override val itemId: String
    ) : ItemDetailsUiState

    data class HasItemMarketPriceIndexRelation(
        val relations: List<ItemRelation>,
        override val isLoading: Boolean,
        override val itemId: String
    ) : ItemDetailsUiState

    data class HasItemMarketPriceIndexRelationMetrics(
        val relationMetrics: ItemMarketPriceIndexRelationMetrics,
        override val isLoading: Boolean,
        override val itemId: String
    ) : ItemDetailsUiState
}

/**
 * An internal representation of the Home route state, in a raw form
 */
private data class ItemDetailsViewModelState(
    val item: C3Item? = null,
    val openClosedPOLineQty: OpenClosedPOLineQtyItem? = null,
    val savingsOpportunity: SavingsOpportunityItem? = null,
    val suppliers: List<C3Vendor>? = null,
    val itemVendorRelations: List<ItemRelation>? = null,
    val itemVendorRelationMetrics: ItemVendorRelationMetrics? = null,
    val marketPriceIndex: List<MarketPriceIndex>? = null,
    val itemMarketPriceIndexRelations: List<ItemRelation>? = null,
    val itemMarketPriceIndexRelationMetrics: ItemMarketPriceIndexRelationMetrics? = null,
    val isLoading: Boolean = false,
    val itemId: String = "",
) {

    /**
     * Converts this [ItemDetailsViewModelState] into a more strongly typed [ItemDetailsUiState]
     * for driving the ui.
     */
    fun toUiState(): ItemDetailsUiState {
        if (openClosedPOLineQty != null) {
            return ItemDetailsUiState.HasPOLinesQtyMetrics(
                poLineQty = openClosedPOLineQty,
                isLoading = isLoading,
                itemId = itemId
            )
        }

        if (item != null) {
            return ItemDetailsUiState.HasItem(
                item = item,
                isLoading = isLoading,
                itemId = itemId
            )
        }

        if (savingsOpportunity != null) {
            return ItemDetailsUiState.HasSavingsOpportunity(
                savingsOpportunity = savingsOpportunity,
                isLoading = isLoading,
                itemId = itemId
            )
        }

        if (suppliers != null) {
            return ItemDetailsUiState.HasSuppliers(
                suppliers = suppliers,
                isLoading = isLoading,
                itemId = itemId
            )
        }

        if (itemVendorRelations != null) {
            return ItemDetailsUiState.HasItemVendorRelation(
                relations = itemVendorRelations,
                isLoading = isLoading,
                itemId = itemId
            )
        }

        if (itemVendorRelationMetrics != null) {
            return ItemDetailsUiState.HasItemVendorRelationMetrics(
                relationMetrics = itemVendorRelationMetrics,
                isLoading = isLoading,
                itemId = itemId
            )
        }

        if (marketPriceIndex != null) {
            return ItemDetailsUiState.HasMarketPriceIndex(
                indexes = marketPriceIndex,
                isLoading = isLoading,
                itemId = itemId
            )
        }

        if (itemMarketPriceIndexRelations != null) {
            return ItemDetailsUiState.HasItemMarketPriceIndexRelation(
                relations = itemMarketPriceIndexRelations,
                isLoading = isLoading,
                itemId = itemId
            )
        }

        if (itemMarketPriceIndexRelationMetrics != null) {
            return ItemDetailsUiState.HasItemMarketPriceIndexRelationMetrics(
                relationMetrics = itemMarketPriceIndexRelationMetrics,
                isLoading = isLoading,
                itemId = itemId
            )
        }

        return ItemDetailsUiState.NoItem(
            isLoading = isLoading,
            itemId = itemId
        )
    }
}

/**
 * ViewModel that handles the business logic of the Home screen
 */
class ItemDetailsViewModel @AssistedInject constructor(
    private val repository: C3Repository,
    @Assisted("itemId") private val itemId: String,
    @Assisted("po_expressions") private val po_expressions: List<String>,
    @Assisted("po_startDate") private val po_startDate: String,
    @Assisted("po_endDate") private val po_endDate: String,
    @Assisted("po_interval") private val po_interval: String,
    @Assisted("so_expressions") private val so_expressions: List<String>,
    @Assisted("so_startDate") private val so_startDate: String,
    @Assisted("so_endDate") private val so_endDate: String,
    @Assisted("so_interval") private val so_interval: String
) : ViewModel() {

    private val viewModelState = MutableStateFlow(ItemDetailsViewModelState())
    private var offset = 0

    // UI state exposed to the UI
    val uiState = viewModelState
        .map { it.toUiState() }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value.toUiState()
        )

    init {
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
                            openClosedPOLineQty = null,
                            savingsOpportunity = null,
                            suppliers = null,
                            itemVendorRelations = null,
                            itemVendorRelationMetrics = null,
                            marketPriceIndex = null,
                            itemMarketPriceIndexRelations = null,
                            itemMarketPriceIndexRelationMetrics = null,
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
                po_expressions,
                po_startDate,
                po_endDate,
                po_interval
            )
            viewModelState.update {
                when (openClosedPOLineQtyResult) {
                    is Success -> {
                        it.copy(
                            item = null,
                            openClosedPOLineQty = openClosedPOLineQtyResult.data,
                            savingsOpportunity = null,
                            suppliers = null,
                            itemVendorRelations = null,
                            itemVendorRelationMetrics = null,
                            marketPriceIndex = null,
                            itemMarketPriceIndexRelations = null,
                            itemMarketPriceIndexRelationMetrics = null,
                            isLoading = false
                        )
                    }
                    is Error -> {
                        it.copy(isLoading = false)
                    }
                }
            }

            val savingsOpportunityResult = repository.getEvalMetricsForSavingsOpportunity(
                itemId,
                so_expressions,
                so_startDate,
                so_endDate,
                so_interval
            )
            viewModelState.update {
                when (savingsOpportunityResult) {
                    is Success -> {
                        it.copy(
                            item = null,
                            openClosedPOLineQty = null,
                            savingsOpportunity = savingsOpportunityResult.data,
                            suppliers = null,
                            itemVendorRelations = null,
                            itemVendorRelationMetrics = null,
                            marketPriceIndex = null,
                            itemMarketPriceIndexRelations = null,
                            itemMarketPriceIndexRelationMetrics = null,
                            isLoading = false
                        )
                    }
                    is Error -> {
                        it.copy(isLoading = false)
                    }
                }
            }

            val suppliersResult = repository.getItemDetailsSuppliers(itemId, 5)
            viewModelState.update {
                when (suppliersResult) {
                    is Success -> {
                        it.copy(
                            item = null,
                            openClosedPOLineQty = null,
                            savingsOpportunity = null,
                            suppliers = suppliersResult.data,
                            itemVendorRelations = null,
                            itemVendorRelationMetrics = null,
                            marketPriceIndex = null,
                            itemMarketPriceIndexRelations = null,
                            itemMarketPriceIndexRelationMetrics = null,
                            isLoading = false
                        )
                    }
                    is Error -> {
                        it.copy(isLoading = false)
                    }
                }
            }

            val marketPriceIndex = repository.getMarketPriceIndexes()
            viewModelState.update {
                when (marketPriceIndex) {
                    is Success -> {
                        it.copy(
                            item = null,
                            openClosedPOLineQty = null,
                            savingsOpportunity = null,
                            suppliers = null,
                            itemVendorRelations = null,
                            itemVendorRelationMetrics = null,
                            marketPriceIndex = marketPriceIndex.data,
                            itemMarketPriceIndexRelations = null,
                            itemMarketPriceIndexRelationMetrics = null,
                            isLoading = false
                        )
                    }
                    is Error -> {
                        it.copy(isLoading = false)
                    }
                }
            }
        }
    }

    fun getItemVendorRelation(itemId: String, supplierIds: List<String>) {
        viewModelScope.launch {
            val itemVendorRelations =
                repository.getItemVendorRelation(itemId, supplierIds = supplierIds)
            viewModelState.update {
                when (itemVendorRelations) {
                    is Success -> {
                        it.copy(
                            item = null,
                            openClosedPOLineQty = null,
                            savingsOpportunity = null,
                            suppliers = null,
                            itemVendorRelations = itemVendorRelations.data,
                            itemVendorRelationMetrics = null,
                            marketPriceIndex = null,
                            itemMarketPriceIndexRelations = null,
                            itemMarketPriceIndexRelationMetrics = null,
                            isLoading = false
                        )
                    }
                    is Error -> {
                        it.copy(isLoading = false)
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
                        it.copy(
                            item = null,
                            openClosedPOLineQty = null,
                            savingsOpportunity = null,
                            suppliers = null,
                            itemVendorRelations = null,
                            itemVendorRelationMetrics = itemVendorRelationMetrics.data,
                            marketPriceIndex = null,
                            itemMarketPriceIndexRelations = null,
                            itemMarketPriceIndexRelationMetrics = null,
                            isLoading = false
                        )
                    }
                    is Error -> {
                        it.copy(isLoading = false)
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
                        it.copy(
                            item = null,
                            openClosedPOLineQty = null,
                            savingsOpportunity = null,
                            suppliers = null,
                            itemVendorRelations = null,
                            itemVendorRelationMetrics = null,
                            marketPriceIndex = null,
                            itemMarketPriceIndexRelations = itemMarketPriceIndexRelations.data,
                            itemMarketPriceIndexRelationMetrics = null,
                            isLoading = false
                        )
                    }
                    is Error -> {
                        it.copy(isLoading = false)
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
                            item = null,
                            openClosedPOLineQty = null,
                            savingsOpportunity = null,
                            suppliers = null,
                            itemVendorRelations = null,
                            itemVendorRelationMetrics = null,
                            marketPriceIndex = null,
                            itemMarketPriceIndexRelations = null,
                            itemMarketPriceIndexRelationMetrics = itemMarketPriceIndexRelationMetrics.data,
                            isLoading = false
                        )
                    }
                    is Error -> {
                        it.copy(isLoading = false)
                    }
                }
            }
        }
    }

    class Factory(
        private val assistedFactory: ItemDetailsViewModelAssistedFactory,
        private val itemId: String,
        private val po_expressions: List<String>,
        private val po_startDate: String,
        private val po_endDate: String,
        private val po_interval: String,
        private val so_expressions: List<String>,
        private val so_startDate: String,
        private val so_endDate: String,
        private val so_interval: String,
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return assistedFactory.create(
                itemId,
                po_expressions, po_startDate, po_endDate, po_interval,
                so_expressions, so_startDate, so_endDate, so_interval
            ) as T
        }
    }
}

@AssistedFactory
interface ItemDetailsViewModelAssistedFactory {

    fun create(
        @Assisted("itemId") itemId: String,
        @Assisted("po_expressions") po_expressions: List<String>,
        @Assisted("po_startDate") po_startDate: String,
        @Assisted("po_endDate") po_endDate: String,
        @Assisted("po_interval") po_interval: String,
        @Assisted("so_expressions") so_expressions: List<String>,
        @Assisted("so_startDate") so_startDate: String,
        @Assisted("so_endDate") so_endDate: String,
        @Assisted("so_interval") so_interval: String
    ): ItemDetailsViewModel
}
