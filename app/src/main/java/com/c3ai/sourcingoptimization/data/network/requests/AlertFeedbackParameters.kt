package com.c3ai.sourcingoptimization.data.network.requests

/**
 * Data class with parameters for AlertFeedback[getAlertsFeedbacks] request
 * @see C3ApiService
 * */
data class AlertFeedbackParameters(
    @Transient val alertIds: List<String>,
    @Transient val userId: String
) : RequestParameters {
    override val spec: C3Spec = C3Spec(
        filter = "user.id == '$userId' && (" + alertIds.joinToString(separator = " || ") {
            "parent.id == '$it'"
        } + ")"
    )
}
