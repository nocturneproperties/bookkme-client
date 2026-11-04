package me.bookk.feature.settings.domain.impl

import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.mock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.feature.settings.domain.api.entity.LegalDocument
import me.bookk.feature.settings.domain.api.entity.LegalDocumentVersion
import me.bookk.feature.settings.domain.datasource.LegalDocumentsDataSource
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.time.Instant

@OptIn(ExperimentalCoroutinesApi::class)
class GetLegalDocumentVersionsImplTest {
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
        val dataSource = mock<LegalDocumentsDataSource>()
        val sut = GetLegalDocumentVersionsImpl(dataSource)
    }

    @Test
    fun `returns the backend records including optional or disabled values`() = runUnitTest {
        given()
        val fixture = Fixture()
        val records =
            listOf(
                LegalDocumentVersion(
                    LegalDocument.PRIVACY_POLICY,
                    "2026-11",
                    "https://example.com/privacy",
                    Instant.parse("2026-11-01T00:00:00Z"),
                ),
            )
        everySuspend { fixture.dataSource.getVersions() } returns records

        whenn()
        val result = fixture.sut()

        then()
        assertEquals(records, result)
    }

    @Test
    fun `returns an empty list when there are no records`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.dataSource.getVersions() } returns emptyList()

        whenn()
        val result = fixture.sut()

        then()
        assertEquals(emptyList(), result)
    }

    @Test
    fun `propagates backend failures`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.dataSource.getVersions() } throws
            IllegalStateException("unavailable")

        whenn()
        val result = runCatching { fixture.sut() }

        then()
        assertFailsWith<IllegalStateException> { result.getOrThrow() }
    }
}
