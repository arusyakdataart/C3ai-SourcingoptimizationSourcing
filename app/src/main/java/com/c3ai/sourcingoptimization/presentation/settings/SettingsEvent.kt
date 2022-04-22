package com.c3ai.sourcingoptimization.presentation.settings

sealed class SettingsEvent {
    data class OnCurrencyChanged(val newCurrency: Int) : SettingsEvent()
    data class OnDateFormatChanged(val dateFormat: String) : SettingsEvent()
    data class OnSearchModeChanged(val mode: Int) : SettingsEvent()
    object Logout : SettingsEvent()
}