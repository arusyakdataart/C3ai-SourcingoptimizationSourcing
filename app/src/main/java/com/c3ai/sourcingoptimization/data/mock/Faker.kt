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
        lastUnitPricePaid = C3UnitValue.fake(),
        averageUnitPricePaid = C3UnitValue.fake(),
        lastUnitPriceLocalPaid = C3UnitValue.fake(),
        averageUnitPriceLocalPaid = C3UnitValue.fake(),
        minimumUnitPricePaid = C3UnitValue.fake(),
        minimumUnitPriceLocalPaid = C3UnitValue.fake(),
        itemFacilityInventoryParams = emptyList(),
        currentInventory = null,
        unfulfilledOrderQuantity = C3UnitValue.fake(),
        unfulfilledOrderCost = C3UnitValue.fake(),
        numberOfVendors = random.nextInt(),
        recentPoLinesCost = C3UnitValue.fake(),
        minPoLinesUnitPrice = C3UnitValue.fake(),
        weightedAveragePoLineUnitPrice = C3UnitValue.fake(),
        hasActiveAlerts = random.nextBoolean(),
        numberOfActiveAlerts = random.nextInt(),
    )
}

fun C3Vendor.Companion.fake(): C3Vendor {
    return C3Vendor(
        id = id(),
        name = name(),
        active = random.nextBoolean(),
        allPOValue = C3UnitValue.fake(),
        diversity = random.nextBoolean(),
        hasActiveContracts = random.nextBoolean(),
        location = C3Location.fake(),
        spend = C3UnitValue.fake(),
        items = emptyList(),
        purchaseOrders = (1..20).map { PurchaseOrder.Order.fake() }
    )
}

fun C3Location.Companion.fake(): C3Location = C3Location(
    id = id(),
    region = C3Unit.fake(),
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

fun C3UnitValue.Companion.fake(): C3UnitValue = C3UnitValue(
    unit = C3Unit.fake(),
    value = random.nextInt(900000).toDouble()
)

fun C3Unit.Companion.fake(): C3Unit = C3Unit(
    id = code(),
    symbol = code(),
    concept = code(),
    name = name(),
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
    totalCost = C3UnitValue.fake(),
    totalCostLocal = C3UnitValue.fake(),
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
        allPOValue = C3UnitValue.fake(),
        diversity = random.nextBoolean(),
        hasActiveContracts = random.nextBoolean(),
        location = C3Location.fake(),
        spend = C3UnitValue.fake(),
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
    totalCost = C3UnitValue.fake(),
    totalCostLocal = C3UnitValue.fake(),
    orderCreationDate = Date(),
    closedDate = Date(),
    numberOfActiveAlerts = 0,
    totalQuantity = C3UnitValue.fake(),
    unitPrice = C3UnitValue.fake(),
    unitPriceLocal = C3UnitValue.fake(),
    requestedDeliveryDate = Date(),
    promisedDeliveryDate = Date(),
    requestedLeadTime = random.nextInt(180),
    order = PurchaseOrder.Order(
        id = id(),
        name = "",
        fulfilled = true,
        fulfilledStr = "Open",
        totalCost = C3UnitValue.fake(),
        totalCostLocal = C3UnitValue.fake(),
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
            allPOValue = C3UnitValue.fake(),
            diversity = random.nextBoolean(),
            hasActiveContracts = random.nextBoolean(),
            location = C3Location.fake(),
            spend = C3UnitValue.fake(),
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