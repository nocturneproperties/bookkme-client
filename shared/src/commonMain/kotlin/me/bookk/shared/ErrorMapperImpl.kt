package me.bookk.shared;

import me.bookk.core.domain.entity.Error
import me.bookk.core.presentation.error.ErrorMapper
import me.bookk.core.presentation.error.PresentationError

class ErrorMapperImpl : ErrorMapper {
    override fun mapToError(e: Throwable): PresentationError {
        return when (e) {
            is Error -> when (e) {
                is Error.ApiError,
                is Error.BadRequest,
                is Error.InternalServerError -> PresentationError.ServerError
                is Error.NoConnectionError -> PresentationError.NoConnection
                is Error.Unauthorized -> PresentationError.Unauthorized
                is Error.SimpleError -> PresentationError.Message(e.message)
                else -> PresentationError.Unsupported(e.message)
            }

            else -> PresentationError.Unsupported(e.message)
        }
    }
}