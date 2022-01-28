package com.c3ai.sourcingoptimization.authorization.presentation

/**
 * The state class for authorization flow
 * */
data class AuthState(
    val login: String = "BA",
    val password: String = "BA",
    val isLoginEnabled: Boolean = true,
    val isAuthorized: Boolean? = null,
)
