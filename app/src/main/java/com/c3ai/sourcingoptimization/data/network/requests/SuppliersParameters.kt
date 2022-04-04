package com.c3ai.sourcingoptimization.data.network.requests

import com.c3ai.sourcingoptimization.utilities.PAGINATED_RESPONSE_LIMIT

data class SuppliersParameters(
    @Transient val itemId: String,
    @Transient val order: String?,
    @Transient val limit: Int? = PAGINATED_RESPONSE_LIMIT,
    @Transient val offset: Int = 0,
) : RequestParameters {
    override val spec: C3Spec = C3Spec(
        limit = limit,
        order = order,
        filter = "items.id == '$itemId'"
    )
}