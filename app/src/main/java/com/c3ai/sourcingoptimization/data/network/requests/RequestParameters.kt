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
    val include: List<String>? = null,
    val filter: String? = null,
    val order: String? = null,
    val limit: Int? = null,
    val offset: Int? = null,
)

/**
 * Interface for Eval Metrics parameters, including specification[C3Spec] with request details.
 * @see C3ApiService
 * */
interface EMRequestParameters {
    val spec: EMSpec
}

/**
 * Base specification for parameters.
 * @see RequestParameters
 * */
data class EMSpec(
    val ids: List<String>? = null,
    val expressions: List<String>? = null,
    val start: String? = null,
    val end: String? = null,
    val interval: String? = null
)