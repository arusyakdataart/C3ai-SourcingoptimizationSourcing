package com.c3ai.sourcingoptimization

import android.app.Application
import com.facebook.stetho.Stetho
import dagger.hilt.android.HiltAndroidApp

/**
 * The singleton application class.
 * */
@HiltAndroidApp
class C3Application : Application() {

    override fun onCreate() {
        super.onCreate()

        Stetho.initializeWithDefaults(this);
    }
}