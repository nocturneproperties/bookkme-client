package me.bookk.core.presentation

import kotlinx.coroutines.isActive
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertNotSame
import kotlin.test.assertSame
import kotlin.test.assertTrue

class ViewModelStoreTest {

    private class TestViewModel : PlatformViewModel() {
        val isActive: Boolean
            get() = platformScope.isActive
    }

    private class Fixture {
        val sut = ViewModelStore()
    }

    @Test
    fun `returns the same view model for the same key`() = runUnitTest {
        given()
        val fixture = Fixture()
        val first = fixture.sut.getOrPut("home") { TestViewModel() }

        whenn()
        val second = fixture.sut.getOrPut("home") { TestViewModel() }

        then()
        assertSame(first, second)
    }

    @Test
    fun `creates a separate view model for another key`() = runUnitTest {
        given()
        val fixture = Fixture()
        val home = fixture.sut.getOrPut("home") { TestViewModel() }

        whenn()
        val services = fixture.sut.getOrPut("services") { TestViewModel() }

        then()
        assertNotSame(home, services)
    }

    @Test
    fun `clears every view model it holds`() = runUnitTest {
        given()
        val fixture = Fixture()
        val home = fixture.sut.getOrPut("home") { TestViewModel() }
        val services = fixture.sut.getOrPut("services") { TestViewModel() }

        whenn()
        fixture.sut.clear()

        then()
        assertFalse(home.isActive)
        assertFalse(services.isActive)
    }

    @Test
    fun `creates a new view model for a key after being cleared`() = runUnitTest {
        given()
        val fixture = Fixture()
        val cleared = fixture.sut.getOrPut("home") { TestViewModel() }
        fixture.sut.clear()

        whenn()
        val recreated = fixture.sut.getOrPut("home") { TestViewModel() }

        then()
        assertNotSame(cleared, recreated)
        assertTrue(recreated.isActive)
    }
}
