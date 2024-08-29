package com.a1573595.parkingdemo.common

sealed class AsyncValue<out T : Any> {
    data object Loading : AsyncValue<Nothing>()

    data class Error(val throwable: Throwable) : AsyncValue<Nothing>()

    data class Data<out T : Any>(val data: T) : AsyncValue<T>()

    val isLoading: Boolean
        get() = this is Loading

    val isError: Boolean
        get() = this is Error

    val isSuccess: Boolean
        get() = this is Data

    val error: Throwable?
        get() = (this as? Error)?.throwable

    val requireError: Throwable
        get() = (this as Error).throwable

    val value: T?
        get() = (this as? Data)?.data

    val requireValue: T
        get() = (this as Data).data
}