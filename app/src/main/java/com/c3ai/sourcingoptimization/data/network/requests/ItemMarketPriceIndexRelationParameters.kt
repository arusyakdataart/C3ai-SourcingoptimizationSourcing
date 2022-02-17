package com.c3ai.sourcingoptimization.data.network.requests

/**
 * Data class with parameters for ItemRelation[getItemMarketPriceIndexRelation] request
 * @see C3ApiService
 * */
data class ItemMarketPriceIndexRelationParameters(
    @Transient val itemId: String,
    @Transient val indexId: String
) : RequestParameters {

    override val spec: C3Spec = C3Spec(
        include = listOf(
            "to"
        ),
        filter = "from == '$itemId' && to == '$indexId'"
    )
}