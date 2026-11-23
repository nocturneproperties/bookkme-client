package me.bookk.core.presentation.navigation

import kotlinx.coroutines.isActive
import me.bookk.core.presentation.PlatformViewModel
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class BackStackTest {

    private class TestViewModel : PlatformViewModel() {
        val isActive: Boolean
            get() = platformScope.isActive
    }

    private class Fixture {
        val sut = BackStack("home")

        fun viewModelOfCurrent(): TestViewModel {
            return sut.currentEntry.viewModelStore.getOrPut("vm") { TestViewModel() }
        }
    }

    @Test
    fun `starts on the initial screen without a way back`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val current = fixture.sut.current

        then()
        assertEquals("home", current)
        assertFalse(fixture.sut.canGoBack)
    }

    @Test
    fun `shows a pushed screen on top`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        fixture.sut.push("services")

        then()
        assertEquals("services", fixture.sut.current)
        assertTrue(fixture.sut.canGoBack)
    }

    @Test
    fun `returns to the previous screen and keeps its view models`() = runUnitTest {
        given()
        val fixture = Fixture()
        val home = fixture.viewModelOfCurrent()
        fixture.sut.push("services")

        whenn()
        fixture.sut.pop()

        then()
        assertEquals("home", fixture.sut.current)
        assertTrue(home.isActive)
    }

    @Test
    fun `clears the view models of a popped screen`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.sut.push("services")
        val services = fixture.viewModelOfCurrent()

        whenn()
        fixture.sut.pop()

        then()
        assertFalse(services.isActive)
    }

    @Test
    fun `keeps the initial screen when popped`() = runUnitTest {
        given()
        val fixture = Fixture()
        val home = fixture.viewModelOfCurrent()

        whenn()
        fixture.sut.pop()

        then()
        assertEquals("home", fixture.sut.current)
        assertTrue(home.isActive)
    }

    @Test
    fun `clears the view models of every screen when cleared`() = runUnitTest {
        given()
        val fixture = Fixture()
        val home = fixture.viewModelOfCurrent()
        fixture.sut.push("services")
        val services = fixture.viewModelOfCurrent()

        whenn()
        fixture.sut.clear()

        then()
        assertFalse(home.isActive)
        assertFalse(services.isActive)
    }
}
