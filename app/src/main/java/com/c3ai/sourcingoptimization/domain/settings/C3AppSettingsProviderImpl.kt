package com.c3ai.sourcingoptimization.domain.settings

import android.content.Context
import android.util.Log
import java.text.SimpleDateFormat
import java.util.*

class C3AppSettingsProviderImpl constructor(context: Context) : C3AppSettingsProvider {

    private val prefs = context.getSharedPreferences(SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE)

    override fun setCurrencyType(type: Int) {
        prefs.edit().putInt(CURRENCY_KEY, type).apply()
    }

    override fun getCurrencyType(): Int {
        return prefs.getInt(CURRENCY_KEY, CURRENCY_USD)
    }

    override fun setDateFormatter(dateFormat: String) {
        prefs.edit().putString(DATE_FORMAT_KEY, dateFormat).apply()
    }

    override fun getDateFormatter(): SimpleDateFormat {
        val pattern = prefs.getString(DATE_FORMAT_KEY, DATE_FORMAT_MONTH_DAY_YEAR)
        return SimpleDateFormat(pattern, Locale.getDefault())
    }

    override fun setSearchMode(mode: Int) {
        Log.e("setSearchMode", mode.toString())
        prefs.edit().putInt(SEARCH, mode).apply()
    }

    override fun getSearchMode(): Int {
        Log.e("getSearchMode", "call")
        return prefs.getInt(SEARCH, SEARCH_MODE)
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