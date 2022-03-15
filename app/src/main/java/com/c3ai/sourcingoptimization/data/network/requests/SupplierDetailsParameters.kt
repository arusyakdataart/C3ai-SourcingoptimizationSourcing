package com.c3ai.sourcingoptimization.data.network.requests

import com.c3ai.sourcingoptimization.domain.model.C3Vendor

/**
 * Data class with parameters for Supplier[C3Vendor] request[getSupplierDetails] with details
 * @see C3ApiService
 * */
data class SupplierDetailsParameters(
    @Transient val supplierId: String
) : RequestParameters {

    override val spec: C3Spec = C3Spec(
        include = listOf(
            "id",
            "name",
            "allPOValue",
            "openPOValue",
            "hasActiveAlerts",
            "hasActiveContracts",
            "numberOfActiveAlerts",
            "diversity",
            "active",
            "email",
            "phone",
            "location.city",
            "location.region",
            "location.address"
        ),
        filter = "id == '$supplierId'"
    )
}