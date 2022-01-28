package com.c3ai.sourcingoptimization.data.network.requests

/**
 * Base interface for parameters, including specification[C3Spec] with request details.
 * @see C3ApiService
 * */
interface RequestParameters {
    val spec: C3Spec
}

/**
 * Base specification for parameters.
 * @see RequestParameters
 * */
data class C3Spec(
    val include: List<String>,
    val filter: String? = null,
    val order: String? = null,
    val limit: Int? = null,
    val offset: Int? = null,
)