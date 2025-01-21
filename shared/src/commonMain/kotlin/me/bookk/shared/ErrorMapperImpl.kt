package me.bookk.shared;

import dev.icerock.moko.resources.desc.desc
import me.bookk.core.domain.entity.Error
import me.bookk.core.presentation.error.ErrorMapper
import me.bookk.core.presentation.error.PresentationError

class ErrorMapperImpl : ErrorMapper {
    override fun mapToError(e: Throwable): PresentationError {
        return when (e) {
            is Error -> when (e) {
                is Error.UnknownApiError,
                is Error.BadRequest,
                is Error.Unauthorized,
                is Error.InternalServerError -> PresentationError.ServerError
                is Error.NoConnectionError -> PresentationError.NoConnection
                is Error.WrappedError -> PresentationError.Message(e.message.orEmpty().desc())
                is Error.BusinessError -> PresentationError.Message(e.message.orEmpty().desc())
                is Error.Cancelled,
                is Error.Ignore -> PresentationError.Ignore
            }

            else -> PresentationError.Unsupported
        }
    }
}