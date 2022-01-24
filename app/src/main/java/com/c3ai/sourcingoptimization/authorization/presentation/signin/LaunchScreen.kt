package com.c3ai.sourcingoptimization.authorization.presentation.signin

import android.app.Activity
import android.content.Intent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.c3ai.sourcingoptimization.R
import com.c3ai.sourcingoptimization.authorization.presentation.AuthRouter
import com.c3ai.sourcingoptimization.authorization.presentation.AuthViewModel
import com.c3ai.sourcingoptimization.presentation.MainActivity

@ExperimentalAnimationApi
@Composable
fun LaunchScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {

    val state = viewModel.state.value
    val context = LocalContext.current

    LaunchedEffect(true) {
        state.isAuthorized?.let {
            if (it) {
                context.startActivity(Intent(context, MainActivity::class.java))
                (context as? Activity)?.finish()
            } else {
                navController.navigate(AuthRouter.SignInScreen.route) {
                    popUpTo(AuthRouter.LaunchScreen.route) { inclusive = true }
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_app_logo),
            contentDescription = null
        )
        Text(
            text = stringResource(R.string.c3_ai_sourcing),
            modifier = Modifier
                .width(146.dp)
                .padding(top = 8.dp),
            fontWeight = FontWeight.Bold,
            color = Color.White,
            fontSize = 24.sp,
            textAlign = TextAlign.Center
        )
    }
}