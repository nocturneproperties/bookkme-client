package me.bookk.feature.business.domain.impl

import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifySuspend
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import me.bookk.core.domain.entity.DataExport
import me.bookk.core.domain.entity.DataExportStatus
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.feature.business.domain.api.DownloadBusinessDataExport
import me.bookk.feature.business.domain.api.GetBusinessDataExport
import me.bookk.feature.business.domain.datasource.DataExportDataSource
import okio.Path.Companion.toPath
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.time.Instant
import kotlin.uuid.Uuid
import me.bookk.core.domain.entity.Error as DomainError

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
        val ready = DataExport(
            DataExportStatus.READY,
            requestedAt = Instant.parse("2026-11-05T12:00:00Z"),
            expiresAt = Instant.parse("2026-11-08T12:00:00Z"),
        )
        val file = "/cache/business_data_exports/export.json".toPath()
        val export = MutableStateFlow<DataExport?>(ready)
        val getBusinessDataExport = mock<GetBusinessDataExport> {
            every { flow(id) } returns export
        }
        val dataSource = mock<DataExportDataSource> {
            everySuspend { getFile(any(), any()) } returns null
            everySuspend { download(id) } returns CONTENT
            everySuspend { saveFile(any(), any(), any()) } returns file
        }
        val sut = DownloadBusinessDataExportImpl(getBusinessDataExport, dataSource)
    }

    @Test
    fun `returns the saved file without downloading again`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.dataSource.getFile(fixture.id, fixture.ready) } returns fixture.file

        whenn()
        val result = fixture.sut(fixture.id)

        then()
        assertEquals(fixture.file, result)
        verifySuspend(VerifyMode.exactly(0)) { fixture.dataSource.download(any()) }
    }

    @Test
    fun `downloads and saves the export when no file is saved`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val result = fixture.sut(fixture.id)

        then()
        assertEquals(fixture.file, result)
        verifySuspend { fixture.dataSource.saveFile(fixture.id, fixture.ready, CONTENT) }
    }

    @Test
    fun `missing export is not ready`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.export.value = null

        whenn()
        val result = runCatching { fixture.sut(fixture.id) }

        then()
        assertFailsWith<DownloadBusinessDataExport.Error.NotReady> { result.getOrThrow() }
        verifySuspend(VerifyMode.exactly(0)) { fixture.dataSource.download(any()) }
    }

    @Test
    fun `exports that are not ready cannot be downloaded`() = runUnitTest {
        given()
        val fixtures = DataExportStatus.entries
            .filter { it != DataExportStatus.READY }
            .map { status -> Fixture().also { it.export.value = DataExport(status) } }

        whenn()
        val results = fixtures.map { runCatching { it.sut(it.id) } }

        then()
        results.forEach {
            assertFailsWith<DownloadBusinessDataExport.Error.NotReady> { it.getOrThrow() }
        }
        fixtures.forEach {
            verifySuspend(VerifyMode.exactly(0)) { it.dataSource.download(any()) }
        }
    }

    @Test
    fun `maps a missing backend export to not ready`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.dataSource.download(fixture.id) } throws
            DomainError.NotFound(IllegalStateException("404"))

        whenn()
        val result = runCatching { fixture.sut(fixture.id) }

        then()
        assertFailsWith<DownloadBusinessDataExport.Error.NotReady> { result.getOrThrow() }
        verifySuspend(VerifyMode.exactly(0)) { fixture.dataSource.saveFile(any(), any(), any()) }
    }

    @Test
    fun `propagates unrelated download failures without saving`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.dataSource.download(fixture.id) } throws
            IllegalStateException("offline")

        whenn()
        val result = runCatching { fixture.sut(fixture.id) }

        then()
        assertFailsWith<IllegalStateException> { result.getOrThrow() }
        verifySuspend(VerifyMode.exactly(0)) { fixture.dataSource.saveFile(any(), any(), any()) }
    }

    @Test
    fun `propagates file write failures`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.dataSource.saveFile(any(), any(), any()) } throws
            IllegalStateException("disk")

        whenn()
        val result = runCatching { fixture.sut(fixture.id) }

        then()
        assertFailsWith<IllegalStateException> { result.getOrThrow() }
    }

    private companion object {
        const val CONTENT = "{\"records\":[]}"
    }
}
