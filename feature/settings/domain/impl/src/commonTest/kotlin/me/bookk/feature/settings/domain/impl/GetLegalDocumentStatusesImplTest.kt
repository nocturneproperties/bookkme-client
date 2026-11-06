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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.feature.settings.domain.api.entity.LegalDocument
import me.bookk.feature.settings.domain.api.entity.LegalDocumentStatus
import me.bookk.feature.settings.domain.api.entity.LegalDocumentVersion
import me.bookk.feature.settings.domain.datasource.LegalDocumentsDataSource
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.time.Instant

@OptIn(ExperimentalCoroutinesApi::class)
class GetLegalDocumentStatusesImplTest {
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
        val dataSource = mock<LegalDocumentsDataSource> {
            everySuspend { saveStatuses(any()) } returns Unit
        }
        val sut = GetLegalDocumentStatusesImpl(dataSource)
    }

    @Test
    fun `emits a cached empty snapshot without fetching remotely`() = runUnitTest {
        given()
        val fixture = Fixture()
        every { fixture.dataSource.observeStatuses() } returns flowOf(emptyList())

        whenn()
        val result = fixture.sut.flow().first()

        then()
        assertEquals(emptyList(), result)
        verifySuspend(VerifyMode.exactly(0)) { fixture.dataSource.getStatuses() }
    }

    @Test
    fun `emits cached status changes`() = runUnitTest {
        given()
        val fixture = Fixture()
        val status = LegalDocumentStatus(
            LegalDocumentVersion(
                LegalDocument.PRIVACY_POLICY,
                "v1",
                "https://example.com/privacy",
                Instant.parse("2026-11-01T00:00:00Z"),
            ),
            null,
        )
        val accepted = status.copy(acceptedAt = Instant.parse("2026-11-02T00:00:00Z"))
        every { fixture.dataSource.observeStatuses() } returns
            flowOf(listOf(status), listOf(accepted), emptyList())

        whenn()
        val result = fixture.sut.flow().toList()

        then()
        assertEquals(listOf(listOf(status), listOf(accepted), emptyList()), result)
    }

    @Test
    fun `propagates cache write failures`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.dataSource.getStatuses() } returns emptyList()
        everySuspend { fixture.dataSource.saveStatuses(any()) } throws IllegalStateException("disk")

        whenn()
        val result = runCatching { fixture.sut.refresh() }

        then()
        assertFailsWith<IllegalStateException> { result.getOrThrow() }
    }

    @Test
    fun `returns and caches records with pending acceptance`() = runUnitTest {
        given()
        val fixture = Fixture()
        val records =
            listOf(
                LegalDocumentStatus(
                    LegalDocumentVersion(
                        LegalDocument.TERMS_OF_SERVICE,
                        "2026-11",
                        "https://example.com/terms",
                        Instant.parse("2026-11-01T00:00:00Z"),
                    ),
                    null,
                ),
            )
        everySuspend { fixture.dataSource.getStatuses() } returns records

        whenn()
        val result = fixture.sut.refresh()

        then()
        assertEquals(records, result)
        verifySuspend { fixture.dataSource.saveStatuses(records) }
    }

    @Test
    fun `returns an empty list when there are no records`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.dataSource.getStatuses() } returns emptyList()

        whenn()
        val result = fixture.sut.refresh()

        then()
        assertEquals(emptyList(), result)
        verifySuspend { fixture.dataSource.saveStatuses(emptyList()) }
    }

    @Test
    fun `propagates backend failures`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.dataSource.getStatuses() } throws
            IllegalStateException("unavailable")

        whenn()
        val result = runCatching { fixture.sut.refresh() }

        then()
        assertFailsWith<IllegalStateException> { result.getOrThrow() }
        verifySuspend(VerifyMode.exactly(0)) { fixture.dataSource.saveStatuses(any()) }
    }
}
