package com.c3ai.sourcingoptimization.domain.model

data class POLine(
    val id: String,
    val totalCost: Double,
    val numberOfActiveAlerts: Int,
    val fulfilledStr: String,
    val unitPrice: Double,
    val totalQuantity: Int,
    val orderCreationDate: String,
    val openedDate: String,
    val closedDate: String,
    val plannedLeadTime: String,
    val requestedDeliveryDate: String,
    val promisedDeliveryDate: String,
    val to: To,
    val buyer: Buyer,
    val vendor: Vendor
)