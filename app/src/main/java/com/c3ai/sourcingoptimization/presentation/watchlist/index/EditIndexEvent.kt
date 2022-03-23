package com.c3ai.sourcingoptimization.presentation.watchlist.index

sealed class EditIndexEvent {
    data class OnSearchInputChanged(val searchInput: String) : EditIndexEvent()
}