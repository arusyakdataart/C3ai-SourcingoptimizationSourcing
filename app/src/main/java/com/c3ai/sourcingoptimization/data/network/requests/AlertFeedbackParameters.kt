package com.c3ai.sourcingoptimization.data.network.requests

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
