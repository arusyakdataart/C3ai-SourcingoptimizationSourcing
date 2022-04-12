package com.c3ai.sourcingoptimization.domain.model

data class C3VendorContact(
    val id: String,
    val name: String = "",
    val location: C3Location? = null,
    val phone: String? = "",
    val email: String? = ""
) {
    companion object
}