package com.c3ai.sourcingoptimization.presentation.item_details

sealed class ItemDetailsEvent {
    data class OnTabItemClick(val tabIndex: Int) : ItemDetailsEvent()
    data class OnDateRangeSelected(val selected: Int) : ItemDetailsEvent()
    data class OnStatsTypeSelected(val selected: Int) : ItemDetailsEvent()
    data class UpdateSourcingAnalysis(val index: Int) : ItemDetailsEvent()
    data class OnSortChanged(val sortOption: String) : ItemDetailsEvent()
    data class OnSupplierContactSelected(val supplierId: String) : ItemDetailsEvent()
}