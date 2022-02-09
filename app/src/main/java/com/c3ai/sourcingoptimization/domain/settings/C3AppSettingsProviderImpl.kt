package com.c3ai.sourcingoptimization.domain.settings

import android.content.Context
import java.text.SimpleDateFormat
import java.util.*

class C3AppSettingsProviderImpl constructor(context: Context) : C3AppSettingsProvider {

    private val prefs = context.getSharedPreferences(SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE)
    private var currencyType: Int = prefs.getInt(CURRENCY_KEY, CURRENCY_USD)
    private var dateFormatter: SimpleDateFormat

    init {
        val pattern = prefs.getString(DATE_FORMAT_KEY, DATE_FORMAT_MONTH_DAY_YEAR)
        dateFormatter = SimpleDateFormat(pattern, Locale.getDefault())
    }

    override fun getCurrencyType(): Int {
        return currencyType
    }

    override fun getDateFormatter(): SimpleDateFormat {
        return dateFormatter
    }

    companion object {
        private const val SHARED_PREFERENCES_KEY = "com.c3ai.sourcingoptimization.settings"
        private const val CURRENCY_KEY = "currency"
        private const val DATE_FORMAT_KEY = "date_format"

        private const val CURRENCY_USD = 0
        private const val CURRENCY_LOCAL = 1
        private const val DATE_FORMAT_MONTH_DAY_YEAR = "dd/MM/yyyy"
        private const val DATE_FORMAT_DAY_MONTH_YEAR = "MM/dd/yyyy"
    }
}