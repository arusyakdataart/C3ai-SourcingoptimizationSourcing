package com.c3ai.sourcingoptimization.data.network.requests

/**
 * Data class with parameters for AlertFeedback[getAlertsFeedbacks] request
 * @see C3ApiService
 * */
data class UpdateAlertParameters(
    @Transient val ids: List<String>,
    @Transient val user: String,
    @Transient val type: String,
    @Transient val value: Boolean?,
) : StatusParameters {
    override val alertIds = ids
    override val userId = user
    override val statusType = type
    override val statusValue = value
}
