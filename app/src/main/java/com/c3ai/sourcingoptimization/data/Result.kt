package com.c3ai.sourcingoptimization.data

/**
 * A generic class that holds a value or an exception
 */
sealed class Result<out R> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()

    companion object {
        inline fun <T> on(f: () -> T): Result<T> = try {
            Success(f())
        } catch (ex: Exception) {
            Error(ex)
        }
    }
}

fun <T> Result<T>.successOr(fallback: T): T {
    return (this as? Result.Success<T>)?.data ?: fallback
}
