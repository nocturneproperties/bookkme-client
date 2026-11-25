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
import kotlin.test.assertNull
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
    fun `keeps the view models of a popped screen while it slides away`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.sut.push("services")
        val services = fixture.viewModelOfCurrent()

        whenn()
        fixture.sut.pop()

        then()
        assertTrue(services.isActive)
    }

    @Test
    fun `clears the view models of a popped screen once its transition finishes`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.sut.push("services")
        val services = fixture.viewModelOfCurrent()
        fixture.sut.pop()

        whenn()
        fixture.sut.finishTransition()

        then()
        assertFalse(services.isActive)
    }

    @Test
    fun `slides forward from the previous screen when a screen is pushed`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        fixture.sut.push("services")

        then()
        assertEquals(BackStackDirection.FORWARD, fixture.sut.transition?.direction)
        assertEquals("home", fixture.sut.transition?.from?.route)
        assertEquals("services", fixture.sut.transition?.to?.route)
    }

    @Test
    fun `slides back to the previous screen when popped`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.sut.push("services")
        fixture.sut.finishTransition()

        whenn()
        fixture.sut.pop()

        then()
        assertEquals(BackStackDirection.BACKWARD, fixture.sut.transition?.direction)
        assertEquals("services", fixture.sut.transition?.from?.route)
        assertEquals("home", fixture.sut.transition?.to?.route)
    }

    @Test
    fun `shows both screens in stack order while sliding forward`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        fixture.sut.push("services")

        then()
        assertEquals(listOf("home", "services"), fixture.sut.visibleRoutes())
    }

    @Test
    fun `shows both screens in stack order while sliding back`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.sut.push("services")
        fixture.sut.finishTransition()

        whenn()
        fixture.sut.pop()

        then()
        assertEquals(listOf("home", "services"), fixture.sut.visibleRoutes())
    }

    @Test
    fun `shows only the current screen once the transition finishes`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.sut.push("services")

        whenn()
        fixture.sut.finishTransition()

        then()
        assertNull(fixture.sut.transition)
        assertEquals(listOf("services"), fixture.sut.visibleRoutes())
    }

    @Test
    fun `finishes the running transition when another screen is pushed`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.sut.push("services")
        val services = fixture.viewModelOfCurrent()
        fixture.sut.pop()

        whenn()
        fixture.sut.push("specialist")

        then()
        assertFalse(services.isActive)
        assertEquals(listOf("home", "specialist"), fixture.sut.visibleRoutes())
    }

    @Test
    fun `returns to the initial screen and slides back from the current one`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.sut.push("services")
        fixture.sut.push("specialist")
        fixture.sut.finishTransition()

        whenn()
        fixture.sut.popToRoot()

        then()
        assertEquals("home", fixture.sut.current)
        assertFalse(fixture.sut.canGoBack)
        assertEquals(BackStackDirection.BACKWARD, fixture.sut.transition?.direction)
        assertEquals("specialist", fixture.sut.transition?.from?.route)
        assertEquals("home", fixture.sut.transition?.to?.route)
    }

    @Test
    fun `clears the view models of the screens skipped on the way to the root`() = runUnitTest {
        given()
        val fixture = Fixture()
        val home = fixture.viewModelOfCurrent()
        fixture.sut.push("services")
        val services = fixture.viewModelOfCurrent()
        fixture.sut.push("specialist")
        val specialist = fixture.viewModelOfCurrent()

        whenn()
        fixture.sut.popToRoot()

        then()
        assertTrue(home.isActive)
        assertFalse(services.isActive)
        assertTrue(specialist.isActive)
    }

    @Test
    fun `replaces every screen above the initial one`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.sut.push("services")
        fixture.sut.push("review")
        fixture.sut.finishTransition()

        whenn()
        fixture.sut.replaceAboveRoot("sent")
        fixture.sut.pop()

        then()
        assertEquals("home", fixture.sut.current)
        assertFalse(fixture.sut.canGoBack)
    }

    @Test
    fun `slides forward from the current screen to the one replacing it`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.sut.push("services")
        fixture.sut.push("review")
        fixture.sut.finishTransition()

        whenn()
        fixture.sut.replaceAboveRoot("sent")

        then()
        assertEquals(BackStackDirection.FORWARD, fixture.sut.transition?.direction)
        assertEquals("review", fixture.sut.transition?.from?.route)
        assertEquals("sent", fixture.sut.transition?.to?.route)
    }

    @Test
    fun `clears the view models of the screens skipped when replacing above the root`() =
        runUnitTest {
            given()
            val fixture = Fixture()
            val home = fixture.viewModelOfCurrent()
            fixture.sut.push("services")
            val services = fixture.viewModelOfCurrent()
            fixture.sut.push("review")
            val review = fixture.viewModelOfCurrent()

            whenn()
            fixture.sut.replaceAboveRoot("sent")

            then()
            assertTrue(home.isActive)
            assertFalse(services.isActive)
            assertTrue(review.isActive)
        }

    @Test
    fun `clears the view models of a replaced screen once its transition finishes`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.sut.push("review")
        val review = fixture.viewModelOfCurrent()
        fixture.sut.replaceAboveRoot("sent")

        whenn()
        fixture.sut.finishTransition()

        then()
        assertFalse(review.isActive)
    }

    @Test
    fun `keeps the view models of the initial screen when it is replaced above`() = runUnitTest {
        given()
        val fixture = Fixture()
        val home = fixture.viewModelOfCurrent()
        fixture.sut.replaceAboveRoot("sent")

        whenn()
        fixture.sut.finishTransition()

        then()
        assertTrue(home.isActive)
        assertEquals("sent", fixture.sut.current)
    }

    @Test
    fun `does not slide when the initial screen is popped`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        fixture.sut.pop()

        then()
        assertNull(fixture.sut.transition)
    }

    @Test
    fun `clears the view models of a screen still sliding away when cleared`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.sut.push("services")
        val services = fixture.viewModelOfCurrent()
        fixture.sut.pop()

        whenn()
        fixture.sut.clear()

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

    @Test
    fun `returns to an earlier screen and slides back to it`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.sut.push("bookings")
        val bookings = fixture.viewModelOfCurrent()
        fixture.sut.push("details")
        fixture.sut.finishTransition()

        whenn()
        fixture.sut.returnTo("bookings")

        then()
        assertEquals("bookings", fixture.sut.current)
        assertEquals(BackStackDirection.BACKWARD, fixture.sut.transition?.direction)
        assertEquals("details", fixture.sut.transition?.from?.route)
        assertTrue(bookings.isActive)
    }

    @Test
    fun `clears the view models of the screens skipped on the way back to an earlier one`() =
        runUnitTest {
            given()
            val fixture = Fixture()
            fixture.sut.push("bookings")
            fixture.sut.push("sent")
            val sent = fixture.viewModelOfCurrent()
            fixture.sut.push("details")

            whenn()
            fixture.sut.returnTo("bookings")

            then()
            assertFalse(sent.isActive)
            assertTrue(fixture.sut.canGoBack)
        }

    @Test
    fun `replaces every screen above the initial one when returning to a screen not in it`() =
        runUnitTest {
            given()
            val fixture = Fixture()
            fixture.sut.push("details")

            whenn()
            fixture.sut.returnTo("bookings")

            then()
            assertEquals("bookings", fixture.sut.current)
            assertEquals(BackStackDirection.FORWARD, fixture.sut.transition?.direction)
            fixture.sut.pop()
            assertEquals("home", fixture.sut.current)
        }
}

private fun BackStack<String>.visibleRoutes(): List<String> {
    return visibleEntries.map { it.route }
}
