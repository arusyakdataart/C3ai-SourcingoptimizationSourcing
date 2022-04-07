package com.c3ai.sourcingoptimization.authorization.presentation.signin

import android.app.Activity
import android.content.Intent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.c3ai.sourcingoptimization.R
import com.c3ai.sourcingoptimization.authorization.presentation.AuthEvent
import com.c3ai.sourcingoptimization.authorization.presentation.AuthViewModel
import com.c3ai.sourcingoptimization.authorization.presentation.AuthViewModel.UiEvent
import com.c3ai.sourcingoptimization.authorization.presentation.components.LabeledTextField
import com.c3ai.sourcingoptimization.common.components.SButton
import com.c3ai.sourcingoptimization.common.components.SharedPrefsToggle
import com.c3ai.sourcingoptimization.presentation.MainActivity
import kotlinx.coroutines.flow.collectLatest

/**
 * Sign in screen launches if user is unauthorized.
 * */
@OptIn(ExperimentalFoundationApi::class)
@ExperimentalAnimationApi
@Composable
fun SignInScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val state = viewModel.state.value
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    LaunchedEffect(true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is UiEvent.OnAuthorized -> {
                    context.startActivity(Intent(context, MainActivity::class.java))
                    (context as? Activity)?.finish()
                }
            }
        }
    }

    val username = stringResource(R.string.username)
    val password = stringResource(R.string.password)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_app_logo),
            contentDescription = null
        )
        Text(
            text = stringResource(R.string.c3_ai_sourcing),
            modifier = Modifier.padding(top = 8.dp),
            fontWeight = FontWeight.Bold,
            color = Color.White,
            fontSize = 27.sp
        )
        LabeledTextField(
            modifier = Modifier
                .padding(top = 40.dp),
            label = username,
            placeholder = username,
            value = state.login,
            onValueChange = { text -> viewModel.onEvent(AuthEvent.LoginChanged(text)) }
        )
        LabeledTextField(
            modifier = Modifier
                .padding(top = 16.dp),
            label = password,
            placeholder = password,
            value = state.password,
            onValueChange = { text -> viewModel.onEvent(AuthEvent.PasswordChanged(text)) }
        )
        SharedPrefsToggle(
            text = stringResource(R.string.remember_me),
            value = false,
            onValueChanged = {}
        )
        SButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 40.dp),
            enabled = state.isLoginEnabled,
            text = stringResource(R.string.login),
            onClick = { viewModel.authorize() }
        )
    }
}