package com.c3ai.sourcingoptimization.presentation.supplier_details

sealed class SupplierDetailsEvent {
    data class OnSearchInputChanged(val searchInput: String) : SupplierDetailsEvent()
    data class OnTabItemClick(val tabIndex: Int) : SupplierDetailsEvent()
    data class OnExpandableItemClick(val itemId: String) : SupplierDetailsEvent()
    data class OnSortChanged(val sortOption: String) : SupplierDetailsEvent()
    data class OnRetry(val retry: String) : SupplierDetailsEvent()
    data class OnError(val error: String) : SupplierDetailsEvent()
}