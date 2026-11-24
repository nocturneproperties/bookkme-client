package me.bookk.designsystem.uistate

import dev.icerock.moko.resources.desc.desc
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.designsystem.uistate.simple.ErrorState
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertSame

class WebListStateTest {

    private class Fixture {
        var retries = 0
        val error = ErrorState("title".desc(), "subtitle".desc(), onRetryClick = { retries++ })
        val sut = WebListState<String>()
    }

    @Test
    fun `ends initial loading when items are appended`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        fixture.sut.append(listOf("a"))

        then()
        assertFalse(fixture.sut.isInitialLoading)
        assertEquals(listOf("a"), fixture.sut.items.toList())
    }

    @Test
    fun `turns a full screen error into a retrying banner once items arrive`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.sut.errorState = fixture.error

        whenn()
        fixture.sut.replace(listOf("a"))

        then()
        assertNull(fixture.sut.errorState)
        val banner = assertNotNull(fixture.sut.bannerError)
        banner.onRetryClick()
        assertEquals(1, fixture.retries)
    }

    @Test
    fun `keeps the full screen error when the replacement is empty`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.sut.errorState = fixture.error

        whenn()
        fixture.sut.replace(emptyList())

        then()
        assertSame(fixture.error, fixture.sut.errorState)
        assertNull(fixture.sut.bannerError)
    }
}
