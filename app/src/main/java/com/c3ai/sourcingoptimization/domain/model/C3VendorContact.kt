package com.c3ai.sourcingoptimization.domain.model

data class C3VendorContact(
    val id: String,
    val name: String,
    val location: C3Location,
    val preferredPhoneNumber: C3Number?,
    val preferredEmail: C3CommunicationIdentifier?
) {
    companion object
}