package me.bookk.feature.settings.domain.impl

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
import me.bookk.feature.settings.domain.api.DownloadPersonalDataExport
import me.bookk.feature.settings.domain.api.GetPersonalDataExport
import me.bookk.feature.settings.domain.datasource.DataExportDataSource
import okio.Path.Companion.toPath
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.time.Instant
import me.bookk.core.domain.entity.Error as DomainError

@OptIn(ExperimentalCoroutinesApi::class)
class DownloadPersonalDataExportImplTest {
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
        val ready = DataExport(
            DataExportStatus.READY,
            requestedAt = Instant.parse("2026-11-05T12:00:00Z"),
            expiresAt = Instant.parse("2026-11-08T12:00:00Z"),
        )
        val file = "/cache/personal_data_exports/export.json".toPath()
        val export = MutableStateFlow<DataExport?>(ready)
        val getPersonalDataExport = mock<GetPersonalDataExport> {
            every { flow() } returns export
        }
        val dataSource = mock<DataExportDataSource> {
            everySuspend { getFile(any()) } returns null
            everySuspend { download() } returns CONTENT
            everySuspend { saveFile(any(), any()) } returns file
        }
        val sut = DownloadPersonalDataExportImpl(getPersonalDataExport, dataSource)
    }

    @Test
    fun `returns the saved file without downloading again`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.dataSource.getFile(fixture.ready) } returns fixture.file

        whenn()
        val result = fixture.sut()

        then()
        assertEquals(fixture.file, result)
        verifySuspend(VerifyMode.exactly(0)) { fixture.dataSource.download() }
    }

    @Test
    fun `downloads and saves the export when no file is saved`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val result = fixture.sut()

        then()
        assertEquals(fixture.file, result)
        verifySuspend { fixture.dataSource.saveFile(fixture.ready, CONTENT) }
    }

    @Test
    fun `missing export is not ready`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.export.value = null

        whenn()
        val result = runCatching { fixture.sut() }

        then()
        assertFailsWith<DownloadPersonalDataExport.Error.NotReady> { result.getOrThrow() }
        verifySuspend(VerifyMode.exactly(0)) { fixture.dataSource.download() }
    }

    @Test
    fun `exports that are not ready cannot be downloaded`() = runUnitTest {
        given()
        val fixtures = DataExportStatus.entries
            .filter { it != DataExportStatus.READY }
            .map { status -> Fixture().also { it.export.value = DataExport(status) } }

        whenn()
        val results = fixtures.map { runCatching { it.sut() } }

        then()
        results.forEach {
            assertFailsWith<DownloadPersonalDataExport.Error.NotReady> { it.getOrThrow() }
        }
        fixtures.forEach {
            verifySuspend(VerifyMode.exactly(0)) { it.dataSource.download() }
        }
    }

    @Test
    fun `maps a missing backend export to not ready`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.dataSource.download() } throws
            DomainError.NotFound(IllegalStateException("404"))

        whenn()
        val result = runCatching { fixture.sut() }

        then()
        assertFailsWith<DownloadPersonalDataExport.Error.NotReady> { result.getOrThrow() }
        verifySuspend(VerifyMode.exactly(0)) { fixture.dataSource.saveFile(any(), any()) }
    }

    @Test
    fun `propagates unrelated download failures without saving`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.dataSource.download() } throws IllegalStateException("offline")

        whenn()
        val result = runCatching { fixture.sut() }

        then()
        assertFailsWith<IllegalStateException> { result.getOrThrow() }
        verifySuspend(VerifyMode.exactly(0)) { fixture.dataSource.saveFile(any(), any()) }
    }

    @Test
    fun `propagates file write failures`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.dataSource.saveFile(any(), any()) } throws
            IllegalStateException("disk")

        whenn()
        val result = runCatching { fixture.sut() }

        then()
        assertFailsWith<IllegalStateException> { result.getOrThrow() }
    }

    private companion object {
        const val CONTENT = "{\"records\":[]}"
    }
}
