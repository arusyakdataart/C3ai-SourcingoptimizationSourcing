package com.c3ai.sourcingoptimization.presentation.watchlist.suppliers

sealed class EditSuppliersEvent {
    data class OnSearchInputChanged(val searchInput: String) : EditSuppliersEvent()
    data class OnSupplierChecked(val itemId: String) : EditSuppliersEvent()
    data class OnSupplierUnchecked(val itemId: String) : EditSuppliersEvent()
}
