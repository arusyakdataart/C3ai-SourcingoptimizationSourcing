package com.c3ai.sourcingoptimization.domain.use_case.get_c3_items

import com.c3ai.sourcingoptimization.data.Result
import com.c3ai.sourcingoptimization.data.repository.C3Repository
import com.c3ai.sourcingoptimization.domain.model.C3Items
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetC3Items @Inject constructor(private val repository: C3Repository) {

    operator fun invoke(itemId: String) : Flow<Result<C3Items>> = flow {
        val c3Items = repository.getItemDetails(itemId)
        emit(c3Items)
    }
}