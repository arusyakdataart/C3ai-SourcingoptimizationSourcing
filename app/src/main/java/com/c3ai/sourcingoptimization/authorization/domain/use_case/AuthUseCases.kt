package com.c3ai.sourcingoptimization.authorization.domain.use_case

/**
 * Handler for all authorization use cases.
 * @see SignIn, GetUser
 * */
data class AuthUseCases(
    val signin: SignIn,
    val getUser: GetUser
)