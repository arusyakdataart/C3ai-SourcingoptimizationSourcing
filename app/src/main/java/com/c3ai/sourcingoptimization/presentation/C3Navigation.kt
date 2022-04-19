package com.c3ai.sourcingoptimization.presentation

import androidx.navigation.*
import androidx.navigation.NavGraph.Companion.findStartDestination

/**
 * Destinations used in the [C3App].
 */
object C3Destinations {
    const val SEARCH_ROUTE = "search"
    const val SETTINGS_ROUTE = "settings"
    const val ITEM_DETAILS_ROUTE = "item_details/{itemId}"
    const val SUPPLIER_DETAILS_ROUTE = "supplier_details/{supplierId}"
    const val PO_DETAILS_ROUTE = "po_details/{orderId}"
    const val EDIT_SUPPLIERS_ROUTE = "edit_suppliers?itemId={itemId}&suppliers={suppliers}"
    const val EDIT_INDEX_ROUTE = "edit_index?indexId={indexId}"
    const val ALERTS_ROUTE = "alerts"
    const val ALERT_SETTINGS_ROUTE = "alert_settings?categories={categories}"
}

/**
 * Navigate to the item details[ItemDetailsRoute]
 *
 * @param itemId the item's id
 */
fun NavController.navigateToItemDetails(
    itemId: String,
) = navigate("item_details/$itemId")

/**
 * Navigate to the supplier details[SupplierDetailsRoute]
 *
 * @param supplierId the supplier's id
 */
fun NavController.navigateToSupplierDetails(
    supplierId: String,
) = navigate("supplier_details/$supplierId")

/**
 * Navigate to the supplier details[PODetailsRoute]
 *
 * @param orderId the order's id
 */
fun NavController.navigateToPoDetails(
    orderId: String,
) = navigate("po_details/$orderId")

/**
 * Navigate to the edit suppliers [EditSuppliersRoute]
 *
 * @param itemId the item's id
 * @param suppliers the selected suppliers
 */
fun NavController.navigateToEditSuppliers(
    itemId: String,
    suppliers: String
) = navigate("edit_suppliers?itemId=$itemId&suppliers=$suppliers")

/**
 * Navigate to the edit index [EditIndexRoute]
 *
 * @param indexId the selected index id
 */
fun NavController.navigateToEditIndex(
    indexId: String
) = navigate("edit_index?indexId=$indexId")

/**
 * Navigate to the alerts[AlertsRoute]
 */
fun NavController.navigateToAlerts() = navigate("alerts")

/**
 * Navigate to the alert settings [AlertSettingsRoute]
 *
 * @param categories the selected alert categories
 */
fun NavController.navigateToAlertSettings(
    categories: String
) = navigate("alert_settings?categories=$categories")

/**
 * Models the navigation actions in the app.
 */
class C3NavigationActions(navController: NavHostController) {
    val navigateToHome: () -> Unit = {
        navController.navigate(C3Destinations.SEARCH_ROUTE) {
            // Pop up to the start destination of the graph to
            // avoid building up a large stack of destinations
            // on the back stack as users select items
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            // Avoid multiple copies of the same destination when
            // reselecting the same item
            launchSingleTop = true
            // Restore state when reselecting a previously selected item
            restoreState = true
        }
    }
}

