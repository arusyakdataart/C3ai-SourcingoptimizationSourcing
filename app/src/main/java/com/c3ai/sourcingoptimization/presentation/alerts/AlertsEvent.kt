package com.c3ai.sourcingoptimization.presentation.alerts

sealed class AlertsEvent {
    data class OnSearchInputChanged(val searchInput: String) : AlertsEvent()
    data class OnExpandableItemClick(val itemId: String) : AlertsEvent()
    data class OnSortChanged(val sortOption: String) : AlertsEvent()
    data class OnFilterChanged(val filterOption: String) : AlertsEvent()
}
