package com.c3ai.sourcingoptimization.presentation

/**
 * The main router of the application with routes as constants.
 * @see MainActivity
 * */
sealed class Router(val route: String) {
    object SearchScreen : Router("search_screen")
}
