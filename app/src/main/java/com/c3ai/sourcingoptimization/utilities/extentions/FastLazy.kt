package com.c3ai.sourcingoptimization.utilities.extentions

/**
 * A wrapper for function lazy[lazy] with specified mode, that is fast but not thread safe.
 * */
fun <T> fastLazy(initializer: () -> T) = lazy(LazyThreadSafetyMode.NONE, initializer)