package me.bookk.designsystem.test

import dev.icerock.moko.resources.desc.desc
import kotlinx.coroutines.CancellationException
import me.bookk.core.presentation.error.ErrorDescription
import me.bookk.core.presentation.error.ErrorMapper
import me.bookk.core.presentation.error.PresentationNotification

class FakeErrorMapper : ErrorMapper {
    val mappedErrors: MutableList<Throwable> = mutableListOf()

    override fun mapToNotification(e: Throwable): PresentationNotification {
        if (e is CancellationException) {
            return PresentationNotification.Ignore
        }
        mappedErrors += e
        return errorNotification(e)
    }

    override fun mapToDescription(e: Throwable): ErrorDescription {
        return ErrorDescription(
            title = "title of ${e::class.simpleName}".desc(),
            message = "message of ${e::class.simpleName}".desc()
        )
    }

    companion object {
        fun errorNotification(e: Throwable): PresentationNotification.GlobalMessage {
            return PresentationNotification.GlobalMessage(
                text = "mapped ${e::class.simpleName}".desc(),
                state = PresentationNotification.GlobalMessage.State.ERROR
            )
        }
    }
}
