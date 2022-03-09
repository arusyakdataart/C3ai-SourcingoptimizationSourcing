package com.c3ai.sourcingoptimization.presentation.views

import com.c3ai.sourcingoptimization.domain.model.C3Buyer
import com.c3ai.sourcingoptimization.domain.model.C3BuyerContact
import com.c3ai.sourcingoptimization.domain.model.C3Facility
import com.c3ai.sourcingoptimization.domain.model.C3VendorContact

sealed interface UiPurchaseOrder {
    val id: String
    val name: String?
    val fulfilled: Boolean
    val fulfilledStr: String
    val totalCost: String
    val orderCreationDate: String
    val closedDate: String
    val numberOfActiveAlerts: String

    data class Order(
        override val id: String,
        override val name: String,
        override val fulfilled: Boolean,
        override val fulfilledStr: String,
        override val totalCost: String,
        override val orderCreationDate: String,
        override val closedDate: String,
        override val numberOfActiveAlerts: String,
        val buyer: C3Buyer?,
        val buyerContact: C3BuyerContact?,
        val to: C3Facility?,
        val from: C3Facility?,
        val vendor: UiVendor?,
        val vendorContact: C3VendorContact?,
        val orderLines: List<Line>,
    ) : UiPurchaseOrder

    data class Line(
        override val id: String,
        override val name: String?,
        override val fulfilled: Boolean,
        override val fulfilledStr: String,
        override val totalCost: String,
        override val orderCreationDate: String,
        override val closedDate: String,
        override val numberOfActiveAlerts: String,
        val totalQuantity: String,
        val unitPrice: String,
        val requestedDeliveryDate: String,
        val promisedDeliveryDate: String,
        val requestedLeadTime: Int,
        val actualLeadTime: Int,
        val order: Order?,
    ) : UiPurchaseOrder
}