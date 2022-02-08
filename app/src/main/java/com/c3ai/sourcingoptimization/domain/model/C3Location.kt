package com.c3ai.sourcingoptimization.domain.model

data class C3Location(
    val id: String,
    val region: Unit,
    val city: String,
    val address: Address,
    val state: String,
) {

    override fun toString(): String {
        return address.toString()
    }

    companion object
}

data class Address(
    val components: List<AddressComponent>,
    val geometry: Geometry,
) {

    override fun toString(): String {
        return components.joinToString { it.name }
    }
}

data class AddressComponent(
    val abbr: String,
    val name: String,
)

data class Geometry(
    val latitude: Double,
    val longitude: Double,
)