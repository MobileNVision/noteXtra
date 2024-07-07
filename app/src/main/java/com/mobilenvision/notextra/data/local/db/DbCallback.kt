package com.mobilenvision.notextra.data.local.db

interface DbCallback<T> {
    fun onSuccess(result: T)
    fun onError(error: Throwable)
}