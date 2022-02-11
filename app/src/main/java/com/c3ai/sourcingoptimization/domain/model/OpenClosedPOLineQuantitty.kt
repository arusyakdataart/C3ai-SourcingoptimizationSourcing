package com.c3ai.sourcingoptimization.domain.model

data class OpenClosedPOLineQtyItem(
    //val result: Pair<String, OpenClosedPOLineResult>
    val result: AA?
)

data class AA(
    val item0: OpenClosedPOLineResult?
)

data class OpenClosedPOLineResult(
    val OpenPOLineQuantity: POLineQuantity?,
    val ClosedPOLineQuantity: POLineQuantity?
)

data class POLineQuantity(
    val type: String?,
    val count: Int?,
    val dates: List<String>?,
    val data: List<Int>?,
    val missing: List<Int>?,
    val unit: C3Unit?,
    val timeZone: String?,
    val interval: String?,
    val start: String?,
    val end: String?
)

data class UnitComponent(
    val unit: Id,
    val order: Int,
    val multiplier: Int
)

data class Meta(
    val tenantTagId: Int?,
    val tenant: String?,
    val tag: String?,
    val created: String?,
    val createdBy: String?,
    val updated: String?,
    val updatedBy: String?,
    val timestamp: String?
)