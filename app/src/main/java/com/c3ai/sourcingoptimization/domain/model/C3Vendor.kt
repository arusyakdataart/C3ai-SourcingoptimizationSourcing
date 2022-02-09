package com.c3ai.sourcingoptimization.domain.model

data class C3Vendor(
    val id: String,
    val name: String,
    val allPOValue: UnitValue?,
    val active: Boolean?,
    val diversity: Boolean?,
    val hasActiveContracts: Boolean?,
    val location: C3Location?,
    val items: List<C3Item>?,
    val purchaseOrders: List<PurchaseOrder.Order>?
) {
    companion object
}