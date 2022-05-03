package com.c3ai.sourcingoptimization.presentation.watchlist.suppliers

sealed class EditSuppliersEvent {
    data class OnSearchInputChanged(val searchInput: String) : EditSuppliersEvent()
    data class OnSupplierChecked(val itemId: String) : EditSuppliersEvent()
    data class OnSupplierUnchecked(val itemId: String) : EditSuppliersEvent()
    data class OnRetry(val retry: String) : EditSuppliersEvent()
    data class OnError(val error: String) : EditSuppliersEvent()
}
