package com.c3ai.sourcingoptimization.data.network.requests

/**
 * Data class with parameters for C3Buyer[getBuyerContacts] request
 * @see C3ApiService
 * */
data class BuyerContactsParameters(
    @Transient val id: String
) : RequestParameters {

    override val spec: C3Spec = C3Spec(
        include = listOf(
            "id",
            "name",
            "currentAddress",
            "preferredEmail.communicationIdentifier", "preferredPhoneNumber.number"
        ),
        filter = "id == '$id'"
    )
}