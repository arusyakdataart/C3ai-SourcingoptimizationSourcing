package com.c3ai.sourcingoptimization.domain.model

data class C3Vendor(
    val id: String,
    val name: String,
    val allPOValue: C3UnitValue?,
    val openPOValue: C3UnitValue?,
    val active: Boolean?,
    val diversity: Boolean?,
    val hasActiveContracts: Boolean?,
    val hasActiveAlerts: Boolean?,
    val numberOfActiveAlerts: Int?,
    val location: C3Location?,
    val email: String?,
    val phone: String?,
    val spend: C3UnitValue,
    val items: List<C3Item>?,
    val purchaseOrders: List<PurchaseOrder.Order>?
) {
    companion object
}