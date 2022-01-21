package com.c3ai.sourcingoptimization.authorization.domain.use_case

import com.c3ai.sourcingoptimization.authorization.domain.C3AuthService

class SignIn(private val service: C3AuthService) {

    suspend operator fun invoke() {
        return service.authorize()
    }
}