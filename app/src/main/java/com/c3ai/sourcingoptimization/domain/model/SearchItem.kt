package com.c3ai.sourcingoptimization.domain.model

sealed interface SearchItem {
    val id: String
    val type: String
    val description: String
}

data class RecentSearchItem(
    val input: String,
    val filters: List<Int>? = null,
    override val id: String = "",
    override val type: String = "",
    override val description: String = "",
) : SearchItem

data class ItemSearchItem(
    val name: String,
    override val id: String,
    override val type: String,
    override val description: String,
) : SearchItem

data class SupplierSearchItem(
    val name: String,
    val hasActiveContracts: Boolean,
    val diversity: Boolean,
    override val id: String,
    override val type: String,
    override val description: String,
) : SearchItem

data class AlertSearchItem(
    val alertType: String,
    val currentState: CurrentState,
    val readStatus: String,
    val flagged: Boolean,
    override val id: String,
    override val type: String,
    override val description: String,
) : SearchItem

data class POSearchItem(
    val name: String,
    val order: LinkId,
    val fulfilled: Boolean,
    val fulfilledStr: String,
    override val id: String,
    override val type: String,
    override val description: String,
) : SearchItem

data class POLSearchItem(
    val name: String,
    val itemVendorId: String,
    val order: LinkId,
    val fulfilled: Boolean,
    val fulfilledStr: String,
    override val id: String,
    override val type: String,
    override val description: String,
) : SearchItem

data class UnknownSearchItem(
    override val id: String = "",
    override val type: String = "",
    override val description: String = "",
) : SearchItem

data class LinkId(
    val id: String
)

data class CurrentState(
    val name: String,
    val deleted: Boolean,
)