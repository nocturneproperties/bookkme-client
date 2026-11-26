package me.bookk.feature.authorization.domain.impl

import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import me.bookk.core.domain.entity.Error
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.feature.authorization.domain.api.ConfirmActionWithTelegram
import me.bookk.feature.authorization.domain.datasource.AuthErrorCodes
import me.bookk.feature.authorization.domain.datasource.authorization.AuthorizationDataSource
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ConfirmActionWithTelegramImplTest {
    private class Fixture {
        val initData = "auth_date=1&start_param=op_delete-account_00&hash=00"
        val dataSource = mock<AuthorizationDataSource>()
        val sut = ConfirmActionWithTelegramImpl(dataSource)
    }

    @Test
    fun `returns the confirmation token for the signed launch data`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.dataSource.confirmActionWithTelegram(fixture.initData) } returns
            "single-use"

        whenn()
        val token = fixture.sut(fixture.initData)

        then()
        assertEquals("single-use", token)
    }

    @Test
    fun `reports an expired or already used confirmation`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.dataSource.confirmActionWithTelegram(any()) } throws
            Error.BusinessError(AuthErrorCodes.TELEGRAM_CONFIRMATION_EXPIRED, "msg")

        whenn()
        val result = runCatching { fixture.sut(fixture.initData) }

        then()
        assertFailsWith<ConfirmActionWithTelegram.Error.ConfirmationExpired> { result.getOrThrow() }
    }

    @Test
    fun `reports a confirmation opened from another telegram account`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.dataSource.confirmActionWithTelegram(any()) } throws
            Error.BusinessError(AuthErrorCodes.TELEGRAM_ACCOUNT_MISMATCH, "msg")

        whenn()
        val result = runCatching { fixture.sut(fixture.initData) }

        then()
        assertFailsWith<ConfirmActionWithTelegram.Error.TelegramAccountMismatch> {
            result.getOrThrow()
        }
    }

    @Test
    fun `reports telegram data that is not signed for this bot`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.dataSource.confirmActionWithTelegram(any()) } throws
            Error.BusinessError(AuthErrorCodes.TELEGRAM_DATA_INVALID, "msg")

        whenn()
        val result = runCatching { fixture.sut(fixture.initData) }

        then()
        assertFailsWith<ConfirmActionWithTelegram.Error.TelegramDataInvalid> { result.getOrThrow() }
    }

    @Test
    fun `reports telegram data that is too old`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.dataSource.confirmActionWithTelegram(any()) } throws
            Error.BusinessError(AuthErrorCodes.TELEGRAM_DATA_EXPIRED, "msg")

        whenn()
        val result = runCatching { fixture.sut(fixture.initData) }

        then()
        assertFailsWith<ConfirmActionWithTelegram.Error.TelegramDataInvalid> { result.getOrThrow() }
    }

    @Test
    fun `passes other server errors on with their message`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.dataSource.confirmActionWithTelegram(any()) } throws
            Error.BusinessError(UNAVAILABLE, "msg")

        whenn()
        val result = runCatching { fixture.sut(fixture.initData) }

        then()
        assertFailsWith<Error.BusinessError> { result.getOrThrow() }
    }

    private companion object {
        const val UNAVAILABLE = 25
    }
}
