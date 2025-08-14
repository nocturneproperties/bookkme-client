package me.bookk.shared;

import dev.icerock.moko.resources.desc.desc
import me.bookk.core.LogFactory
import me.bookk.core.domain.entity.Error
import me.bookk.core.presentation.error.ButtonDescriptor
import me.bookk.core.presentation.error.ErrorMapper
import me.bookk.core.presentation.error.PresentationNotification
import me.bookk.designsystem.resources.DesignSystem

class ErrorMapperImpl : ErrorMapper {
    private val logger = LogFactory.createLogger("ErrorMapper")
    override fun mapToNotification(e: Throwable): PresentationNotification {
        if (BuildKonfig.DEBUG) {
            logger.e(e)
        }
        return when (e) {
            is Error -> when (e) {
                is Error.UnknownApiError,
                is Error.BadRequest,
                is Error.InternalServerError -> PresentationNotification.Message(
                    message = DesignSystem.strings.error_server.desc(),
                    buttons = listOf(ButtonDescriptor(text = DesignSystem.strings.action_ok.desc()))
                )
                is Error.NoConnectionError -> PresentationNotification.Message(
                    message = DesignSystem.strings.error_no_internet.desc(),
                    buttons = listOf(ButtonDescriptor(text = DesignSystem.strings.action_ok.desc()))
                )
                is Error.WrappedError -> PresentationNotification.Message(
                    message = e.message.orEmpty().desc(),
                    buttons = listOf(ButtonDescriptor(text = DesignSystem.strings.action_ok.desc()))
                )
                is Error.BusinessError -> PresentationNotification.Message(
                    message = e.message.orEmpty().desc(),
                    buttons = listOf(ButtonDescriptor(text = DesignSystem.strings.action_ok.desc()))
                )
                is Error.Unknown -> PresentationNotification.Message(
                    message = DesignSystem.strings.error_unexpected.desc(),
                    buttons = listOf(ButtonDescriptor(text = DesignSystem.strings.action_ok.desc()))
                )
                is Error.Cancelled,
                is Error.Ignore -> PresentationNotification.Ignore
                is Error.Unauthorized -> PresentationNotification.Unauthorized
            }

            else -> PresentationNotification.Message(
                message = DesignSystem.strings.error_unexpected.desc(),
                buttons = listOf(ButtonDescriptor(text = DesignSystem.strings.action_ok.desc()))
            )
        }
    }
}