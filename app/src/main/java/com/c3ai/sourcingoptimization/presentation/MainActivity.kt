package com.c3ai.sourcingoptimization.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.c3ai.sourcingoptimization.utilities.rememberWindowSizeClass
import dagger.hilt.android.AndroidEntryPoint

/**
 * An entry point for authorized users. The activity[MainActivity] setup the application theme and
 * the main navigation.
 * @see SearchScreen
 * */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val windowSizeClass = rememberWindowSizeClass()
            C3App(windowSizeClass)
        }
    }
}