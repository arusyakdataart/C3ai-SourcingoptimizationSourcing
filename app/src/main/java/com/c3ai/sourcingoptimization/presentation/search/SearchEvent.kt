package com.c3ai.sourcingoptimization.presentation.search


sealed class SearchEvent {
    data class OnQueryChange(val query: String) : SearchEvent()
    data class OnFilterClick(val index: Int) : SearchEvent()
    object Search : SearchEvent()
}