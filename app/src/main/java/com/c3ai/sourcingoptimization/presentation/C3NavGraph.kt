package com.c3ai.sourcingoptimization.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.c3ai.sourcingoptimization.presentation.search.SearchRoute
import com.c3ai.sourcingoptimization.presentation.supplier_details.SupplierDetailsRoute

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
    }
}
