package com.c3ai.sourcingoptimization.data.network.requests

/**
 * Data class with parameters for Item[getEvalMetrics] request
 * @see C3ApiService
 * */
data class EvalMetricsParameters(
    @Transient val ids: List<String>,
    @Transient val expressions: List<String>,
    @Transient val startDate: String,
    @Transient val endDate: String,
    @Transient val interval: String
) : EMRequestParameters {

    override val spec: EMSpec = EMSpec(
        ids = ids,
        expressions = expressions,
        start = startDate,
        end = endDate,
        interval = interval
    )
}