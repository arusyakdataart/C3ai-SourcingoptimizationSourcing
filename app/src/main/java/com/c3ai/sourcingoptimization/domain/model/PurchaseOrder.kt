package com.c3ai.sourcingoptimization.domain.model

import java.util.*

sealed interface PurchaseOrder {
    val id: String
    val name: String?
    val fulfilled: Boolean?
    val fulfilledStr: String?
    val totalCost: C3UnitValue?
    val totalCostLocal: C3UnitValue?
    val orderCreationDate: Date?
    val closedDate: Date?
    val numberOfActiveAlerts: Int

    data class Order(
        override val id: String,
        override val name: String?,
        override val fulfilled: Boolean?,
        override val fulfilledStr: String?,
        override val totalCost: C3UnitValue?,
        override val totalCostLocal: C3UnitValue?,
        override val orderCreationDate: Date?,
        override val closedDate: Date?,
        override val numberOfActiveAlerts: Int,
        val buyer: C3Buyer?,
        var buyerContact: C3BuyerContact?,
        val to: C3Facility?,
        val from: C3Facility?,
        val vendor: C3Vendor?,
        var vendorContract: C3VendorContact?,
        val orderLines: List<Line>?,
    ) : PurchaseOrder {

        companion object
    }

    data class Line(
        override val id: String,
        override val name: String?,
        override val fulfilled: Boolean?,
        override val fulfilledStr: String?,
        override val totalCost: C3UnitValue?,
        override val totalCostLocal: C3UnitValue?,
        override val orderCreationDate: Date?,
        override val closedDate: Date?,
        override val numberOfActiveAlerts: Int,
        val totalQuantity: C3UnitValue,
        val unitPrice: C3UnitValue,
        val unitPriceLocal: C3UnitValue,
        val requestedDeliveryDate: Date,
        val promisedDeliveryDate: Date,
        val requestedLeadTime: Int,
        val order: Order?,
    ) : PurchaseOrder {

        companion object
    }
}