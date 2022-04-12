package com.c3ai.sourcingoptimization.domain.model

data class C3BuyerContact(
    val id: String,
    val name: String = "",
    val currentAddress: C3Location? = null,
    val preferredPhoneNumber: C3Number? = null,
    val preferredEmail: C3CommunicationIdentifier? = null
) {
    companion object
}