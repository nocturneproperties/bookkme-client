package me.bookk.core.domain.usecase

interface AsyncUseCase<in Param, out Result> {
    suspend fun call(params: Param): Result
}