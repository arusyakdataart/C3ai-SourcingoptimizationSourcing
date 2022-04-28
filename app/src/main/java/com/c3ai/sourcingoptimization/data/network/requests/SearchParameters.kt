package com.c3ai.sourcingoptimization.data.network.requests

import com.c3ai.sourcingoptimization.utilities.PAGINATED_RESPONSE_LIMIT

data class SearchParameters(
    val queryString: String,
    val typesToFilter: List<String>?,
    val numResultsToReturn: Int = PAGINATED_RESPONSE_LIMIT,
    @Transient val offset: Int = 0,
) : RequestParameters {
    override val spec: C3Spec = C3Spec(
        limit = numResultsToReturn,
        offset = offset,
    )
}