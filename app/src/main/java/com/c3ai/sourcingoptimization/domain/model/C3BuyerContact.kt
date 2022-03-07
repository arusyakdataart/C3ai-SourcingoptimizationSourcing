package com.c3ai.sourcingoptimization.domain.model

data class C3BuyerContact(
    val id: String,
    val name: String,
    val currentAddress: C3Location?,
    val preferredPhoneNumber: C3Number?,
    val preferredEmail: C3CommunicationIdentifier?
) {
    companion object
}