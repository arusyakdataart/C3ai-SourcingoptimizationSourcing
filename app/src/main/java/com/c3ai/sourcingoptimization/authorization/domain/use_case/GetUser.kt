package com.c3ai.sourcingoptimization.authorization.domain.use_case

import com.c3ai.sourcingoptimization.authorization.domain.C3AuthService
import com.c3ai.sourcingoptimization.authorization.domain.model.User

/**
 * Use case to request user data.
 * */
class GetUser(private val service: C3AuthService) {

    suspend operator fun invoke(): User {
        return service.getUser()
    }
}