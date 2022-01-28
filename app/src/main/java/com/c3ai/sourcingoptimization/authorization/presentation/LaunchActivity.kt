package com.c3ai.sourcingoptimization.authorization.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.c3ai.sourcingoptimization.authorization.presentation.signin.LaunchScreen
import com.c3ai.sourcingoptimization.authorization.presentation.signin.SignInScreen
import com.c3ai.sourcingoptimization.ui.theme.C3AppTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * An entry point for the application. Activity with navigation for authorization flow
 * @see LaunchScreen
 * */
@ExperimentalAnimationApi
@AndroidEntryPoint
class LaunchActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            C3AppTheme {
                Surface(
                    color = MaterialTheme.colors.background
                ) {
                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = AuthRouter.LaunchScreen.route
                    ) {
                        composable(route = AuthRouter.LaunchScreen.route) {
                            LaunchScreen(navController = navController)
                        }
                        composable(route = AuthRouter.SignInScreen.route) {
                            SignInScreen(navController = navController)
                        }
                    }
                }
            }
        }
    }
}