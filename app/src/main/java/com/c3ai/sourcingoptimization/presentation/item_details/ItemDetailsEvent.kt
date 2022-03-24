package com.c3ai.sourcingoptimization.presentation.item_details

sealed class ItemDetailsEvent {
    data class OnTabItemClick(val tabIndex: Int) : ItemDetailsEvent()
}