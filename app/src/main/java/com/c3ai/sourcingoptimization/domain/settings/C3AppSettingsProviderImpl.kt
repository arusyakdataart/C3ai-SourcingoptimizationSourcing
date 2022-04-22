package com.c3ai.sourcingoptimization.domain.settings

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class C3AppSettingsProviderImpl constructor(context: Context) : C3AppSettingsProvider {

    private val prefs = context.getSharedPreferences(SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE)

    private val stateLive: MutableLiveData<SettingsState> by lazy {
        MutableLiveData<SettingsState>(SettingsState(
            currencyType = prefs.getInt(CURRENCY_KEY, CURRENCY_USD),
            dateFormat = prefs.getString(DATE_FORMAT_KEY, DATE_FORMAT_MONTH_DAY_YEAR)!!,
            searchMode = prefs.getInt(SEARCH, SEARCH_MODE)
        ))
    }
    override val state: SettingsState
        get() = stateLive.value!!


    override fun asLiveData(): LiveData<SettingsState> {
        return stateLive
    }

    override fun setCurrencyType(type: Int) {
        stateLive.postValue(stateLive.value!!.copy(currencyType = type))
        prefs.edit().putInt(CURRENCY_KEY, type).apply()
    }

    override fun setDateFormatter(dateFormat: String) {
        stateLive.postValue(stateLive.value!!.copy(dateFormat = dateFormat))
        prefs.edit().putString(DATE_FORMAT_KEY, dateFormat).apply()
    }

    override fun setSearchMode(mode: Int) {
        stateLive.postValue(stateLive.value!!.copy(searchMode = mode))
        prefs.edit().putInt(SEARCH, mode).apply()
    }

    companion object {
        private const val SHARED_PREFERENCES_KEY = "com.c3ai.sourcingoptimization.settings"
        private const val CURRENCY_KEY = "currency"
        private const val DATE_FORMAT_KEY = "date_format"
        private const val SEARCH = "search"

        const val CURRENCY_USD = 0
        const val CURRENCY_LOCAL = 1
        const val DATE_FORMAT_MONTH_DAY_YEAR = "dd/MM/yyyy"
        const val DATE_FORMAT_DAY_MONTH_YEAR = "MM/dd/yyyy"
        const val SEARCH_MODE = 0
        const val SEARCH_ALERTS_MODE = 1
    }
}