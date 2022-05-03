package com.c3ai.sourcingoptimization.presentation.watchlist.index

sealed class EditIndexEvent {
    data class OnSearchInputChanged(val searchInput: String) : EditIndexEvent()
    data class OnRetry(val retry: String) : EditIndexEvent()
    data class OnError(val error: String) : EditIndexEvent()
}