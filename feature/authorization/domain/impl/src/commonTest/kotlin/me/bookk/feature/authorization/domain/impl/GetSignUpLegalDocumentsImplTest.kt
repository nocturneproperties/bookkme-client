package me.bookk.feature.authorization.domain.impl

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
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.feature.authorization.domain.api.GetSignUpLegalDocuments
import me.bookk.feature.authorization.domain.datasource.SignUpLegalDocumentsDataSource
import me.bookk.feature.authorization.domain.entity.SignUpLegalDocument
import me.bookk.feature.authorization.domain.entity.SignUpLegalDocument.Document
import me.bookk.feature.authorization.domain.entity.SignUpLegalDocuments
import me.bookk.feature.settings.domain.api.GetLegalDocumentVersions
import me.bookk.feature.settings.domain.api.entity.LegalDocument
import me.bookk.feature.settings.domain.api.entity.LegalDocumentVersion
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.time.Instant

@OptIn(ExperimentalCoroutinesApi::class)
class GetSignUpLegalDocumentsImplTest {
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
        val getLegalDocumentVersions = mock<GetLegalDocumentVersions>()
        val dataSource = mock<SignUpLegalDocumentsDataSource> {
            everySuspend { saveDocuments(any()) } returns Unit
        }
        val documents = SignUpLegalDocuments(
            SignUpLegalDocument(
                Document.PRIVACY_POLICY,
                "privacy-v3",
                "https://example.com/privacy",
            ),
            SignUpLegalDocument(Document.TERMS_OF_SERVICE, "terms-v2", "https://example.com/terms"),
        )
        val sut = GetSignUpLegalDocumentsImpl(getLegalDocumentVersions, dataSource)
    }

    @Test
    fun `signup reads do not fetch remote versions`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.dataSource.getDocuments() } returns fixture.documents

        whenn()
        val result = fixture.sut()

        then()
        assertEquals(fixture.documents, result)
        verifySuspend(VerifyMode.not) { fixture.getLegalDocumentVersions() }
    }

    @Test
    fun `throws when no cached documents are available without fetching remotely`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.dataSource.getDocuments() } returns null

        whenn()
        val result = runCatching { fixture.sut() }

        then()
        assertFailsWith<GetSignUpLegalDocuments.Error.NotCached> { result.getOrThrow() }
        verifySuspend(VerifyMode.not) { fixture.getLegalDocumentVersions() }
    }

    @Test
    fun `returns published versions and links in authorization owned records`() = runUnitTest {
        given()
        val fixture = Fixture()
        val effectiveAt = Instant.parse("2026-11-01T00:00:00Z")
        everySuspend { fixture.getLegalDocumentVersions() } returns listOf(
            LegalDocumentVersion(
                LegalDocument.PRIVACY_POLICY,
                "privacy-v3",
                "https://example.com/privacy",
                effectiveAt,
            ),
            LegalDocumentVersion(
                LegalDocument.TERMS_OF_SERVICE,
                "terms-v2",
                "https://example.com/terms",
                effectiveAt,
            ),
        )

        whenn()
        val result = fixture.sut.refresh()

        then()
        assertEquals(
            SignUpLegalDocuments(
                privacyPolicy = SignUpLegalDocument(
                    Document.PRIVACY_POLICY,
                    "privacy-v3",
                    "https://example.com/privacy",
                ),
                termsOfService = SignUpLegalDocument(
                    Document.TERMS_OF_SERVICE,
                    "terms-v2",
                    "https://example.com/terms",
                ),
            ),
            result,
        )
        verifySuspend { fixture.dataSource.saveDocuments(result) }
    }

    @Test
    fun `throws when no documents are published`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.getLegalDocumentVersions() } returns emptyList()

        whenn()
        val result = runCatching { fixture.sut.refresh() }

        then()
        val error = assertFailsWith<GetSignUpLegalDocuments.Error.MissingDocument> {
            result.getOrThrow()
        }
        assertEquals(Document.PRIVACY_POLICY, error.document)
        verifySuspend(VerifyMode.not) { fixture.dataSource.saveDocuments(any()) }
    }

    @Test
    fun `throws when privacy policy is missing`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.getLegalDocumentVersions() } returns listOf(
            LegalDocumentVersion(
                LegalDocument.TERMS_OF_SERVICE,
                "terms-v2",
                "https://example.com/terms",
                Instant.parse("2026-11-01T00:00:00Z"),
            ),
        )

        whenn()
        val result = runCatching { fixture.sut.refresh() }

        then()
        val error = assertFailsWith<GetSignUpLegalDocuments.Error.MissingDocument> {
            result.getOrThrow()
        }
        assertEquals(Document.PRIVACY_POLICY, error.document)
        verifySuspend(VerifyMode.not) { fixture.dataSource.saveDocuments(any()) }
    }

    @Test
    fun `throws when terms of service are missing`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.getLegalDocumentVersions() } returns listOf(
            LegalDocumentVersion(
                LegalDocument.PRIVACY_POLICY,
                "privacy-v3",
                "https://example.com/privacy",
                Instant.parse("2026-11-01T00:00:00Z"),
            ),
        )

        whenn()
        val result = runCatching { fixture.sut.refresh() }

        then()
        val error = assertFailsWith<GetSignUpLegalDocuments.Error.MissingDocument> {
            result.getOrThrow()
        }
        assertEquals(Document.TERMS_OF_SERVICE, error.document)
        verifySuspend(VerifyMode.not) { fixture.dataSource.saveDocuments(any()) }
    }

    @Test
    fun `propagates loading failures instead of treating them as no documents`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.getLegalDocumentVersions() } throws IllegalStateException("offline")

        whenn()
        val result = runCatching { fixture.sut.refresh() }

        then()
        assertFailsWith<IllegalStateException> { result.getOrThrow() }
        verifySuspend(VerifyMode.not) { fixture.dataSource.saveDocuments(any()) }
    }
}
