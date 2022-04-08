package com.c3ai.sourcingoptimization.domain.use_case

import com.c3ai.sourcingoptimization.data.C3Result
import com.c3ai.sourcingoptimization.data.repository.C3Repository
import com.c3ai.sourcingoptimization.domain.model.Alert
import com.c3ai.sourcingoptimization.domain.model.AlertFeedback

class GetAlertsForUser(private val repository: C3Repository) {

    suspend operator fun invoke(order: String): C3Result<List<Alert>> {
        return repository.getAlertsForUser(order)
    }
}

class GetAlertsFeedbacks(private val repository: C3Repository) {

    suspend operator fun invoke(alertIds: List<String>): C3Result<List<AlertFeedback>> {
        return repository.getAlertsFeedbacks(alertIds, "BA")
    }
}

class UpdateAlerts(private val repository: C3Repository) {

    suspend operator fun invoke(
        alertIds: List<String>,
        statusType: String,
        statusValue: Boolean?
    ) {
        return repository.updateAlert(alertIds, "BA", statusType, statusValue)
    }
}

data class AlertsUseCases(
    val getAlerts: GetAlertsForUser,
    val getAlertsFeedbacks: GetAlertsFeedbacks,
    val updateAlerts: UpdateAlerts
)