package com.c3ai.sourcingoptimization.authorization.presentation

/**
 * The state class for authorization flow
 * */
data class AuthState(
    val login: String = "artem.makhovyk@dataart.com", //artem.makhovyk@dataart.com BA
    val password: String = "0ktverO7", //0ktverO7 BA
    val isLoginEnabled: Boolean = true,
    val isAuthorized: Boolean? = null,
)
