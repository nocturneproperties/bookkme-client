package me.bookk.designsystem.components

import kotlinx.datetime.LocalDate
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class DatePickerCommitTest {

    private val firstDate = LocalDate(2026, 12, 3)
    private val secondDate = LocalDate(2026, 12, 7)

    @Test
    fun `commits a changed date at once when committing on change`() = runUnitTest {
        given()
        val commit = DatePickerCommit(DateCommitMode.ON_CHANGE)

        whenn()
        val committed = commit.change(firstDate)

        then()
        assertEquals(firstDate, committed)
    }

    @Test
    fun `holds a changed date while the picker is open when committing on close`() = runUnitTest {
        given()
        val commit = DatePickerCommit(DateCommitMode.ON_CLOSE)

        whenn()
        val committed = commit.change(firstDate)

        then()
        assertNull(committed)
    }

    @Test
    fun `commits the last changed date when the picker closes`() = runUnitTest {
        given()
        val commit = DatePickerCommit(DateCommitMode.ON_CLOSE)
        commit.change(firstDate)
        commit.change(secondDate)

        whenn()
        val committed = commit.close()

        then()
        assertEquals(secondDate, committed)
    }

    @Test
    fun `commits nothing when the picker closes without a change`() = runUnitTest {
        given()
        val commit = DatePickerCommit(DateCommitMode.ON_CLOSE)

        whenn()
        val committed = commit.close()

        then()
        assertNull(committed)
    }

    @Test
    fun `commits nothing when the picker closes after the date was cleared`() = runUnitTest {
        given()
        val commit = DatePickerCommit(DateCommitMode.ON_CLOSE)
        commit.change(firstDate)
        commit.change(null)

        whenn()
        val committed = commit.close()

        then()
        assertNull(committed)
    }

    @Test
    fun `commits a held date only once`() = runUnitTest {
        given()
        val commit = DatePickerCommit(DateCommitMode.ON_CLOSE)
        commit.change(firstDate)
        commit.close()

        whenn()
        val committed = commit.close()

        then()
        assertNull(committed)
    }

    @Test
    fun `commits nothing on close when committing on change`() = runUnitTest {
        given()
        val commit = DatePickerCommit(DateCommitMode.ON_CHANGE)
        commit.change(firstDate)

        whenn()
        val committed = commit.close()

        then()
        assertNull(committed)
    }

    @Test
    fun `commits on close on an iPhone`() = runUnitTest {
        given()
        val userAgent = "Mozilla/5.0 (iPhone; CPU iPhone OS 18_1 like Mac OS X) " +
            "AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15E148"

        whenn()
        val mode = DateCommitMode.of(userAgent, maxTouchPoints = 5)

        then()
        assertEquals(DateCommitMode.ON_CLOSE, mode)
    }

    @Test
    fun `commits on close on an iPad that reports a Mac`() = runUnitTest {
        given()
        val userAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) " +
            "AppleWebKit/605.1.15 (KHTML, like Gecko) Version/18.1 Safari/605.1.15"

        whenn()
        val mode = DateCommitMode.of(userAgent, maxTouchPoints = 5)

        then()
        assertEquals(DateCommitMode.ON_CLOSE, mode)
    }

    @Test
    fun `commits on change on a Mac`() = runUnitTest {
        given()
        val userAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) " +
            "AppleWebKit/605.1.15 (KHTML, like Gecko) Version/18.1 Safari/605.1.15"

        whenn()
        val mode = DateCommitMode.of(userAgent, maxTouchPoints = 0)

        then()
        assertEquals(DateCommitMode.ON_CHANGE, mode)
    }

    @Test
    fun `commits on change on Android`() = runUnitTest {
        given()
        val userAgent = "Mozilla/5.0 (Linux; Android 15; Pixel 9) AppleWebKit/537.36 " +
            "(KHTML, like Gecko) Chrome/131.0.0.0 Mobile Safari/537.36"

        whenn()
        val mode = DateCommitMode.of(userAgent, maxTouchPoints = 5)

        then()
        assertEquals(DateCommitMode.ON_CHANGE, mode)
    }
}
