package com.c3ai.sourcingoptimization.presentation.search


sealed class SearchEvent {
    data class OnFilterClick(val index: Int) : SearchEvent()
}