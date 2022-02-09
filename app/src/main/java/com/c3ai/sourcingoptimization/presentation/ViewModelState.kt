package com.c3ai.sourcingoptimization.presentation

import com.c3ai.sourcingoptimization.domain.settings.C3AppSettingsProvider

abstract class ViewModelState {
    abstract val settings: C3AppSettingsProvider
}