package me.bookk.feature.settings.domain.impl

import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import dev.mokkery.verify.VerifyMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import kotlin.test.Test
import kotlin.test.BeforeTest
import kotlin.test.AfterTest
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.uuid.Uuid
import me.bookk.feature.settings.domain.api.StartPersonalDataExport
import me.bookk.feature.settings.domain.datasource.DataExportDataSource
import me.bookk.core.domain.entity.DataExport
import me.bookk.core.domain.entity.DataExportStatus
import me.bookk.feature.authorization.domain.api.ConfirmSensitiveAction
import me.bookk.feature.authorization.domain.entity.SensitiveAction
import me.bookk.feature.authorization.domain.api.UserProfileCRUD
import me.bookk.feature.authorization.domain.entity.UserProfile

@OptIn(ExperimentalCoroutinesApi::class)
class StartPersonalDataExportImplTest {
    private val testDispatcher = UnconfinedTestDispatcher()
    @BeforeTest
    fun setUp() { Dispatchers.setMain(testDispatcher) }
    @AfterTest
    fun tearDown() { Dispatchers.resetMain() }

    private class Fixture {
        val id = Uuid.parse("00000000-0000-0000-0000-000000000001")
        val dataSource = mock<DataExportDataSource>()
        val confirmation = mock<ConfirmSensitiveAction>()
        val profile = mock<UserProfileCRUD>()
        val sut = StartPersonalDataExportImpl(dataSource, confirmation, profile)
        init {
            everySuspend { confirmation(any(), any(), any()) } returns "confirmed"
            everySuspend { profile.get() } returns UserProfile(id, "John", "Doe", "john@example.com")
        }
    }
    @Test
    fun `returns the backend start result unchanged`() = runUnitTest {
        given()
        val fixture = Fixture()
        val expected = DataExport(DataExportStatus.RUNNING)
        everySuspend { fixture.dataSource.start("confirmed") } returns expected

        whenn()
        val result = fixture.sut()

        then()
        assertEquals(expected, result)
        verifySuspend { fixture.confirmation(SensitiveAction.EXPORT_PERSONAL_DATA, Uuid.parse("00000000-0000-0000-0000-000000000000"), fixture.id) }
    }
    @Test
    fun `maps export error 100005 to ConfirmationRequired`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.dataSource.start("confirmed") } throws me.bookk.core.domain.entity.Error.BusinessError(100005, "rejected")

        whenn()
        val result = runCatching { fixture.sut() }

        then()
        assertFailsWith<StartPersonalDataExport.Error.ConfirmationRequired> { result.getOrThrow() }
    }
    @Test
    fun `maps export error 900002 to TooFrequent`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.dataSource.start("confirmed") } throws me.bookk.core.domain.entity.Error.BusinessError(900002, "rejected")

        whenn()
        val result = runCatching { fixture.sut() }

        then()
        assertFailsWith<StartPersonalDataExport.Error.TooFrequent> { result.getOrThrow() }
    }
    @Test
    fun `failed confirmation never starts an export`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.confirmation(any(), any(), any()) } throws ConfirmSensitiveAction.Error.VerificationFailed()

        whenn()
        val result = runCatching { fixture.sut() }

        then()
        assertFailsWith<StartPersonalDataExport.Error.ConfirmationFailed> { result.getOrThrow() }
        verifySuspend(VerifyMode.exactly(0)) { fixture.dataSource.start(any()) }
    }
    @Test
    fun `propagates unrelated failures`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.dataSource.start("confirmed") } throws IllegalStateException("offline")

        whenn()
        val result = runCatching { fixture.sut() }

        then()
        assertFailsWith<IllegalStateException> { result.getOrThrow() }
    }

}
