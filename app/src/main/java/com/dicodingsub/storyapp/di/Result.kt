package com.dicodingsub.storyapp.di

sealed class Result<out T> {
    data class Loading<out T>(val state: Boolean) : Result<T>()
    data class Success<out T>(val data: T) : Result<T>()
    data class Failure<out T>(val throwable: Throwable) : Result<T>()
}