package com.c3ai.sourcingoptimization.authorization.presentation

sealed class AuthRouter(val route: String) {
    object LaunchScreen: AuthRouter("launch_screen")
    object SignInScreen: AuthRouter("signin_screen")
}
