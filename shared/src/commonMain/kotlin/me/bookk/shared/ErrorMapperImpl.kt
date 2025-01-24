package me.bookk.shared;

import dev.icerock.moko.resources.desc.desc
import me.bookk.core.domain.entity.Error
import me.bookk.core.presentation.error.ErrorMapper
import me.bookk.core.presentation.error.PresentationError
import me.bookk.designsystem.resources.DesignSystem

class ErrorMapperImpl : ErrorMapper {
    override fun mapToError(e: Throwable): PresentationError {
        return when (e) {
            is Error -> when (e) {
                is Error.UnknownApiError,
                is Error.BadRequest,
                is Error.Unauthorized,
                is Error.InternalServerError -> PresentationError.Message(
                    message = DesignSystem.strings.error_server.desc(),
                    buttonText = DesignSystem.strings.action_ok.desc()
                )
                is Error.NoConnectionError -> PresentationError.Message(
                    message = DesignSystem.strings.error_no_internet.desc(),
                    buttonText = DesignSystem.strings.action_ok.desc()
                )
                is Error.WrappedError -> PresentationError.Message(
                    message = e.message.orEmpty().desc(),
                    buttonText = DesignSystem.strings.action_ok.desc()
                )
                is Error.BusinessError -> PresentationError.Message(
                    message = e.message.orEmpty().desc(),
                    buttonText = DesignSystem.strings.action_ok.desc()
                )
                is Error.Cancelled,
                is Error.Ignore -> PresentationError.Ignore
            }

            else -> PresentationError.Message(
                message = DesignSystem.strings.error_unexpected.desc(),
                buttonText = DesignSystem.strings.action_ok.desc()
            )
        }
    }
}