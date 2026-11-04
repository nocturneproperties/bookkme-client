package me.bookk.feature.business.domain.impl

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
import me.bookk.feature.authorization.domain.entity.ActionConfirmation
import me.bookk.feature.authorization.domain.entity.SensitiveAction
import me.bookk.feature.business.domain.api.DownloadBusinessDataExport
import me.bookk.feature.business.domain.datasource.DataExportDataSource
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.uuid.Uuid

@OptIn(ExperimentalCoroutinesApi::class)
class DownloadBusinessDataExportImplTest {
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
        val sut = DownloadBusinessDataExportImpl(dataSource)
        init {
            everySuspend { confirmation(any(), any(), any()) } returns
                ActionConfirmation("confirmed", "assertion")
        }
    }

    @Test
    fun `returns the backend download result unchanged`() = runUnitTest {
        given()
        val fixture = Fixture()
        val expected = "{\"records\":[]}"
        everySuspend { fixture.dataSource.download(fixture.id) } returns expected

        whenn()
        val result = fixture.sut(fixture.id)

        then()
        assertEquals(expected, result)
    }

    @Test
    fun `maps a missing download to NotReady`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.dataSource.download(fixture.id) } throws
            me.bookk.core.domain.entity.Error.UnknownApiError(
                "missing",
                IllegalStateException(),
                404,
            )

        whenn()
        val result = runCatching { fixture.sut(fixture.id) }

        then()
        assertFailsWith<DownloadBusinessDataExport.Error.NotReady> { result.getOrThrow() }
    }

    @Test
    fun `propagates unrelated failures`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.dataSource.download(fixture.id) } throws
            IllegalStateException("offline")

        whenn()
        val result = runCatching { fixture.sut(fixture.id) }

        then()
        assertFailsWith<IllegalStateException> { result.getOrThrow() }
    }
}
