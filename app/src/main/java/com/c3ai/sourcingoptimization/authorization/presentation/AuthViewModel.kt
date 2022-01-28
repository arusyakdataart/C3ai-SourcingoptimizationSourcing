package com.c3ai.sourcingoptimization.authorization.presentation

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.c3ai.sourcingoptimization.authorization.domain.use_case.AuthUseCases
import com.c3ai.sourcingoptimization.data.network.C3Session
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel class with all authorization logic. It provides main actions and event handler
 * for authorization flow
 * @see ViewModel
 * */
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val session: C3Session,
    private val authUseCases: AuthUseCases
) : ViewModel() {

    private val _state = mutableStateOf(AuthState())
    val state: State<AuthState> = _state

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        if (session.isValid()) {
            _state.value = state.value.copy(isAuthorized = true)
        } else {
            _state.value = state.value.copy(isAuthorized = false)
        }
    }

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

    fun authorize() {
        viewModelScope.launch {
            session.login = _state.value.login
            session.password = _state.value.password
            authUseCases.signin()
            _eventFlow.emit(UiEvent.OnAuthorized)
        }
    }

    sealed class UiEvent {
        object OnAuthorized : UiEvent()
        object OnAuthorizationFailed : UiEvent()
    }
}