package me.bookk.feature.authorization.domain.impl

import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import me.bookk.core.domain.entity.Error
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.feature.authorization.domain.api.StartTelegramActionConfirmation
import me.bookk.feature.authorization.domain.datasource.AuthErrorCodes
import me.bookk.feature.authorization.domain.datasource.authorization.AuthorizationDataSource
import me.bookk.feature.authorization.domain.datasource.authorization.TelegramActionConfirmationStart
import me.bookk.feature.authorization.domain.entity.SensitiveAction
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.uuid.Uuid

class StartTelegramActionConfirmationImplTest {
    private class Fixture {
        val userId = Uuid.parse("00000000-0000-0000-0000-000000000001")
        val dataSource = mock<AuthorizationDataSource> {
            everySuspend { startTelegramActionConfirmation(any()) } returns Unit
        }
        val sut = StartTelegramActionConfirmationImpl(dataSource)
    }

    @Test
    fun `asks the server to send the confirmation for the action and target`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        fixture.sut(SensitiveAction.EXPORT_PERSONAL_DATA, Uuid.NIL, fixture.userId)

        then()
        verifySuspend {
            fixture.dataSource.startTelegramActionConfirmation(
                TelegramActionConfirmationStart(
                    action = SensitiveAction.EXPORT_PERSONAL_DATA,
                    businessId = Uuid.NIL,
                    targetId = fixture.userId,
                ),
            )
        }
    }

    @Test
    fun `asks for write access when the bot cannot write to the person`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.dataSource.startTelegramActionConfirmation(any()) } throws
            Error.BusinessError(AuthErrorCodes.TELEGRAM_CHAT_UNREACHABLE, "msg")

        whenn()
        val result =
            runCatching { fixture.sut(SensitiveAction.DELETE_ACCOUNT, Uuid.NIL, fixture.userId) }

        then()
        assertFailsWith<StartTelegramActionConfirmation.Error.BotCannotWrite> {
            result.getOrThrow()
        }
    }

    @Test
    fun `reports a confirmation started less than a minute ago`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.dataSource.startTelegramActionConfirmation(any()) } throws
            Error.BusinessError(AuthErrorCodes.TELEGRAM_CONFIRMATION_TOO_FREQUENT, "msg")

        whenn()
        val result =
            runCatching { fixture.sut(SensitiveAction.DELETE_ACCOUNT, Uuid.NIL, fixture.userId) }

        then()
        assertFailsWith<StartTelegramActionConfirmation.Error.TooFrequent> { result.getOrThrow() }
    }

    @Test
    fun `passes other server errors on with their message`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.dataSource.startTelegramActionConfirmation(any()) } throws
            Error.BusinessError(UNAVAILABLE, "msg")

        whenn()
        val result =
            runCatching { fixture.sut(SensitiveAction.DELETE_ACCOUNT, Uuid.NIL, fixture.userId) }

        then()
        assertFailsWith<Error.BusinessError> { result.getOrThrow() }
    }

    private companion object {
        const val UNAVAILABLE = 25
    }
}
