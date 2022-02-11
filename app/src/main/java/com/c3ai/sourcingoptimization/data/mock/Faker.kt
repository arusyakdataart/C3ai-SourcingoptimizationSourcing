package com.c3ai.sourcingoptimization.data.mock

import com.c3ai.sourcingoptimization.domain.model.*
import com.c3ai.sourcingoptimization.utilities.extentions.fastLazy
import java.util.*

private const val dateString = "20/20/2000"
private val random by fastLazy { Random() }

fun C3Item.Companion.fake(): C3Item {
    return C3Item(
        id = id(),
        name = name(),
        description = text(20),
        family = name(),
        numberOfOpenOrders = random.nextInt(50),
        latestOrderLineDate = dateString,
        lastUnitPricePaid = UnitValue.fake(),
        averageUnitPricePaid = UnitValue.fake(),
        lastUnitPriceLocalPaid = UnitValue.fake(),
        averageUnitPriceLocalPaid = UnitValue.fake(),
        minimumUnitPricePaid = UnitValue.fake(),
        minimumUnitPriceLocalPaid = UnitValue.fake(),
        itemFacilityInventoryParams = emptyList(),
        currentInventory = null,
        unfulfilledOrderQuantity = UnitValue.fake(),
        unfulfilledOrderCost = UnitValue.fake(),
        numberOfVendors = random.nextInt(),
        recentPoLinesCost = UnitValue.fake(),
        minPoLinesUnitPrice = UnitValue.fake(),
        weightedAveragePoLineUnitPrice = UnitValue.fake(),
        hasActiveAlerts = random.nextBoolean(),
        numberOfActiveAlerts = random.nextInt(),
    )
}

fun C3Vendor.Companion.fake(): C3Vendor {
    return C3Vendor(
        id = id(),
        name = name(),
        active = random.nextBoolean(),
        allPOValue = UnitValue.fake(),
        diversity = random.nextBoolean(),
        hasActiveContracts = random.nextBoolean(),
        location = C3Location.fake(),
        items = emptyList(),
        purchaseOrders = (1..20).map { PurchaseOrder.Order.fake() }
    )
}

fun C3Location.Companion.fake(): C3Location = C3Location(
    id = id(),
    region = Unit(code()),
    city = name(),
    address = Address(
        components = listOf(
            AddressComponent("", name()),
            AddressComponent("", name()),
            AddressComponent("", name())
        ),
        geometry = Geometry(random.nextDouble(), random.nextDouble())
    ),
    state = name(),
)

fun UnitValue.Companion.fake(): UnitValue = UnitValue(
    unit = Unit(code()),
    value = random.nextInt(900000).toDouble()
)

fun C3Buyer.Companion.fake(): C3Buyer = C3Buyer(
    id = id(),
    name = name()
)

fun C3Facility.Companion.fake(): C3Facility = C3Facility(
    id = id(),
    name = name(),
)

fun PurchaseOrder.Order.Companion.fake(): PurchaseOrder.Order = PurchaseOrder.Order(
    id = id(),
    name = "",
    fulfilled = true,
    fulfilledStr = "Open",
    totalCost = UnitValue.fake(),
    totalCostLocal = UnitValue.fake(),
    orderCreationDate = Date(),
    closedDate = Date(),
    numberOfActiveAlerts = 0,
    buyer = C3Buyer.fake(),
    to = C3Facility.fake(),
    from = C3Facility.fake(),
    vendor = C3Vendor(
        id = id(),
        name = name(),
        active = random.nextBoolean(),
        allPOValue = UnitValue.fake(),
        diversity = random.nextBoolean(),
        hasActiveContracts = random.nextBoolean(),
        location = C3Location.fake(),
        items = emptyList(),
        purchaseOrders = emptyList()
    ),
    orderLines = listOf(PurchaseOrder.Line.fake())
)

fun PurchaseOrder.Line.Companion.fake(): PurchaseOrder.Line = PurchaseOrder.Line(
    id = id(),
    name = "",
    fulfilled = true,
    fulfilledStr = "Open",
    totalCost = UnitValue.fake(),
    totalCostLocal = UnitValue.fake(),
    orderCreationDate = Date(),
    closedDate = Date(),
    numberOfActiveAlerts = 0,
    totalQuantity = UnitValue.fake(),
    unitPrice = UnitValue.fake(),
    unitPriceLocal = UnitValue.fake(),
    requestedDeliveryDate = Date(),
    promisedDeliveryDate = Date(),
    requestedLeadTime = random.nextInt(180),
    order = PurchaseOrder.Order(
        id = id(),
        name = "",
        fulfilled = true,
        fulfilledStr = "Open",
        totalCost = UnitValue.fake(),
        totalCostLocal = UnitValue.fake(),
        orderCreationDate = Date(),
        closedDate = Date(),
        numberOfActiveAlerts = 0,
        buyer = C3Buyer.fake(),
        to = C3Facility.fake(),
        from = C3Facility.fake(),
        vendor = C3Vendor(
            id = id(),
            name = name(),
            active = random.nextBoolean(),
            allPOValue = UnitValue.fake(),
            diversity = random.nextBoolean(),
            hasActiveContracts = random.nextBoolean(),
            location = C3Location.fake(),
            items = emptyList(),
            purchaseOrders = emptyList()
        ),
        orderLines = emptyList()
    ),
)

private fun id(): String = UUID.randomUUID().toString().substring(0, 7)

private fun name(): String = text(random.nextInt(3))

private fun code(): String = world(random.nextInt(4))

private fun world(length: Int = random.nextInt(14), hasCap: Boolean = false): String {
    val allowedCapChars = ('A'..'Z')
    val allowedChars = ('a'..'z')
    val world = (1..length)
        .map { allowedChars.random() }
        .joinToString("")
    return if (hasCap) allowedCapChars.random().toString() + world else world
}

private fun text(length: Int): String {
    return world(hasCap = true) + (1..length).joinToString("") { world() }
}