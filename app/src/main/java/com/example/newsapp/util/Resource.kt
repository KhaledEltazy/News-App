package com.example.newsapp.util

sealed class Resource<T>(
    val data : T? = null,
    val massage : String? = null
) {
    class Success<T>(data : T) : Resource<T>(data)
    class Error<T>(masssage : String, data : T? = null) : Resource<T>(data,masssage)
    class Loading<T> : Resource<T>()
}