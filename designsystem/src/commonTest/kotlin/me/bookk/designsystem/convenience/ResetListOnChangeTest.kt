package me.bookk.designsystem.convenience

import dev.icerock.moko.resources.desc.desc
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.designsystem.uistate.simple.BannerErrorState
import me.bookk.designsystem.uistate.simple.ErrorState
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ResetListOnChangeTest {

    private class Fixture {
        val listState = FakeListState(initialItems = listOf("cached"))
    }

    @Test
    fun `emits each distinct key once`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val result = flowOf(1, 1, 2).resetListOnChange(fixture.listState).toList()

        then()
        assertEquals(listOf(1, 2), result)
    }

    @Test
    fun `keeps the list as is for the first key`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        flowOf(1).resetListOnChange(fixture.listState).toList()

        then()
        assertEquals(listOf("cached"), fixture.listState.items)
        assertFalse(fixture.listState.isInitialLoading)
    }

    @Test
    fun `resets the list to initial loading when the key changes`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        flowOf(1, 2).resetListOnChange(fixture.listState).toList()

        then()
        assertTrue(fixture.listState.items.isEmpty())
        assertTrue(fixture.listState.isInitialLoading)
    }

    @Test
    fun `clears errors of the previous key when the key changes`() = runUnitTest {
        given()
        val fixture = Fixture()
        val keys = MutableSharedFlow<Int>()
        val job = launch(UnconfinedTestDispatcher(testScheduler)) {
            keys.resetListOnChange(fixture.listState).collect {}
        }
        keys.emit(1)
        fixture.listState.errorState = ErrorState("t".desc(), "s".desc()) {}
        fixture.listState.bannerError = BannerErrorState("b".desc()) {}

        whenn()
        keys.emit(2)

        then()
        job.cancel()
        assertNull(fixture.listState.errorState)
        assertNull(fixture.listState.bannerError)
    }

    @Test
    fun `resets the list before the new key reaches downstream`() = runUnitTest {
        given()
        val fixture = Fixture()
        val itemsSeenDownstream = mutableListOf<List<String>>()

        whenn()
        flowOf(1, 2).resetListOnChange(fixture.listState)
            .onEach { itemsSeenDownstream += fixture.listState.items.toList() }
            .toList()

        then()
        assertEquals(listOf(listOf("cached"), emptyList()), itemsSeenDownstream)
    }
}
