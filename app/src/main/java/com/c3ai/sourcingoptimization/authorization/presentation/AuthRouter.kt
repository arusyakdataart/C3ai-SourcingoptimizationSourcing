package com.c3ai.sourcingoptimization.authorization.presentation

/**
 * The list of routs for authorization flow
 * */
sealed class AuthRouter(val route: String) {
    object LaunchScreen : AuthRouter("launch_screen")
    object SignInScreen : AuthRouter("signin_screen")
}
