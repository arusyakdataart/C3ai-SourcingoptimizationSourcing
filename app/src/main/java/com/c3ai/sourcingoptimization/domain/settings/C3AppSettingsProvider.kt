package com.c3ai.sourcingoptimization.domain.settings

import java.text.SimpleDateFormat

interface C3AppSettingsProvider {

    fun setCurrencyType(type: Int)

    fun getCurrencyType(): Int

    fun setDateFormatter(dateFormat: String)

    fun getDateFormatter(): SimpleDateFormat

    fun setSearchMode(mode: Int)

    fun getSearchMode(): Int
}