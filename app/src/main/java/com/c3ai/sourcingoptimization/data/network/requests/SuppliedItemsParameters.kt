package com.c3ai.sourcingoptimization.data.network.requests

/**
 * Data class with parameters for Supplier[getSuppliedItems] request
 * @see C3ApiService
 * */
data class SuppliedItemsParameters(
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
            "purchaseOrders",
            "purchaseOrders.name",
            "items",
            "items.name",
            "items.description",
            "items.averageUnitPricePaid",
            "purchaseOrders.totalCost",
            "purchaseOrders.orderCreationDate",
            "purchaseOrders.closedDate",
            "purchaseOrders.fulfilled",
            "purchaseOrders.fulfilledStr",
            "purchaseOrders.orderLines",
            "purchaseOrders.orderLines.fulfilled",
            "purchaseOrders.orderLines.fulfilledStr",
            "purchaseOrders.orderLines.unitPrice",
            "purchaseOrders.orderLines.totalCost",
            "purchaseOrders.orderLines.totalCostLocal",
            "purchaseOrders.orderLines.totalQuantity",
            "purchaseOrders.orderLines.item",
            "purchaseOrders.orderLines.item.id",
            "purchaseOrders.orderLines.requestedLeadTime",
            "purchaseOrders.orderLines.requestedDeliveryDate",
            "purchaseOrders.orderLines.promisedDeliveryDate",
            "purchaseOrders.orderLines.orderCreationDate",
            "purchaseOrders.orderLines.closedDate",
            "purchaseOrders.orderLines.order",
            "purchaseOrders.orderLines.order.to",
            "purchaseOrders.orderLines.order.to.name",
            "purchaseOrders.orderLines.order.to.location",
            "purchaseOrders.orderLines.order.to.name",
            "purchaseOrders.orderLines.order.vendor",
            "purchaseOrders.orderLines.order.vendor.name",
            "purchaseOrders.orderLines.order.vendor.location",
            "purchaseOrders.orderLines.order.buyer",
            "purchaseOrders.orderLines.order.buyer.name",
            "purchaseOrders.orderLines.order.buyer.location"
        ),
        filter = "id == '$supplierId'"
    )
}