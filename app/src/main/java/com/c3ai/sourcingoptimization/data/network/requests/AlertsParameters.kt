package com.c3ai.sourcingoptimization.data.network.requests

import com.c3ai.sourcingoptimization.utilities.PAGINATED_RESPONSE_LIMIT

/**
 * Data class with parameters for Alert[getAlerts] request
 * @see C3ApiService
 * */
data class AlertsParameters(
    @Transient val order: String,
    @Transient val limit: Int = PAGINATED_RESPONSE_LIMIT,
    @Transient val offset: Int = 0
) : RequestParameters {

    override val spec: C3Spec = C3Spec(
        limit = limit,
        offset = offset,
        order = order
    )
}