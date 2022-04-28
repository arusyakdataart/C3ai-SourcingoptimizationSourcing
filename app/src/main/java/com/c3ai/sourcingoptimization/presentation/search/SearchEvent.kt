package com.c3ai.sourcingoptimization.presentation.search

import com.c3ai.sourcingoptimization.domain.model.RecentSearchItem


sealed class SearchEvent {
    data class OnFilterClick(val index: Int) : SearchEvent()
    data class OnSearchRecentClick(val item: RecentSearchItem) : SearchEvent()
}