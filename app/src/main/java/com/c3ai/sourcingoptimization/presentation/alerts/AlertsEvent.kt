package com.c3ai.sourcingoptimization.presentation.alerts

sealed class AlertsEvent {
    data class OnSearchInputChanged(val searchInput: String) : AlertsEvent()
    data class OnCollapsableItemClick(val id: String) : AlertsEvent()
    data class OnSortChanged(val sortOption: String) : AlertsEvent()
    data class OnFilterChanged(val categories: List<String>) : AlertsEvent()
    data class OnFeedbackChanged(val alertId: String, val statusValue: Boolean) : AlertsEvent()
    data class OnFlaggedChanged(val alertId: String, val statusValue: Boolean) : AlertsEvent()
    data class OnSupplierContactSelected(val supplierId: String) : AlertsEvent()
    data class OnRetry(val retry: String) : AlertsEvent()
    data class OnError(val error: String) : AlertsEvent()
}
