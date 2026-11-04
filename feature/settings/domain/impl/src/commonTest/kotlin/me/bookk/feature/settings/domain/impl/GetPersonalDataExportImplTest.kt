package me.bookk.feature.settings.domain.impl

import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifySuspend
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import me.bookk.core.domain.entity.DataExport
import me.bookk.core.domain.entity.DataExportStatus
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.feature.authorization.domain.api.ConfirmSensitiveAction
import me.bookk.feature.authorization.domain.api.UserProfileCRUD
import me.bookk.feature.authorization.domain.entity.SensitiveAction
import me.bookk.feature.authorization.domain.entity.UserProfile
import me.bookk.feature.settings.domain.api.GetPersonalDataExport
import me.bookk.feature.settings.domain.datasource.DataExportDataSource
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.uuid.Uuid

@OptIn(ExperimentalCoroutinesApi::class)
class GetPersonalDataExportImplTest {
    private val testDispatcher = UnconfinedTestDispatcher()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private class Fixture {
        val id = Uuid.parse("00000000-0000-0000-0000-000000000001")
        val dataSource = mock<DataExportDataSource>()
        val confirmation = mock<ConfirmSensitiveAction>()
        val profile = mock<UserProfileCRUD>()
        val sut = GetPersonalDataExportImpl(dataSource)
        init {
            everySuspend { confirmation(any(), any(), any()) } returns "confirmed"
            everySuspend { profile.get() } returns
                UserProfile(id, "John", "Doe", "john@example.com")
        }
    }

    @Test
    fun `returns the backend get result unchanged`() = runUnitTest {
        given()
        val fixture = Fixture()
        val expected = DataExport(DataExportStatus.RUNNING)
        everySuspend { fixture.dataSource.get() } returns expected

        whenn()
        val result = fixture.sut()

        then()
        assertEquals(expected, result)
    }

    @Test
    fun `propagates unrelated failures`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.dataSource.get() } throws IllegalStateException("offline")

        whenn()
        val result = runCatching { fixture.sut() }

        then()
        assertFailsWith<IllegalStateException> { result.getOrThrow() }
    }
}
