package com.c3ai.sourcingoptimization.domain.model

import java.util.*

sealed interface PurchaseOrder {
    val id: String
    val name: String
    val fulfilled: Boolean
    val fulfilledStr: String
    val totalCost: UnitValue
    val totalCostLocal: UnitValue
    val orderCreationDate: Date
    val closedDate: Date
    val numberOfActiveAlerts: Int

    data class Order(
        override val id: String,
        override val name: String,
        override val fulfilled: Boolean,
        override val fulfilledStr: String,
        override val totalCost: UnitValue,
        override val totalCostLocal: UnitValue,
        override val orderCreationDate: Date,
        override val closedDate: Date,
        override val numberOfActiveAlerts: Int,
        val buyer: C3Buyer,
        val to: C3Facility,
        val from: C3Facility,
        val vendor: C3Vendor,
        val orderLines: List<Line>,
    ) : PurchaseOrder {

        companion object
    }

    data class Line(
        override val id: String,
        override val name: String,
        override val fulfilled: Boolean,
        override val fulfilledStr: String,
        override val totalCost: UnitValue,
        override val totalCostLocal: UnitValue,
        override val orderCreationDate: Date,
        override val closedDate: Date,
        override val numberOfActiveAlerts: Int,
        val totalQuantity: UnitValue,
        val unitPrice: UnitValue,
        val unitPriceLocal: UnitValue,
        val requestedDeliveryDate: Date,
        val promisedDeliveryDate: Date,
        val requestedLeadTime: Int,
        val order: Order,
    ) : PurchaseOrder {

        companion object
    }
}