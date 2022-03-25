package com.c3ai.sourcingoptimization.domain.use_case

import com.c3ai.sourcingoptimization.data.C3Result
import com.c3ai.sourcingoptimization.data.repository.C3Repository
import com.c3ai.sourcingoptimization.domain.model.Alert

class GetAlertsForUser(private val repository: C3Repository) {

    suspend operator fun invoke(order: String): C3Result<List<Alert>> {
        return repository.getAlertsForUser(order)
    }
}

data class AlertsUseCases(
    val getAlerts: GetAlertsForUser
)