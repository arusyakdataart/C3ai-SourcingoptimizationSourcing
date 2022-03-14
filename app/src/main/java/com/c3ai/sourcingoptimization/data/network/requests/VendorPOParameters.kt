package com.c3ai.sourcingoptimization.data.network.requests

import com.c3ai.sourcingoptimization.utilities.PAGINATED_RESPONSE_LIMIT

/**
 * Data class with parameters for PurchaseOrder[getPOsForVendor] request
 * @see C3ApiService
 * */
data class VendorPOParameters(
    @Transient val vendorId: String,
    @Transient val order: String?,
    @Transient val limit: Int = PAGINATED_RESPONSE_LIMIT,
    @Transient val offset: Int = 0
) : RequestParameters {

    override val spec: C3Spec = C3Spec(
        include = listOf(
            "id",
            "name",
            "totalCost",
            "orderCreationDate",
            "closedDate",
            "fulfilledStr",
            "buyer.id",
            "buyer.name",
            "vendor.id",
            "vendor.name",
            "vendor.numberOfActiveAlerts",
            "vendor.location.address",
            "orderLines",
            "orderLines.actualLeadTime",
            "orderLines.promisedDeliveryDate",
            "orderLines.requestedDeliveryDate",
            "orderLines.unitPrice",
            "orderLines.unitPriceLocal",
            "orderLines.requestedLeadTime",
            "orderLines.plannedLeadTime",
            "orderLines.totalQuantity",
            "orderLines.fulfilled",
            "orderLines.fulfilledStr",
            "orderLines.unitPrice",
            "orderLines.totalCost",
            "orderLines.totalCostLocal",
            "orderLines.totalQuantity",
            "orderLines.item",
            "orderLines.item.id",
            "orderLines.requestedLeadTime",
            "orderLines.requestedDeliveryDate",
            "orderLines.promisedDeliveryDate",
            "orderLines.orderCreationDate",
            "orderLines.closedDate",
            "orderLines.order",
            "orderLines.order.to",
            "orderLines.order.to.name",
            "orderLines.order.to.location",
            "orderLines.order.to.name",
            "orderLines.order.vendor",
            "orderLines.order.vendor.name",
            "orderLines.order.vendor.location",
            "orderLines.order.buyer",
            "orderLines.order.buyer.name",
            "orderLines.order.buyer.location"
        ),
        limit = limit,
        offset = offset,
        filter = "vendor.id == '$vendorId'",
        order = order
    )
}