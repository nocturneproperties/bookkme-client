package me.bookk.core.domain.pagination

import me.bookk.core.domain.entity.Error
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue

class PageRestartTest {

    private class Fixture {
        var restarts = 0
        var fetchResult: () -> Page<String> = { Page(listOf("next"), nextCursor = null) }
        val firstPage = Page(listOf("first"), nextCursor = "after-first")

        suspend fun sut(): Page<String> {
            return fetchPageOrRestart(
                fetch = { fetchResult() },
                restart = {
                    restarts++
                    firstPage
                },
            )
        }
    }

    @Test
    fun `returns the fetched page`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.fetchResult = { Page(listOf("b"), nextCursor = "after-b") }

        whenn()
        val page = fixture.sut()

        then()
        assertEquals(Page(listOf("b"), nextCursor = "after-b"), page)
        assertEquals(0, fixture.restarts)
    }

    @Test
    fun `restarts from the first page when the server rejects the cursor`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.fetchResult = { throw Error.BusinessError(PageErrorCodes.CURSOR_INVALID, "") }

        whenn()
        val page = fixture.sut()

        then()
        assertEquals(1, fixture.restarts)
        assertSame(fixture.firstPage, page)
    }

    @Test
    fun `rethrows other business errors`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.fetchResult = { throw Error.BusinessError(PageErrorCodes.LIMIT_INVALID, "") }

        whenn()
        val error = assertFailsWith<Error.BusinessError> { fixture.sut() }

        then()
        assertEquals(PageErrorCodes.LIMIT_INVALID, error.errorCode)
        assertEquals(0, fixture.restarts)
    }

    @Test
    fun `has more pages only while the server returns a next cursor`() = runUnitTest {
        given()
        val pages = listOf(Page(listOf(1), nextCursor = "c"), Page(listOf(2), nextCursor = null))

        whenn()
        val hasMore = pages.map { it.hasMore }

        then()
        assertTrue(hasMore.first())
        assertFalse(hasMore.last())
    }
}
