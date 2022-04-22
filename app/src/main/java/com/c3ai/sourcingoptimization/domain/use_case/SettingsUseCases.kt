package com.c3ai.sourcingoptimization.domain.use_case

import com.c3ai.sourcingoptimization.data.network.C3Session
import com.c3ai.sourcingoptimization.data.repository.C3Repository

class Logout(
    private val repository: C3Repository,
    private val session: C3Session
) {

    suspend operator fun invoke() {
        session.clear()
    }
}

data class SettingsUseCases(
    val logout: Logout
)