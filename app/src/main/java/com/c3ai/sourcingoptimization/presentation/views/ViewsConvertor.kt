package com.c3ai.sourcingoptimization.presentation.views

import com.c3ai.sourcingoptimization.domain.model.*
import com.c3ai.sourcingoptimization.domain.settings.SettingsState
import com.c3ai.sourcingoptimization.presentation.ViewModelState
import java.text.SimpleDateFormat
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
    currentInventory = item.currentInventory?.value.toString() ?: "",
    unfulfilledOrderQuantity = item.lastUnitPricePaid?.format() ?: "",
    unfulfilledOrderCost = item.lastUnitPricePaid?.format() ?: "",
    numberOfVendors = item.numberOfVendors,
    recentPoLinesCost = item.lastUnitPricePaid?.format() ?: "",
    minPoLinesUnitPrice = item.lastUnitPricePaid?.format() ?: "",
    weightedAveragePoLineUnitPrice = item.lastUnitPricePaid?.format() ?: "",
    hasActiveAlerts = item.hasActiveAlerts,
    numberOfActiveAlerts = item.numberOfActiveAlerts?.numberOfActiveAlertsString() ?: "",
)

fun ViewModelState.convert(vendor: C3Vendor): UiVendor = UiVendor(
    id = vendor.id,
    name = vendor.name ?: "",
    allPOValue = vendor.allPOValue?.format() ?: "",
    openPOValue = vendor.openPOValue?.format() ?: "",
    closedPOValue = vendor.allPOValue?.let {
        C3UnitValue(
            it.unit,
            it.value - (vendor.openPOValue?.value ?: 0.0)
        ).format()
    } ?: "",
    avgPOValue = vendor.allPOValue?.let {
        C3UnitValue(
            it.unit,
            it.value / 2
        ).format()
    } ?: "",
    active = vendor.active ?: false,
    diversity = vendor.diversity ?: false,
    hasActiveContracts = vendor.hasActiveContracts ?: false,
    hasActiveAlerts = vendor.hasActiveAlerts ?: false,
    numberOfActiveAlerts = vendor.numberOfActiveAlerts?.numberOfActiveAlertsString() ?: "",
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
        numberOfActiveAlerts = order.numberOfActiveAlerts?.numberOfActiveAlertsString() ?: "",
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
        actualLeadTime = Date().daysBefore(line?.promisedDeliveryDate),
        order = line.order?.let { convert(it) },
    )

fun ViewModelState.convert(
    savingsOppItem: SavingsOpportunityItem,
    itemId: String
): UiSavingsOpportunityItem = UiSavingsOpportunityItem(
    savingOppText = savingsOppItem.let { item ->
        val savingOppText = item.result[itemId]?.SavingsOpportunityCompound?.missing?.let { list ->
            val filteredList = list.filter { it < 100 }
            if (filteredList.isEmpty()) "0" else filteredList.sum().div(filteredList.size)
        }
        String.format("%s%s", "$", savingOppText)
    } ?: "",
    data = savingsOppItem.result[itemId]?.SavingsOpportunityCompound?.data
        ?: emptyList()
)

fun ViewModelState.convert(
    ocPOLineQtyItem: OpenClosedPOLineQtyItem?,
    itemId: String
): UiOpenClosedPOLineQtyItem = UiOpenClosedPOLineQtyItem(
    closedValueText = String.format(
        "%s%s", "$",
        (ocPOLineQtyItem?.result?.get(itemId)?.ClosedPOLineQuantity?.data?.get(0) ?: "").toString()
    ),
    openValueText = String.format(
        "%s%s", "$",
        (ocPOLineQtyItem?.result?.get(itemId)?.OpenPOLineQuantity?.data?.get(0) ?: "").toString()
    ),
)

fun ViewModelState.convert(
    alerts: Set<Alert>,
    feedBacks: Set<AlertFeedback>,
    supplierContracts: List<C3VendorContact>
): List<UiAlert> {
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
            redirectUrl = it.redirectUrl,
            feedback = feedBacks.findLast { it1 -> it.id == it1.parent?.id },
            supplierContract = supplierContracts.find { it1 -> it.id == it1.id }
        )
    }
    return uiAlerts
}

fun filterByCategory(alerts: List<UiAlert>, categories: Set<String>): List<UiAlert> {
    if (categories.isEmpty()) {
        return alerts
    }
    val filteredAlerts = mutableListOf<UiAlert>()
    alerts.forEach {
        if (categories.contains(it.category?.name)) {
            filteredAlerts.add(it)
        }
    }
    return filteredAlerts
}

fun SettingsState.format(date: Date?): String {
    return date?.let { SimpleDateFormat(dateFormat, Locale.getDefault()).format(date) } ?: "-"
}

fun SettingsState.lastUnitPricePaid(source: C3Item): String {
    return when (currencyType) {
        1 -> source.lastUnitPriceLocalPaid
        else -> source.lastUnitPricePaid
    }?.format() ?: ""
}

fun SettingsState.averageUnitPricePaid(source: C3Item): String {
    return when (currencyType) {
        1 -> source.averageUnitPriceLocalPaid
        else -> source.averageUnitPricePaid
    }?.format() ?: ""
}

fun SettingsState.minimumUnitPricePaid(source: C3Item): String {
    return when (currencyType) {
        1 -> source.minimumUnitPriceLocalPaid
        else -> source.minimumUnitPricePaid
    }?.format() ?: ""
}

fun SettingsState.formatTotalCost(source: PurchaseOrder): String {
    return when (currencyType) {
        1 -> source.totalCostLocal
        else -> source.totalCost
    }?.format() ?: ""
}

fun SettingsState.formatUnitPrice(source: PurchaseOrder.Line): String {
    return when (currencyType) {
        1 -> source.unitPriceLocal
        else -> source.unitPrice
    }.format("%s%.1f")
}

fun C3UnitValue.format(pattern: String = "%s%.0f"): String {
    return String.format(Locale.getDefault(), pattern, unit.id, value)
}

fun SettingsState.formatQuantity(source: PurchaseOrder.Line): String {
    return source.totalQuantity
        .let { String.format(Locale.getDefault(), "%.0f", it.value) }
}

fun Int.numberOfActiveAlertsString(): String {
    return if (this > 0) this.toString() else ""
}

fun Date.daysBefore(date: Date?): Int {
    val days = TimeUnit.DAYS.convert(
        date?.time ?: Calendar.getInstance().timeInMillis - time,
        TimeUnit.MILLISECONDS
    ).toInt()
    return if (days > 0) days else 0
}