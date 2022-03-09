package com.c3ai.sourcingoptimization.domain.use_case

import com.c3ai.sourcingoptimization.data.C3Result
import com.c3ai.sourcingoptimization.data.repository.C3Repository
import com.c3ai.sourcingoptimization.domain.model.C3BuyerContact
import com.c3ai.sourcingoptimization.domain.model.C3VendorContact


class GetSupplierContacts(private val repository: C3Repository) {

    suspend operator fun invoke(id: String): C3Result<C3VendorContact> {
        return repository.getSupplierContacts(id)
    }
}

class GetBuyerContacts(private val repository: C3Repository) {

    suspend operator fun invoke(id: String): C3Result<C3BuyerContact> {
        return repository.getBuyerContacts(id)
    }
}