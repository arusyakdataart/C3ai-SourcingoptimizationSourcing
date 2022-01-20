package com.c3ai.sourcingoptimization.presentation

sealed class Router(val route: String) {
    object SearchScreen: Router("search_screen")
}
