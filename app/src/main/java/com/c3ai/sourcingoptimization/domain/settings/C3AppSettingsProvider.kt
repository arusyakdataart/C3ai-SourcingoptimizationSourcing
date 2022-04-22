package com.c3ai.sourcingoptimization.domain.settings

import androidx.lifecycle.LiveData

interface C3AppSettingsProvider {

    val state: SettingsState

    fun asLiveData(): LiveData<SettingsState>

    fun setCurrencyType(type: Int)

    fun setDateFormatter(dateFormat: String)

    fun setSearchMode(mode: Int)
}