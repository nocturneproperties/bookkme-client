package me.bookk.designsystem.uistate

import dev.icerock.moko.resources.desc.desc
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.whenn
import me.bookk.designsystem.uistate.simple.ErrorState
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertSame

class AndroidListStateTest {

    private class Fixture {
        var retries = 0
        val sut = AndroidListState<String>().apply {
            errorState = ErrorState(title = "title".desc(), subtitle = "subtitle".desc()) { retries++ }
        }
    }

    @Test
    fun `replacing with items clears the full screen error`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        fixture.sut.replace(listOf("a"))

        then()
        assertNull(fixture.sut.errorState)
    }

    @Test
    fun `replacing with items turns the full screen error into a banner that retries`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        fixture.sut.replace(listOf("a"))

        then()
        assertNotNull(fixture.sut.bannerError).onRetryClick()
        assertEquals(1, fixture.retries)
    }

    @Test
    fun `appending items turns the full screen error into a banner`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        fixture.sut.append(listOf("a"))

        then()
        assertNull(fixture.sut.errorState)
        assertNotNull(fixture.sut.bannerError)
    }

    @Test
    fun `replacing with no items keeps the full screen error`() = runUnitTest {
        given()
        val fixture = Fixture()
        val error = fixture.sut.errorState

        whenn()
        fixture.sut.replace(emptyList())

        then()
        assertSame(error, fixture.sut.errorState)
        assertNull(fixture.sut.bannerError)
    }
}
