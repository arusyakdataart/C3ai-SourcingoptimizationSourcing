package com.c3ai.sourcingoptimization.domain.settings

import java.text.SimpleDateFormat

interface C3AppSettingsProvider {

    fun getCurrencyType(): Int

    fun getDateFormatter(): SimpleDateFormat
}