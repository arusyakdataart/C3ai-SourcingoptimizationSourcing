package com.c3ai.sourcingoptimization.data.network.requests

/**
 * Data class with parameters for ItemRelation[getItemVendorRelation] request
 * @see C3ApiService
 * */
data class ItemVendorRelationParameters(
    @Transient val itemId: String,
    @Transient val supplierIds: List<String>
) : RequestParameters {

    override val spec: C3Spec = C3Spec(
        include = listOf(
            "to"
        ),
        filter = "from == '$itemId' && (" + supplierIds.joinToString(separator = " || ") {
            "to == '$it'"
        } + ")"
    )
}