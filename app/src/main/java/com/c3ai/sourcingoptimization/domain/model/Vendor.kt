package com.c3ai.sourcingoptimization.domain.model

data class Vendors(
    val objs: List<Vendor>?,
    val count: Int,
    val hasMore: Boolean
)

data class Vendor(
    val party: Organization?,
    val location: Location?,
    val dunsLookup: String?,
    val rridLookup: String?,
    val hasActiveContracts: Boolean?,
    val contractEffectiveThrough: String?,
    val allPOValue: C3Dimension?,
    val numberOfAllPOs: Int?,
    val allPOItemQuantities: C3Dimension?,
    val openPOValue: C3Dimension?,
    val numOpenPOs: Int?,
    val diversity: Boolean?,
    val active: Boolean?,
    val items: List<C3Item>?,
    val numItems: Int?,
    val spend: C3Dimension,
    val numberOfActiveAlerts: Int?,
    val hasActiveAlerts: Boolean,
    val id: String,
    val name: String,
    val meta: Meta,
    val version: Int,
    val typeIdent: String
)

data class Organization(
    val id: String,
    val name: String,
    val meta: Meta
)

data class Location(
    val address: InlineAddress,
    val postalCode: String,
    val streetAddress: String,
    val city: String,
    val state: String,
    val shortPostalCode: String,
    val country: String,
    val id: String,
    val name: String,
    val meta: Meta,
    val version: Int
)

data class C3Dimension(
    val value: Double,
    val unit: Unit
)

data class InlineAddress(
    val raw: String,
    val formatted: String,
    val components: GeoAddressComponent,
    val geometry: LatLong,
    val timeZone: String
)

data class GeoAddressComponent(
    val name: String,
    val abbr: String
)

data class LatLong(
    val latitude: Double,
    val longitude: Double
)