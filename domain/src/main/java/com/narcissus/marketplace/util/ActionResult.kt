package com.narcissus.marketplace.util

typealias Mapper<Input, Output> = (Input) -> Output
sealed class QueryResult<T> {
    class SuccessResult<T>(val data: T) : QueryResult<T>()
    class ErrorResult<T>(val message: String) : QueryResult<T>()

    fun <R> mapResult(mapper: Mapper<T, R>) = when (this) {
        is SuccessResult -> SuccessResult(mapper(this.data))
        is ErrorResult -> ErrorResult(this.message)
    }

}