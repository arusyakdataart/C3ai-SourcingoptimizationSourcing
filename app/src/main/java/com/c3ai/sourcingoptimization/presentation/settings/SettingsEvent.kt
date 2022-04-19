package com.c3ai.sourcingoptimization.presentation.settings

sealed class SettingsEvent {
    data class OnCurrencyChanged(val newCurrency: Int) : SettingsEvent()
}