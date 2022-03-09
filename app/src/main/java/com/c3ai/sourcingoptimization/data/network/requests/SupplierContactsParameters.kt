package com.c3ai.sourcingoptimization.data.network.requests

/**
 * Data class with parameters for C3Vendor[getSupplierContacts] request
 * @see C3ApiService
 * */
data class SupplierContactsParameters(
    @Transient val id: String
) : RequestParameters {

    override val spec: C3Spec = C3Spec(
        include = listOf(
            "id",
            "name",
            "mail",
            "phone",
            "location.address"
        ),
        filter = "id == '$id'"
    )
}