package me.bookk.feature.settings.domain.impl

import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.feature.authorization.domain.api.StartTelegramActionConfirmation
import me.bookk.feature.authorization.domain.api.UserProfileCRUD
import me.bookk.feature.authorization.domain.entity.SensitiveAction
import me.bookk.feature.authorization.domain.entity.UserProfile
import me.bookk.feature.settings.domain.api.entity.ActionOutcome
import me.bookk.feature.settings.domain.api.entity.ChatConfirmationError
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertSame
import kotlin.uuid.Uuid

class TelegramConfirmationSenderTest {
    private class Fixture {
        val userId = Uuid.parse("00000000-0000-0000-0000-000000000001")
        val startConfirmation = mock<StartTelegramActionConfirmation> {
            everySuspend { invoke(any(), any(), any()) } returns Unit
        }
        val profile = mock<UserProfileCRUD> {
            everySuspend { get() } returns UserProfile(userId, "Jane", "Doe", "jane@example.com")
        }
        val sut = TelegramConfirmationSender(startConfirmation, profile)
    }

    @Test
    fun `asks for a confirmation of the caller's own action`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        fixture.sut.send(SensitiveAction.DELETE_ACCOUNT)

        then()
        verifySuspend {
            fixture.startConfirmation(SensitiveAction.DELETE_ACCOUNT, Uuid.NIL, fixture.userId)
        }
    }

    @Test
    fun `reports the confirmation as sent`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val result = fixture.sut.send(SensitiveAction.EXPORT_PERSONAL_DATA)

        then()
        assertEquals(ActionOutcome.ConfirmationSent, result)
    }

    @Test
    fun `asks for write access when the bot cannot write to the person`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.startConfirmation(any(), any(), any()) } throws
            StartTelegramActionConfirmation.Error.BotCannotWrite()

        whenn()
        val result = runCatching { fixture.sut.send(SensitiveAction.DELETE_ACCOUNT) }

        then()
        assertFailsWith<ChatConfirmationError.WriteAccessRequired> { result.getOrThrow() }
    }

    @Test
    fun `reports a confirmation sent too recently`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.startConfirmation(any(), any(), any()) } throws
            StartTelegramActionConfirmation.Error.TooFrequent()

        whenn()
        val result = runCatching { fixture.sut.send(SensitiveAction.DELETE_ACCOUNT) }

        then()
        assertFailsWith<ChatConfirmationError.TooFrequent> { result.getOrThrow() }
    }

    @Test
    fun `rethrows other failures`() = runUnitTest {
        given()
        val fixture = Fixture()
        val failure = IllegalStateException("offline")
        everySuspend { fixture.startConfirmation(any(), any(), any()) } throws failure

        whenn()
        val result = runCatching { fixture.sut.send(SensitiveAction.DELETE_ACCOUNT) }

        then()
        assertSame(failure, result.exceptionOrNull())
    }
}
