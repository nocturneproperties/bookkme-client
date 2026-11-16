package me.bookk.site.legal

import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.site.designsystem.resources.AppLanguage
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class LegalDocumentHistoryTest {

    @Test
    fun `keeps only the texts of its own document`() = runUnitTest {
        given()
        val texts = stubPublishedTexts(LegalDocument.PRIVACY_POLICY, FIRST_VERSION) +
            stubPublishedTexts(LegalDocument.DATA_PROCESSING_AGREEMENT, SECOND_VERSION)

        whenn()
        val history = LegalDocumentHistory.from(LegalDocument.DATA_PROCESSING_AGREEMENT, texts)

        then()
        assertEquals(listOf(SECOND_VERSION), history.versions.map { it.version })
    }

    @Test
    fun `groups the languages of a version`() = runUnitTest {
        given()
        val texts = stubPublishedTexts(version = FIRST_VERSION)

        whenn()
        val history = LegalDocumentHistory.from(LegalDocument.PRIVACY_POLICY, texts)

        then()
        val version = history.versions.single()
        assertEquals("# pl $FIRST_VERSION", version.text(AppLanguage.PL))
        assertEquals(instant("2026-12-01T00:00:00Z"), version.effectiveAt)
    }

    @Test
    fun `orders versions newest first`() = runUnitTest {
        given()
        val texts =
            stubPublishedTexts(version = FIRST_VERSION) +
                stubPublishedTexts(version = SECOND_VERSION)

        whenn()
        val history = LegalDocumentHistory.from(LegalDocument.PRIVACY_POLICY, texts.reversed())

        then()
        assertEquals(listOf(SECOND_VERSION, FIRST_VERSION), history.versions.map { it.version })
    }

    @Test
    fun `finds the version in effect as the newest one that has started`() = runUnitTest {
        given()
        val history = stubHistory(FIRST_VERSION, SECOND_VERSION)

        whenn()
        val inEffect = history.inEffect(instant("2027-02-28T23:59:59Z"))

        then()
        assertEquals(FIRST_VERSION, inEffect?.version)
    }

    @Test
    fun `has no version in effect before the first one starts`() = runUnitTest {
        given()
        val history = stubHistory(FIRST_VERSION)

        whenn()
        val inEffect = history.inEffect(instant("2026-11-30T23:59:59Z"))

        then()
        assertNull(inEffect)
    }

    @Test
    fun `finds the next version as the earliest one that has not started`() = runUnitTest {
        given()
        val history = stubHistory(FIRST_VERSION, SECOND_VERSION, "2027-06-01")

        whenn()
        val next = history.next(instant("2026-12-15T00:00:00Z"))

        then()
        assertEquals(SECOND_VERSION, next?.version)
    }
}
