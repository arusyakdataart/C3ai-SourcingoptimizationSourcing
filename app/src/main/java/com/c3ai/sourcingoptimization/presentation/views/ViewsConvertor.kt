package com.c3ai.sourcingoptimization.presentation.views

import com.c3ai.sourcingoptimization.domain.model.C3Item
import com.c3ai.sourcingoptimization.domain.model.C3UnitValue
import com.c3ai.sourcingoptimization.domain.model.C3Vendor
import com.c3ai.sourcingoptimization.domain.model.PurchaseOrder
import com.c3ai.sourcingoptimization.domain.settings.C3AppSettingsProvider
import com.c3ai.sourcingoptimization.presentation.ViewModelState
import java.util.*
import java.util.concurrent.TimeUnit

fun ViewModelState.convert(item: C3Item): UiItem = UiItem(
    id = item.id,
    name = item.name,
    description = item.description,
    family = item.family,
    numberOfOpenOrders = item.numberOfOpenOrders,
    latestOrderLineDate = item.latestOrderLineDate ?: "",
    lastUnitPricePaid = settings.lastUnitPricePaid(item),
    averageUnitPricePaid = settings.averageUnitPricePaid(item),
    minimumUnitPricePaid = settings.minimumUnitPricePaid(item),
    itemFacilityInventoryParams = item.itemFacilityInventoryParams,
    currentInventory = item.currentInventory,
    unfulfilledOrderQuantity = item.lastUnitPricePaid?.format() ?: "",
    unfulfilledOrderCost = item.lastUnitPricePaid?.format() ?: "",
    numberOfVendors = item.numberOfVendors,
    recentPoLinesCost = item.lastUnitPricePaid?.format() ?: "",
    minPoLinesUnitPrice = item.lastUnitPricePaid?.format() ?: "",
    weightedAveragePoLineUnitPrice = item.lastUnitPricePaid?.format() ?: "",
    hasActiveAlerts = item.hasActiveAlerts,
    numberOfActiveAlerts = item.numberOfActiveAlerts.numberOfActiveAlertsString(),
)

fun ViewModelState.convert(vendor: C3Vendor): UiVendor = UiVendor(
    id = vendor.id,
    name = vendor.name,
    allPOValue = vendor.allPOValue?.format() ?: "",
    active = vendor.active ?: false,
    diversity = vendor.diversity ?: false,
    hasActiveContracts = vendor.hasActiveContracts ?: false,
    location = vendor.location,
    items = vendor.items?.map { convert(it) } ?: emptyList(),
    purchaseOrders = vendor.purchaseOrders?.map { convert(it) } ?: emptyList(),
)

fun ViewModelState.convert(order: PurchaseOrder.Order): UiPurchaseOrder.Order =
    UiPurchaseOrder.Order(
        id = order.id,
        name = order.name,
        fulfilled = order.fulfilled ?: false,
        fulfilledStr = order.fulfilledStr ?: "",
        totalCost = settings.formatTotalCost(order),
        orderCreationDate = settings.format(order.orderCreationDate),
        closedDate = settings.format(order.closedDate),
        numberOfActiveAlerts = order.numberOfActiveAlerts.numberOfActiveAlertsString(),
        buyer = order.buyer,
        to = order.to,
        from = order.from,
        vendor = order.vendor?.let { convert(it) },
        orderLines = order.orderLines?.map { convert(it) } ?: emptyList(),
    )

fun ViewModelState.convert(line: PurchaseOrder.Line): UiPurchaseOrder.Line =
    UiPurchaseOrder.Line(
        id = line.id,
        name = line.name,
        fulfilled = line.fulfilled ?: false,
        fulfilledStr = line.fulfilledStr ?: "",
        totalCost = settings.formatTotalCost(line),
        orderCreationDate = settings.format(line.orderCreationDate),
        closedDate = settings.format(line.closedDate),
        numberOfActiveAlerts = line.numberOfActiveAlerts.numberOfActiveAlertsString(),
        totalQuantity = settings.formatQuantity(line),
        unitPrice = settings.formatUnitPrice(line),
        requestedDeliveryDate = settings.format(line.requestedDeliveryDate),
        promisedDeliveryDate = settings.format(line.promisedDeliveryDate),
        requestedLeadTime = line.requestedLeadTime,
        actualLeadTime = Date().daysBefore(line.promisedDeliveryDate),
        order = line.order?.let { convert(it) },
    )

fun C3AppSettingsProvider.format(date: Date?): String {
    return date?.let { getDateFormatter().format(date) } ?: "-"
}

fun C3AppSettingsProvider.lastUnitPricePaid(source: C3Item): String {
    return when (getCurrencyType()) {
        1 -> source.lastUnitPriceLocalPaid
        else -> source.lastUnitPricePaid
    }?.format() ?: ""
}

fun C3AppSettingsProvider.averageUnitPricePaid(source: C3Item): String {
    return when (getCurrencyType()) {
        1 -> source.averageUnitPriceLocalPaid
        else -> source.averageUnitPricePaid
    }?.format() ?: ""
}

fun C3AppSettingsProvider.minimumUnitPricePaid(source: C3Item): String {
    return when (getCurrencyType()) {
        1 -> source.minimumUnitPriceLocalPaid
        else -> source.minimumUnitPricePaid
    }?.format() ?: ""
}

fun C3AppSettingsProvider.formatTotalCost(source: PurchaseOrder): String {
    return when (getCurrencyType()) {
        1 -> source.totalCostLocal
        else -> source.totalCost
    }?.format() ?: ""
}

fun C3AppSettingsProvider.formatUnitPrice(source: PurchaseOrder.Line): String {
    return when (getCurrencyType()) {
        1 -> source.unitPriceLocal
        else -> source.unitPrice
    }.format("%s%.1f")
}

fun C3UnitValue.format(pattern: String = "%s%.0f"): String {
    return String.format(Locale.getDefault(), pattern, unit.id, value)
}

fun C3AppSettingsProvider.formatQuantity(source: PurchaseOrder.Line): String {
    return source.totalQuantity
        .let { String.format(Locale.getDefault(), "%.0f", it.value) }
}

fun Int.numberOfActiveAlertsString(): String {
    return if (this > 0) this.toString() else ""
}

fun Date.daysBefore(date: Date): Int {
    val days = TimeUnit.DAYS.convert(date.time - time, TimeUnit.MILLISECONDS).toInt()
    return if (days > 0) days else 0
}