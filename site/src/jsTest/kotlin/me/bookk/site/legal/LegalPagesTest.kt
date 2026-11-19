package me.bookk.site.legal

import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.site.designsystem.resources.AppLanguage
import me.bookk.site.legal.markdown.MarkdownBlock
import me.bookk.site.legal.markdown.MarkdownInline
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class LegalPagesTest {

    private val afterSecondVersion = instant("2027-04-01T00:00:00Z")
    private val betweenVersions = instant("2027-01-01T00:00:00Z")
    private val beforeFirstVersion = instant("2026-11-15T00:00:00Z")

    @Test
    fun `shows the version in effect in the site language`() = runUnitTest {
        given()
        val history = stubHistory(FIRST_VERSION, SECOND_VERSION)

        whenn()
        val page = LegalPages.build(history, LegalPageRequest(), AppLanguage.PL, afterSecondVersion)

        then()
        assertEquals(SECOND_VERSION, page?.version?.version)
        assertEquals(AppLanguage.PL, page?.language)
        assertEquals(
            listOf(MarkdownBlock.Heading(1, listOf(MarkdownInline.Text("pl $SECOND_VERSION")))),
            page?.blocks,
        )
    }

    @Test
    fun `shows the first upcoming version when none is in effect yet`() = runUnitTest {
        given()
        val history = stubHistory(FIRST_VERSION, SECOND_VERSION)

        whenn()
        val page = LegalPages.build(history, LegalPageRequest(), AppLanguage.EN, beforeFirstVersion)

        then()
        assertEquals(FIRST_VERSION, page?.version?.version)
        assertEquals(LegalNotice.Upcoming(date(FIRST_VERSION), currentPath = null), page?.notice)
    }

    @Test
    fun `shows no page when nothing is published`() = runUnitTest {
        given()
        val history = stubHistory()

        whenn()
        val page = LegalPages.build(history, LegalPageRequest(), AppLanguage.EN, afterSecondVersion)

        then()
        assertNull(page)
    }

    @Test
    fun `shows the requested version`() = runUnitTest {
        given()
        val history = stubHistory(FIRST_VERSION, SECOND_VERSION)

        whenn()
        val page = LegalPages.build(
            history,
            LegalPageRequest(FIRST_VERSION),
            AppLanguage.EN,
            afterSecondVersion,
        )

        then()
        assertEquals(FIRST_VERSION, page?.version?.version)
    }

    @Test
    fun `shows no page for an unknown version`() = runUnitTest {
        given()
        val history = stubHistory(FIRST_VERSION)

        whenn()
        val page = LegalPages.build(
            history,
            LegalPageRequest("2020-01-01"),
            AppLanguage.EN,
            afterSecondVersion,
        )

        then()
        assertNull(page)
    }

    @Test
    fun `reports a requested version as published`() = runUnitTest {
        given()
        val history = stubHistory(FIRST_VERSION)

        whenn()
        val isPublished = LegalPages.isPublished(
            history,
            LegalPageRequest(FIRST_VERSION),
            AppLanguage.UK,
            afterSecondVersion,
        )

        then()
        assertTrue(isPublished)
    }

    @Test
    fun `reports an unknown version as not published`() = runUnitTest {
        given()
        val history = stubHistory(FIRST_VERSION)

        whenn()
        val isPublished = LegalPages.isPublished(
            history,
            LegalPageRequest("2020-01-01"),
            AppLanguage.EN,
            afterSecondVersion,
        )

        then()
        assertFalse(isPublished)
    }

    @Test
    fun `reports a version without a text in the site language as not published`() = runUnitTest {
        given()
        val history = LegalDocumentHistory.from(
            LegalDocument.PRIVACY_POLICY,
            stubPublishedTexts(version = FIRST_VERSION).filter {
                it.language == AppLanguage.EN.tag
            },
        )

        whenn()
        val isPublished = LegalPages.isPublished(
            history,
            LegalPageRequest(),
            AppLanguage.PL,
            afterSecondVersion,
        )

        then()
        assertFalse(isPublished)
    }

    @Test
    fun `shows no page when the text is not published in the site language`() = runUnitTest {
        given()
        val history = LegalDocumentHistory.from(
            LegalDocument.PRIVACY_POLICY,
            stubPublishedTexts(version = FIRST_VERSION).filter {
                it.language == AppLanguage.EN.tag
            },
        )

        whenn()
        val page = LegalPages.build(history, LegalPageRequest(), AppLanguage.PL, afterSecondVersion)

        then()
        assertNull(page)
    }

    @Test
    fun `explains that a superseded version is no longer in effect`() = runUnitTest {
        given()
        val history = stubHistory(FIRST_VERSION, SECOND_VERSION)

        whenn()
        val page = LegalPages.build(
            history,
            LegalPageRequest(FIRST_VERSION),
            AppLanguage.EN,
            afterSecondVersion,
        )

        then()
        assertEquals(
            LegalNotice.Superseded(
                LegalPeriod.Superseded(date(FIRST_VERSION), date(SECOND_VERSION)),
                currentPath = "/privacy/$SECOND_VERSION",
            ),
            page?.notice,
        )
    }

    @Test
    fun `points an upcoming version to the version in effect`() = runUnitTest {
        given()
        val history = stubHistory(FIRST_VERSION, SECOND_VERSION)

        whenn()
        val page = LegalPages.build(
            history,
            LegalPageRequest(SECOND_VERSION),
            AppLanguage.EN,
            betweenVersions,
        )

        then()
        assertEquals(
            LegalNotice.Upcoming(date(SECOND_VERSION), currentPath = "/privacy/$FIRST_VERSION"),
            page?.notice,
        )
    }

    @Test
    fun `announces the next version on the version in effect`() = runUnitTest {
        given()
        val history = stubHistory(FIRST_VERSION, SECOND_VERSION)

        whenn()
        val page = LegalPages.build(history, LegalPageRequest(), AppLanguage.EN, betweenVersions)

        then()
        assertEquals(
            LegalNotice.NewVersion(date(SECOND_VERSION), "/privacy/$SECOND_VERSION"),
            page?.notice,
        )
    }

    @Test
    fun `shows no notice on the version in effect without a next version`() = runUnitTest {
        given()
        val history = stubHistory(FIRST_VERSION, SECOND_VERSION)

        whenn()
        val page = LegalPages.build(history, LegalPageRequest(), AppLanguage.EN, afterSecondVersion)

        then()
        assertNull(page?.notice)
    }

    @Test
    fun `lists every version newest first with its period`() = runUnitTest {
        given()
        val history = stubHistory(FIRST_VERSION, SECOND_VERSION, "2027-06-01")

        whenn()
        val page = LegalPages.build(
            history,
            LegalPageRequest(FIRST_VERSION),
            AppLanguage.EN,
            afterSecondVersion,
        )

        then()
        assertEquals(
            listOf(
                LegalHistoryEntry(
                    "2027-06-01",
                    "/privacy/2027-06-01",
                    LegalPeriod.Upcoming(date("2027-06-01")),
                    isSelected = false,
                ),
                LegalHistoryEntry(
                    SECOND_VERSION,
                    "/privacy/$SECOND_VERSION",
                    LegalPeriod.InEffect(date(SECOND_VERSION)),
                    isSelected = false,
                ),
                LegalHistoryEntry(
                    FIRST_VERSION,
                    "/privacy/$FIRST_VERSION",
                    LegalPeriod.Superseded(date(FIRST_VERSION), date(SECOND_VERSION)),
                    isSelected = true,
                ),
            ),
            page?.history,
        )
    }

    @Test
    fun `links every language of the shown version`() = runUnitTest {
        given()
        val history = stubHistory(FIRST_VERSION)

        whenn()
        val page = LegalPages.build(history, LegalPageRequest(), AppLanguage.UK, afterSecondVersion)

        then()
        assertEquals(
            listOf(
                LegalLanguageLink(AppLanguage.EN, "/privacy/$FIRST_VERSION", isSelected = false),
                LegalLanguageLink(AppLanguage.UK, "/uk/privacy/$FIRST_VERSION", isSelected = true),
                LegalLanguageLink(AppLanguage.PL, "/pl/privacy/$FIRST_VERSION", isSelected = false),
            ),
            page?.languages,
        )
    }

    @Test
    fun `links the version history in the page language`() = runUnitTest {
        given()
        val history = stubHistory(FIRST_VERSION, SECOND_VERSION)

        whenn()
        val page = LegalPages.build(history, LegalPageRequest(), AppLanguage.PL, afterSecondVersion)

        then()
        assertEquals(
            listOf("/pl/privacy/$SECOND_VERSION", "/pl/privacy/$FIRST_VERSION"),
            page?.history?.map { it.path },
        )
    }

    @Test
    fun `links the notice in the page language`() = runUnitTest {
        given()
        val history = stubHistory(FIRST_VERSION, SECOND_VERSION)

        whenn()
        val page = LegalPages.build(history, LegalPageRequest(), AppLanguage.UK, betweenVersions)

        then()
        assertEquals(
            LegalNotice.NewVersion(date(SECOND_VERSION), "/uk/privacy/$SECOND_VERSION"),
            page?.notice,
        )
    }
}
