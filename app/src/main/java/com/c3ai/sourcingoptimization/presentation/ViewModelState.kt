package com.c3ai.sourcingoptimization.presentation

import com.c3ai.sourcingoptimization.domain.settings.SettingsState

abstract class ViewModelState {
    abstract val settings: SettingsState
}