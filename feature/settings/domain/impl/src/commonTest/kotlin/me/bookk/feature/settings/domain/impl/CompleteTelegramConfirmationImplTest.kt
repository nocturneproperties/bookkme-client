package me.bookk.feature.settings.domain.impl

import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.matcher.matches
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifySuspend
import me.bookk.core.domain.entity.DataExport
import me.bookk.core.domain.entity.DataExportStatus
import me.bookk.core.domain.entity.Error
import me.bookk.core.domain.launch.ConfirmableAction
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.feature.authorization.domain.api.ConfirmActionWithTelegram
import me.bookk.feature.authorization.domain.api.LogOut
import me.bookk.feature.authorization.domain.datasource.AuthErrorCodes
import me.bookk.feature.authorization.domain.datasource.authorization.AuthorizationDataSource
import me.bookk.feature.settings.domain.api.CompleteTelegramConfirmation
import me.bookk.feature.settings.domain.datasource.DataExportDataSource
import me.bookk.feature.settings.domain.datasource.SettingsErrorCodes
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertSame

class CompleteTelegramConfirmationImplTest {
    private class Fixture {
        val initData = "auth_date=1&start_param=op_delete-account_00&hash=00"
        val export = DataExport(DataExportStatus.RUNNING)
        val confirmWithTelegram = mock<ConfirmActionWithTelegram> {
            everySuspend { invoke(any()) } returns "single-use"
        }
        val authorizationDataSource = mock<AuthorizationDataSource> {
            everySuspend { deleteAccount(any()) } returns Unit
            everySuspend { saveAuthorizationTokens(null) } returns Unit
            everySuspend { setAuthorizationStatus(false) } returns Unit
        }
        val logOut = mock<LogOut> {
            everySuspend { invoke() } returns Unit
        }
        val dataExportDataSource = mock<DataExportDataSource> {
            everySuspend { start(any()) } returns export
            everySuspend { deleteFiles() } returns Unit
            everySuspend { saveInDb(any()) } returns Unit
        }
        val sut = CompleteTelegramConfirmationImpl(
            confirmWithTelegram = confirmWithTelegram,
            authorizationDataSource = authorizationDataSource,
            logOut = logOut,
            dataExportDataSource = dataExportDataSource,
        )
    }

    @Test
    fun `deletes the account with the token telegram confirmed`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        fixture.sut(ConfirmableAction.DELETE_ACCOUNT, fixture.initData)

        then()
        verifySuspend(VerifyMode.order) {
            fixture.confirmWithTelegram(fixture.initData)
            fixture.authorizationDataSource.deleteAccount(
                matches({ "confirmed token" }) { it.confirmationToken == "single-use" },
            )
        }
    }

    @Test
    fun `ends the session after the account is deleted`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        fixture.sut(ConfirmableAction.DELETE_ACCOUNT, fixture.initData)

        then()
        verifySuspend(VerifyMode.order) {
            fixture.authorizationDataSource.deleteAccount(any())
            fixture.logOut()
            fixture.authorizationDataSource.saveAuthorizationTokens(null)
            fixture.authorizationDataSource.setAuthorizationStatus(false)
        }
    }

    @Test
    fun `starts the export with the token telegram confirmed`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        fixture.sut(ConfirmableAction.EXPORT_PERSONAL_DATA, fixture.initData)

        then()
        verifySuspend(VerifyMode.order) {
            fixture.confirmWithTelegram(fixture.initData)
            fixture.dataExportDataSource.start("single-use")
        }
        verifySuspend(VerifyMode.not) { fixture.authorizationDataSource.deleteAccount(any()) }
    }

    @Test
    fun `saves the started export so its preparing state shows`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        fixture.sut(ConfirmableAction.EXPORT_PERSONAL_DATA, fixture.initData)

        then()
        verifySuspend { fixture.dataExportDataSource.deleteFiles() }
        verifySuspend { fixture.dataExportDataSource.saveInDb(fixture.export) }
    }

    @Test
    fun `reports an expired or used confirmation`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.confirmWithTelegram(any()) } throws
            ConfirmActionWithTelegram.Error.ConfirmationExpired()

        whenn()
        val result = runCatching { fixture.sut(ConfirmableAction.DELETE_ACCOUNT, fixture.initData) }

        then()
        assertFailsWith<CompleteTelegramConfirmation.Error.ConfirmationExpired> {
            result.getOrThrow()
        }
        verifySuspend(VerifyMode.not) { fixture.authorizationDataSource.deleteAccount(any()) }
        verifySuspend(VerifyMode.not) { fixture.logOut() }
    }

    @Test
    fun `reports a confirmation requested from another telegram account`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.confirmWithTelegram(any()) } throws
            ConfirmActionWithTelegram.Error.TelegramAccountMismatch()

        whenn()
        val result = runCatching {
            fixture.sut(ConfirmableAction.EXPORT_PERSONAL_DATA, fixture.initData)
        }

        then()
        assertFailsWith<CompleteTelegramConfirmation.Error.TelegramAccountMismatch> {
            result.getOrThrow()
        }
        verifySuspend(VerifyMode.not) { fixture.dataExportDataSource.start(any()) }
    }

    @Test
    fun `reports invalid or expired telegram data`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.confirmWithTelegram(any()) } throws
            ConfirmActionWithTelegram.Error.TelegramDataInvalid()

        whenn()
        val result = runCatching { fixture.sut(ConfirmableAction.DELETE_ACCOUNT, fixture.initData) }

        then()
        assertFailsWith<CompleteTelegramConfirmation.Error.TelegramDataInvalid> {
            result.getOrThrow()
        }
    }

    @Test
    fun `reports a token the deletion rejects as an expired confirmation`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.authorizationDataSource.deleteAccount(any()) } throws
            Error.BusinessError(AuthErrorCodes.ACTION_CONFIRMATION_INVALID, "invalid")

        whenn()
        val result = runCatching { fixture.sut(ConfirmableAction.DELETE_ACCOUNT, fixture.initData) }

        then()
        assertFailsWith<CompleteTelegramConfirmation.Error.ConfirmationExpired> {
            result.getOrThrow()
        }
        verifySuspend(VerifyMode.not) { fixture.logOut() }
    }

    @Test
    fun `reports a token the export rejects as an expired confirmation`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.dataExportDataSource.start(any()) } throws
            Error.BusinessError(SettingsErrorCodes.USER_ACTION_CONFIRMATION_INVALID, "invalid")

        whenn()
        val result = runCatching {
            fixture.sut(ConfirmableAction.EXPORT_PERSONAL_DATA, fixture.initData)
        }

        then()
        assertFailsWith<CompleteTelegramConfirmation.Error.ConfirmationExpired> {
            result.getOrThrow()
        }
    }

    @Test
    fun `reports an export already made today`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.dataExportDataSource.start(any()) } throws
            Error.BusinessError(SettingsErrorCodes.DATA_EXPORT_TOO_FREQUENT, "too frequent")

        whenn()
        val result = runCatching {
            fixture.sut(ConfirmableAction.EXPORT_PERSONAL_DATA, fixture.initData)
        }

        then()
        assertFailsWith<CompleteTelegramConfirmation.Error.ExportTooFrequent> {
            result.getOrThrow()
        }
        verifySuspend(VerifyMode.not) { fixture.dataExportDataSource.saveInDb(any()) }
    }

    @Test
    fun `rethrows other failures`() = runUnitTest {
        given()
        val fixture = Fixture()
        val failure = IllegalStateException("offline")
        everySuspend { fixture.authorizationDataSource.deleteAccount(any()) } throws failure

        whenn()
        val result = runCatching { fixture.sut(ConfirmableAction.DELETE_ACCOUNT, fixture.initData) }

        then()
        assertSame(failure, result.exceptionOrNull())
    }
}
