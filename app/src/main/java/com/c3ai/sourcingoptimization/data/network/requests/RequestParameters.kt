package com.c3ai.sourcingoptimization.data.network.requests

interface RequestParameters {
    val spec: C3Spec
}

data class C3Spec(
    val include: List<String>,
    val filter: String
)