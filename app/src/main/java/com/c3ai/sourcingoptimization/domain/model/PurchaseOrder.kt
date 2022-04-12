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
    val numberOfActiveAlerts: Int?

    data class Order(
        override val id: String,
        override val name: String? = "",
        override val fulfilled: Boolean? = false,
        override val fulfilledStr: String? = "",
        override val totalCost: C3UnitValue? = null,
        override val totalCostLocal: C3UnitValue? = null,
        override val orderCreationDate: Date? = null,
        override val closedDate: Date? = null,
        override val numberOfActiveAlerts: Int? = null,
        val buyer: C3Buyer? = null,
        var buyerContact: C3BuyerContact? = null,
        val to: C3Facility? = null,
        val from: C3Facility? = null,
        val vendor: C3Vendor? = null,
        var vendorContract: C3VendorContact? = null,
        val orderLines: List<Line>? = emptyList(),
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