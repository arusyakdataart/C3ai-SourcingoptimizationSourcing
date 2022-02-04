package com.c3ai.sourcingoptimization.data

/**
 * A generic class that holds a value or an exception
 */
sealed class C3Result<out R> {
    data class Success<out T>(val data: T) : C3Result<T>()
    data class Error(val exception: Exception) : C3Result<Nothing>()

    companion object {
        inline fun <T> on(f: () -> T): C3Result<T> = try {
            Success(f())
        } catch (ex: Exception) {
            ex.printStackTrace()
            Error(ex)
        }
    }
}

fun <T> C3Result<T>.successOr(fallback: T): T {
    return (this as? C3Result.Success<T>)?.data ?: fallback
}
