package me.bookk.designsystem.convenience

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import me.bookk.core.coroutine.DispatcherProvider
import me.bookk.core.domain.pagination.Page
import me.bookk.core.domain.pagination.PagedSource
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.time.Duration.Companion.milliseconds

class ListPagerTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var originalMain: CoroutineDispatcher
    private lateinit var originalIo: CoroutineDispatcher

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        originalMain = DispatcherProvider.main
        originalIo = DispatcherProvider.io
        DispatcherProvider.swapMain(testDispatcher)
        DispatcherProvider.swapIo(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        DispatcherProvider.swapMain(originalMain)
        DispatcherProvider.swapIo(originalIo)
        Dispatchers.resetMain()
    }

    private class Fixture {
        val listState = FakeListState(initialItems = listOf("cached"))
        val refreshState = FakeRefreshState()
        val notifications = FakeNotificationState()
        val viewModel = TestListViewModel()

        var firstPage: suspend (String) -> Page<String> = {
            Page(listOf("a"), nextCursor = "after-a")
        }
        var nextPage: suspend (String) -> Page<String> = { Page(listOf("b"), nextCursor = null) }
        val requestedPages = mutableListOf<Pair<String, String>>()
        val requestedCursors: List<String>
            get() = requestedPages.map { it.second }

        private val source = object : PagedSource<String, String> {
            override suspend fun refresh(key: String): Page<String> {
                return firstPage(key)
            }

            override suspend fun loadMore(key: String, cursor: String): Page<String> {
                requestedPages += key to cursor
                return nextPage(cursor)
            }
        }

        val sut = viewModel.listPager(listState, notifications, source, refreshState)

        fun load(key: String = "business") = sut.load(key)

        fun pullToRefresh() = refreshState.onRefresh()

        fun scrollToEnd() = listState.loadMore?.invoke()
    }

    @Test
    fun `offers the next page when the first page has more`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        fixture.load()

        then()
        assertNotNull(fixture.listState.loadMore)
    }

    @Test
    fun `offers no next page when the first page is the last`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.firstPage = { Page(listOf("a"), nextCursor = null) }

        whenn()
        fixture.load()

        then()
        assertNull(fixture.listState.loadMore)
    }

    @Test
    fun `offers the next page only after the first page had time to render`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.listState.replace(emptyList())
        fixture.listState.isInitialLoading = true
        fixture.load()
        val offeredBeforeRender = fixture.listState.loadMore

        whenn()
        advanceTimeBy(RENDER_GRACE_PERIOD + 1.milliseconds)

        then()
        assertNull(offeredBeforeRender)
        assertNotNull(fixture.listState.loadMore)
    }

    @Test
    fun `loads the page after the cursor of the first page`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.load()

        whenn()
        fixture.scrollToEnd()

        then()
        assertEquals(listOf("after-a"), fixture.requestedCursors)
    }

    @Test
    fun `loads each following page after the cursor of the previous one`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.nextPage = { cursor -> Page(listOf("b"), nextCursor = "$cursor-next") }
        fixture.load()

        whenn()
        fixture.scrollToEnd()
        fixture.scrollToEnd()

        then()
        assertEquals(listOf("after-a", "after-a-next"), fixture.requestedCursors)
        assertNotNull(fixture.listState.loadMore)
    }

    @Test
    fun `loads following pages for the key of the first page`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.load(key = "business-a")

        whenn()
        fixture.scrollToEnd()

        then()
        assertEquals(listOf("business-a" to "after-a"), fixture.requestedPages)
    }

    @Test
    fun `loading another key continues with the pages of that key`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.firstPage = { key -> Page(listOf("a"), nextCursor = "after-$key") }
        fixture.load(key = "business-a")
        fixture.load(key = "business-b")

        whenn()
        fixture.scrollToEnd()

        then()
        assertEquals(listOf("business-b" to "after-business-b"), fixture.requestedPages)
    }

    @Test
    fun `stops offering pages after the last page`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.load()

        whenn()
        fixture.scrollToEnd()

        then()
        assertNull(fixture.listState.loadMore)
    }

    @Test
    fun `shows a banner error when the next page fails`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.nextPage = { throw IllegalStateException() }
        fixture.load()

        whenn()
        fixture.scrollToEnd()

        then()
        assertNotNull(fixture.listState.bannerError)
    }

    @Test
    fun `retrying a failed next page loads the same page again`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.nextPage = { throw IllegalStateException() }
        fixture.load()
        fixture.scrollToEnd()
        fixture.nextPage = { Page(listOf("b"), nextCursor = null) }

        whenn()
        fixture.listState.bannerError?.onRetryClick?.invoke()

        then()
        assertEquals(listOf("after-a", "after-a"), fixture.requestedCursors)
        assertNull(fixture.listState.bannerError)
        assertNull(fixture.listState.loadMore)
    }

    @Test
    fun `ignores the end of the list while the first page is reloading`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.load()
        val reload = CompletableDeferred<Page<String>>()
        fixture.firstPage = { reload.await() }
        fixture.pullToRefresh()

        whenn()
        fixture.scrollToEnd()

        then()
        assertEquals(emptyList(), fixture.requestedCursors)
    }

    @Test
    fun `reloading the first page cancels a running next page`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.load()
        val next = CompletableDeferred<Page<String>>()
        fixture.nextPage = { next.await() }
        fixture.scrollToEnd()

        whenn()
        fixture.pullToRefresh()
        next.complete(Page(listOf("b"), nextCursor = null))

        then()
        assertNotNull(fixture.listState.loadMore)
    }

    @Test
    fun `pull to refresh starts again from the cursor of the new first page`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.nextPage = { Page(listOf("b"), nextCursor = "after-b") }
        fixture.load()
        fixture.scrollToEnd()
        fixture.firstPage = { Page(listOf("a2"), nextCursor = "after-a2") }

        whenn()
        fixture.pullToRefresh()
        fixture.scrollToEnd()

        then()
        assertEquals(listOf("after-a", "after-a2"), fixture.requestedCursors)
        assertFalse(fixture.refreshState.isRefreshing)
    }
}
