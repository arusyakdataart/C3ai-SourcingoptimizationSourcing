package com.c3ai.sourcingoptimization.presentation.views

import com.c3ai.sourcingoptimization.common.AlertTypes
import com.c3ai.sourcingoptimization.domain.model.*
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
    openPOValue = vendor.openPOValue?.format() ?: "",
    active = vendor.active ?: false,
    diversity = vendor.diversity ?: false,
    hasActiveContracts = vendor.hasActiveContracts ?: false,
    hasActiveAlerts = vendor.hasActiveAlerts ?: false,
    numberOfActiveAlerts = vendor.numberOfActiveAlerts ?: 0,
    location = vendor.location,
    email = vendor.email,
    phone = vendor.phone,
    items = vendor.items?.map { convert(it) } ?: emptyList(),
    purchaseOrders = vendor.purchaseOrders?.map { convert(it) } ?: emptyList(),
)

fun ViewModelState.convert(order: PurchaseOrder.Order): UiPurchaseOrder.Order =
    UiPurchaseOrder.Order(
        id = order.id,
        name = order.name ?: "",
        fulfilled = order.fulfilled ?: false,
        fulfilledStr = order.fulfilledStr ?: "",
        totalCost = settings.formatTotalCost(order),
        orderCreationDate = settings.format(order.orderCreationDate),
        closedDate = settings.format(order.closedDate),
        numberOfActiveAlerts = order.numberOfActiveAlerts.numberOfActiveAlertsString(),
        buyer = order.buyer,
        buyerContact = order.buyerContact,
        to = order.to,
        from = order.from,
        vendor = order.vendor?.let { convert(it) },
        vendorContact = order.vendorContract,
        orderLines = order.orderLines?.map { convert(it) } ?: emptyList(),
    )

fun ViewModelState.convert(line: PurchaseOrder.Line): UiPurchaseOrder.Line =
    UiPurchaseOrder.Line(
        id = line.id,
        name = line.name ?: "",
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

fun ViewModelState.convert(alerts: List<Alert>): List<UiAlertWithCategory> {
    val uiAlerts = alerts.map {
        UiAlert(
            id = it.id,
            alertType = it.alertType,
            category = it.category,
            description = it.description,
            currentState = it.currentState,
            readStatus = it.readStatus,
            flagged = it.flagged,
            timestamp = settings.format(it.timestamp),
            redirectUrl = it.redirectUrl
        )
    }
    val uiAlertsWithCategory = mutableMapOf<String, MutableList<UiAlertWithCategory>>()
    uiAlertsWithCategory.put(AlertTypes.NEW_LOWEST_PRICE.categoryName, mutableListOf())
    uiAlertsWithCategory.put(AlertTypes.UNEXPECTED_PRICE_INCREASE.categoryName, mutableListOf())
    uiAlertsWithCategory.put(
        AlertTypes.REQUESTED_DELIVERY_DATE_CHANGE.categoryName,
        mutableListOf()
    )
    uiAlertsWithCategory.put(AlertTypes.SHORT_CYCLED_PURCHASE_ORDER.categoryName, mutableListOf())
    uiAlertsWithCategory.put(AlertTypes.INDEX_PRICE_CHANGE.categoryName, mutableListOf())
    uiAlertsWithCategory.put(
        AlertTypes.CORRELATED_INDEX_PRICING_ANOMALY.categoryName,
        mutableListOf()
    )
    uiAlertsWithCategory.put(AlertTypes.D_U_N_S_RISK.categoryName, mutableListOf())
    uiAlertsWithCategory.put(AlertTypes.RAPID_RATINGS_RISK.categoryName, mutableListOf())

    uiAlerts.forEach {
        uiAlertsWithCategory.get(it.category?.name)
            ?.add(UiAlertWithCategory(it.category?.name ?: "", it))
    }

    val uiAlertsList = mutableListOf<UiAlertWithCategory>()
    uiAlertsWithCategory.values.forEach {
        if (it.isNotEmpty()) {
            uiAlertsList.add(UiAlertWithCategory(it[0].category))
            uiAlertsList.addAll(it)
        }
    }
    return uiAlertsList
}

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