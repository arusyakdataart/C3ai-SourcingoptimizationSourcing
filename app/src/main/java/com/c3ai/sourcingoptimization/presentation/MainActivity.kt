package com.c3ai.sourcingoptimization.presentation

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.c3ai.sourcingoptimization.data.C3Result
import com.c3ai.sourcingoptimization.data.mock.fake
import com.c3ai.sourcingoptimization.data.repository.C3MockRepositoryImpl
import com.c3ai.sourcingoptimization.domain.model.C3Vendor
import com.c3ai.sourcingoptimization.utilities.rememberWindowSizeClass
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking

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