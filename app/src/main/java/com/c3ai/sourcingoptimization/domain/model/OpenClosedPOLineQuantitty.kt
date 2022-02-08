package com.c3ai.sourcingoptimization.domain.model

data class OpenClosedPOLineQtyItem(
    //val result: Pair<String, OpenClosedPOLineResult>
    val result: AA
)

data class AA(
    val item0: OpenClosedPOLineResult
)

data class OpenClosedPOLineResult(
    val OpenPOLineQuantity: POLineQuantity,
    val ClosedPOLineQuantity: POLineQuantity
)

data class POLineQuantity(
    val type: String,
    val count: Int,
    val dates: List<String>,
    val data: List<Double>,
    val missing: List<Int>,
    val unit: Unit,
    val timeZone: String,
    val interval: String,
    val start: String,
    val end: String
)

data class Unit(
    val id: String?,
    val symbol: String,
    val concept: String,
    val name: String,
    val meta: Meta,
    val version: Int
)

data class Meta(
    val tenantTagId: Int,
    val tenant: String,
    val tag: String,
    val created: String,
    val createdBy: String,
    val updated: String,
    val updatedBy: String,
    val timestamp: String
)