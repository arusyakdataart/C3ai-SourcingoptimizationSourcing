package com.c3ai.sourcingoptimization.presentation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.c3ai.sourcingoptimization.presentation.po_details.PODetailsRoute
import com.c3ai.sourcingoptimization.presentation.search.SearchRoute
import com.c3ai.sourcingoptimization.presentation.supplier_details.SupplierDetailsRoute
import com.c3ai.sourcingoptimization.presentation.watchlist.suppliers.EditSuppliersRoute
import com.google.gson.Gson

@ExperimentalFoundationApi
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun C3NavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = C3Destinations.SEARCH_ROUTE
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(C3Destinations.SEARCH_ROUTE) {
            SearchRoute(navController = navController)
        }
        composable(
            C3Destinations.SUPPLIER_DETAILS_ROUTE,
            arguments = listOf(navArgument("supplierId") { type = NavType.StringType })
        ) { entry ->
            SupplierDetailsRoute(
                navController = navController,
                supplierId = entry.arguments?.getString("supplierId")
            )
        }
        composable(
            C3Destinations.PO_DETAILS_ROUTE,
            arguments = listOf(navArgument("orderId") { type = NavType.StringType })
        ) { entry ->
            PODetailsRoute(
                navController = navController,
                orderId = entry.arguments?.getString("orderId")
            )
        }
        composable(
            C3Destinations.EDIT_SUPPLIERS_ROUTE,
            arguments = listOf(
                navArgument("itemId") { type = NavType.StringType },
                navArgument("supplierIds") { type = NavType.StringType }
            )
        ) { entry ->
            EditSuppliersRoute(
                navController = navController,
                itemId = entry.arguments?.getString("itemId"),
                supplierIds = Gson().fromJson(
                    entry.arguments?.getString("supplierIds"),
                    Array<String>::class.java
                ).asList()
            )
        }
    }
}
