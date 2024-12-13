package me.bookk.core.domain.usecase

interface BlockingUseCase<in Param, Result> {
	fun call(params: Param): Result
}