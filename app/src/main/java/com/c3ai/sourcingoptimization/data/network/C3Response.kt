package com.c3ai.sourcingoptimization.data.network

data class C3Response<out T>(
    val count: Int,
    val hasMore: Boolean,
    val objs: List<T>
)