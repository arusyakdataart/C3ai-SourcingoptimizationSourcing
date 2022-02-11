package com.c3ai.sourcingoptimization.data.network.requests

data class SuppliersParameters(
    @Transient val itemId: String,
    @Transient val limit: Int?,
    @Transient val order: String?
) : RequestParameters {
    override val spec: C3Spec = C3Spec(
        limit = limit,
        order = order,
        filter = "items.id == '$itemId'"
    )
}