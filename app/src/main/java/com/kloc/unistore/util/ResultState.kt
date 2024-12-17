package com.kloc.unistore.util

sealed class ResultState<out T> {
    object Loading: ResultState<Nothing>()

    data class Success<out T>(
        val data: T
    ): ResultState<T>()

    data class Failure(
        val e: Exception
    ): ResultState<Nothing>()
}