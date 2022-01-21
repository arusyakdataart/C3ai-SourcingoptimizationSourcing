package com.c3ai.sourcingoptimization.authorization.presentation

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.c3ai.sourcingoptimization.authorization.domain.use_case.AuthUseCases
import com.c3ai.sourcingoptimization.data.network.C3Session
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val session: C3Session,
    private val authUseCases: AuthUseCases
) : ViewModel() {

    private val _state = mutableStateOf(AuthState())
    val state: State<AuthState> = _state

    fun onEvent(event: AuthEvent) {
        when (event) {
            is AuthEvent.LoginChanged -> {
                _state.value = state.value.copy(
                    login = event.text,
                    isLoginEnabled = event.text.isNotEmpty() && state.value.password.isNotEmpty()
                )
            }
            is AuthEvent.PasswordChanged -> {
                _state.value = state.value.copy(
                    password = event.text,
                    isLoginEnabled = state.value.login.isNotEmpty() && event.text.isNotEmpty()
                )
            }
        }
    }

    suspend fun authorize() {
        session.login = _state.value.login
        session.password = _state.value.password
        authUseCases.signin()
    }
}