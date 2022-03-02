package com.c3ai.sourcingoptimization.presentation.po_details

sealed class PODetailsEvent {
    data class OnSearchInputChanged(val searchInput: String) : PODetailsEvent()
    data class OnSortChanged(val sortOption: String) : PODetailsEvent()
}