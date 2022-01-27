package com.c3ai.sourcingoptimization.utilities.extentions

fun <T> fastLazy(initializer: () -> T) = lazy(LazyThreadSafetyMode.NONE, initializer)