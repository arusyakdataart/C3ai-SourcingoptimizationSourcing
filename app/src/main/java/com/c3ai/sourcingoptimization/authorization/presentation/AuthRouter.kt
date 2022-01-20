package com.c3ai.sourcingoptimization.authorization.presentation

sealed class AuthRouter(val route: String) {
    object SignInScreen: AuthRouter("signin_screen")
}
