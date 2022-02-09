package com.c3ai.sourcingoptimization.data.network.requests

import com.c3ai.sourcingoptimization.domain.model.C3Vendor

/**
 * Data class with parameters for Supplier[C3Vendor] request[getSupplierDetails] with details
 * @see C3ApiService
 * */
data class SupplierDetailsParameters(
    @Transient val supplierId: String
) : RequestParameters {

    override val spec: C3Spec = C3Spec(
        include = listOf(
            "id",
            "name",
            "allPOValue",
            "openPOValue",
            "hasActiveAlerts",
            "hasActiveContracts",
            "numberOfActiveAlerts",
            "diversity",
            "active",
            "location.city",
            "location.region",
            "location.address",
            "items.name",
            "items.description",
            "items.averageUnitPricePaid",
            "purchaseOrders.name",
            "purchaseOrders.totalCost",
            "purchaseOrders.orderCreationDate",
            "purchaseOrders.closedDate",
            "purchaseOrders.fulfilled",
            "purchaseOrders.fulfilledStr",
            "purchaseOrders.vendor.id",
            "purchaseOrders.vendor.name",
            "purchaseOrders.vendor.location.address",
            "purchaseOrders.orderLines.name",
            "purchaseOrders.orderLines.fulfilled",
            "purchaseOrders.orderLines.fulfilledStr",
            "purchaseOrders.orderLines.unitPrice",
            "purchaseOrders.orderLines.unitPriceLocal",
            "purchaseOrders.orderLines.totalCost",
            "purchaseOrders.orderLines.totalCostLocal",
            "purchaseOrders.orderLines.totalQuantity",
            "purchaseOrders.orderLines.item.id",
            "purchaseOrders.orderLines.requestedLeadTime",
            "purchaseOrders.orderLines.requestedDeliveryDate",
            "purchaseOrders.orderLines.promisedDeliveryDate",
            "purchaseOrders.orderLines.orderCreationDate",
            "purchaseOrders.orderLines.closedDate",
            "purchaseOrders.orderLines.order.name",
            "purchaseOrders.orderLines.order.to.name",
            "purchaseOrders.orderLines.order.to.location",
            "purchaseOrders.orderLines.order.vendor.name",
            "purchaseOrders.orderLines.order.vendor.location",
            "purchaseOrders.orderLines.order.buyer.name",
            "purchaseOrders.orderLines.order.buyer.location"
        ),
        filter = "id == '$supplierId'"
    )
}