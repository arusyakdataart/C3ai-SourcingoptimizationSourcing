package com.c3ai.sourcingoptimization.domain.model

data class C3Vendor(
    val id: String,
    val name: String = "",
    val allPOValue: C3UnitValue? = null,
    val openPOValue: C3UnitValue? = null,
    val active: Boolean? = false,
    val diversity: Boolean? = false,
    val hasActiveContracts: Boolean? = false,
    val hasActiveAlerts: Boolean? = false,
    val numberOfActiveAlerts: Int? = 0,
    val location: C3Location? = null,
    val email: String? = "",
    val phone: String? = "",
    val spend: C3UnitValue? = null,
    val items: List<C3Item>? = emptyList(),
    val purchaseOrders: List<PurchaseOrder.Order>? = emptyList(),
) {
    companion object
}