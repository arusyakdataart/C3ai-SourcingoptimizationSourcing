package com.c3ai.sourcingoptimization.domain.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class FakeC3AppSettingsProvider() : C3AppSettingsProvider {

    override val state: SettingsState = SettingsState(
        0,
        "dd/MM/yyyy",
        0,
    )

    override fun asLiveData(): LiveData<SettingsState> {
        return MutableLiveData(
            SettingsState(
                currencyType = 0,
                dateFormat = "dd/MM/yyyy",
                searchMode = 0,
            )
        )
    }

    override fun setCurrencyType(type: Int) {
    }

    override fun setDateFormatter(dateFormat: String) {
    }

    override fun setSearchMode(mode: Int) {
    }
}