package com.c3ai.sourcingoptimization.domain.settings

import java.text.SimpleDateFormat
import java.util.*

class FakeC3AppSettingsProvider : C3AppSettingsProvider {

    override fun getCurrencyType(): Int {
        return 0
    }

    override fun getDateFormatter(): SimpleDateFormat {
        return SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    }
}