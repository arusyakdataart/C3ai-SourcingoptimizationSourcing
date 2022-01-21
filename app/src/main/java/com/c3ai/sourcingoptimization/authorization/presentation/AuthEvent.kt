package com.c3ai.sourcingoptimization.authorization.presentation

sealed class AuthEvent {
    data class LoginChanged(val text: String): AuthEvent()
    data class PasswordChanged(val text: String): AuthEvent()
}