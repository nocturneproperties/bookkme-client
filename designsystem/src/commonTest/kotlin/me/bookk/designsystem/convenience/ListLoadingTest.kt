package me.bookk.designsystem.convenience

import dev.icerock.moko.resources.desc.desc
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import me.bookk.core.coroutine.DispatcherProvider
import me.bookk.core.presentation.error.PresentationNotification
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.designsystem.uistate.simple.BannerErrorState
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.milliseconds

class ListLoadingTest {

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

    private class Fixture(initialItems: List<String> = emptyList()) {
        val listState = FakeListState(initialItems)
        val refreshState = FakeRefreshState()
        val notifications = FakeNotificationState()
        val sut = TestListViewModel()

        var extraRefreshes = 0

        fun load(call: suspend () -> Collection<*>) =
            sut.load(listState, notifications, refreshState, onRefresh = { extraRefreshes++ }, call = call)

        fun pullToRefresh() = refreshState.onRefresh()
    }

    @Test
    fun `ends initial loading when the refresh returns no items`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        fixture.load { emptyList<String>() }

        then()
        assertFalse(fixture.listState.isInitialLoading)
    }

    @Test
    fun `keeps initial loading when the refresh returns items that are not rendered yet`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        fixture.load { listOf("a") }

        then()
        assertTrue(fixture.listState.isInitialLoading)
    }

    @Test
    fun `ends initial loading after a grace period when returned items are never rendered`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.load { listOf("a") }

        whenn()
        testScheduler.advanceTimeBy(RENDER_GRACE_PERIOD + 1.milliseconds)

        then()
        assertFalse(fixture.listState.isInitialLoading)
    }

    @Test
    fun `does not show the refresh indicator for a load the screen starts itself`() = runUnitTest {
        given()
        val fixture = Fixture()
        val pending = CompletableDeferred<List<String>>()

        whenn()
        fixture.load { pending.await() }

        then()
        assertFalse(fixture.refreshState.isRefreshing)
    }

    @Test
    fun `pull to refresh runs the call again`() = runUnitTest {
        given()
        val fixture = Fixture()
        var attempts = 0
        fixture.load { attempts++; listOf("a") }

        whenn()
        fixture.pullToRefresh()

        then()
        assertEquals(2, attempts)
    }

    @Test
    fun `pull to refresh runs the extra refresh work`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.load { listOf("a") }

        whenn()
        fixture.pullToRefresh()

        then()
        assertEquals(1, fixture.extraRefreshes)
    }

    @Test
    fun `shows the refresh indicator while a pull to refresh is running`() = runUnitTest {
        given()
        val fixture = Fixture()
        val pending = CompletableDeferred<List<String>>()
        var attempts = 0
        fixture.load { if (attempts++ == 0) listOf("a") else pending.await() }

        whenn()
        fixture.pullToRefresh()

        then()
        assertTrue(fixture.refreshState.isRefreshing)
    }

    @Test
    fun `hides the refresh indicator when a pull to refresh completes`() = runUnitTest {
        given()
        val fixture = Fixture(initialItems = listOf("a"))
        fixture.load { listOf("a") }

        whenn()
        fixture.pullToRefresh()

        then()
        assertFalse(fixture.refreshState.isRefreshing)
    }

    @Test
    fun `hides the refresh indicator when a pull to refresh fails`() = runUnitTest {
        given()
        val fixture = Fixture(initialItems = listOf("a"))
        var attempts = 0
        fixture.load { if (attempts++ == 0) listOf("a") else throw IllegalStateException() }

        whenn()
        fixture.pullToRefresh()

        then()
        assertFalse(fixture.refreshState.isRefreshing)
        assertNotNull(fixture.listState.bannerError)
    }

    @Test
    fun `keeps the refresh indicator while a newer pull to refresh is still running`() = runUnitTest {
        given()
        val fixture = Fixture()
        val first = CompletableDeferred<List<String>>()
        val second = CompletableDeferred<List<String>>()
        val calls = ArrayDeque(listOf<suspend () -> List<String>>({ listOf("a") }, { first.await() }, { second.await() }))
        fixture.load { calls.removeFirst()() }
        fixture.pullToRefresh()
        fixture.pullToRefresh()

        whenn()
        first.complete(emptyList())

        then()
        assertTrue(fixture.refreshState.isRefreshing)
    }

    @Test
    fun `retry from the full screen error does not show the refresh indicator`() = runUnitTest {
        given()
        val fixture = Fixture()
        val pending = CompletableDeferred<List<String>>()
        var attempts = 0
        fixture.load { if (attempts++ == 0) throw IllegalStateException() else pending.await() }

        whenn()
        fixture.listState.errorState!!.onRetryClick()

        then()
        assertFalse(fixture.refreshState.isRefreshing)
    }

    @Test
    fun `shows full screen error when the call fails and the list is empty`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        fixture.load { throw IllegalStateException() }

        then()
        assertNotNull(fixture.listState.errorState)
        assertNull(fixture.listState.bannerError)
    }

    @Test
    fun `full screen error describes the failure`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        fixture.load { throw IllegalStateException() }

        then()
        val errorState = assertNotNull(fixture.listState.errorState)
        assertEquals("title of IllegalStateException".desc(), errorState.title)
        assertEquals("message of IllegalStateException".desc(), errorState.subtitle)
    }

    @Test
    fun `shows banner error when the call fails and the list already has items`() = runUnitTest {
        given()
        val fixture = Fixture(initialItems = listOf("a"))

        whenn()
        fixture.load { throw IllegalStateException() }

        then()
        assertNotNull(fixture.listState.bannerError)
        assertNull(fixture.listState.errorState)
    }

    @Test
    fun `shows banner error when a refresh fails after the list loaded empty`() = runUnitTest {
        given()
        val fixture = Fixture()
        var attempts = 0
        fixture.load { if (attempts++ == 0) emptyList<String>() else throw IllegalStateException() }

        whenn()
        fixture.pullToRefresh()

        then()
        assertNotNull(fixture.listState.bannerError)
        assertNull(fixture.listState.errorState)
    }

    @Test
    fun `shows full screen error when a load fails after the list was reset to initial loading`() = runUnitTest {
        given()
        val fixture = Fixture(initialItems = listOf("a"))
        fixture.listState.clear()
        fixture.listState.isInitialLoading = true

        whenn()
        fixture.load { throw IllegalStateException() }

        then()
        assertNotNull(fixture.listState.errorState)
        assertNull(fixture.listState.bannerError)
    }

    @Test
    fun `does not add a notification for a failure shown in the list`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        fixture.load { throw IllegalStateException() }

        then()
        assertTrue(fixture.notifications.presentationNotification.isEmpty())
    }

    @Test
    fun `forwards unauthorized error to notifications`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        fixture.load { throw UnauthorizedTestError() }

        then()
        assertEquals(
            listOf<PresentationNotification>(PresentationNotification.Unauthorized),
            fixture.notifications.presentationNotification
        )
    }

    @Test
    fun `does not show a list error for unauthorized`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        fixture.load { throw UnauthorizedTestError() }

        then()
        assertNull(fixture.listState.errorState)
        assertNull(fixture.listState.bannerError)
    }

    @Test
    fun `does not show an error when the call is cancelled`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        fixture.load { throw CancellationException() }

        then()
        assertNull(fixture.listState.errorState)
        assertTrue(fixture.notifications.presentationNotification.isEmpty())
    }

    @Test
    fun `retry from the full screen error runs the call again and clears the error`() = runUnitTest {
        given()
        val fixture = Fixture()
        var attempts = 0
        fixture.load {
            attempts++
            if (attempts == 1) throw IllegalStateException()
            emptyList<String>()
        }

        whenn()
        fixture.listState.errorState!!.onRetryClick()

        then()
        assertEquals(2, attempts)
        assertNull(fixture.listState.errorState)
        assertFalse(fixture.listState.isInitialLoading)
    }

    @Test
    fun `banner shows progress while its retry is running`() = runUnitTest {
        given()
        val fixture = Fixture(initialItems = listOf("a"))
        val pending = CompletableDeferred<List<String>>()
        var attempts = 0
        fixture.load { if (attempts++ == 0) throw IllegalStateException() else pending.await() }

        whenn()
        fixture.listState.bannerError!!.onRetryClick()

        then()
        assertTrue(assertNotNull(fixture.listState.bannerError).isRetrying)
    }

    @Test
    fun `banner made from a full screen error shows progress while its retry is running`() = runUnitTest {
        given()
        val fixture = Fixture()
        val pending = CompletableDeferred<List<String>>()
        var attempts = 0
        fixture.load { if (attempts++ == 0) throw IllegalStateException() else pending.await() }
        val fullScreenRetry = fixture.listState.errorState!!.onRetryClick
        fixture.listState.replace(listOf("cached"))
        fixture.listState.errorState = null
        fixture.listState.bannerError = BannerErrorState.default(onRetryClick = fullScreenRetry)

        whenn()
        fixture.listState.bannerError!!.onRetryClick()

        then()
        assertTrue(assertNotNull(fixture.listState.bannerError).isRetrying)
    }

    @Test
    fun `banner goes back to the retry button when its retry fails`() = runUnitTest {
        given()
        val fixture = Fixture(initialItems = listOf("a"))
        fixture.load { throw IllegalStateException() }

        whenn()
        fixture.listState.bannerError!!.onRetryClick()

        then()
        assertFalse(assertNotNull(fixture.listState.bannerError).isRetrying)
    }

    @Test
    fun `banner goes back to the retry button when its retry is cancelled`() = runUnitTest {
        given()
        val fixture = Fixture(initialItems = listOf("a"))
        var attempts = 0
        fixture.load { if (attempts++ == 0) throw IllegalStateException() else throw CancellationException() }

        whenn()
        fixture.listState.bannerError!!.onRetryClick()

        then()
        assertFalse(assertNotNull(fixture.listState.bannerError).isRetrying)
    }

    @Test
    fun `pull to refresh hides the banner while it runs`() = runUnitTest {
        given()
        val fixture = Fixture(initialItems = listOf("a"))
        val pending = CompletableDeferred<List<String>>()
        var attempts = 0
        fixture.load { if (attempts++ == 0) throw IllegalStateException() else pending.await() }

        whenn()
        fixture.pullToRefresh()

        then()
        assertNull(fixture.listState.bannerError)
    }

    @Test
    fun `retry from the banner runs the call again and clears the banner`() = runUnitTest {
        given()
        val fixture = Fixture(initialItems = listOf("a"))
        var attempts = 0
        fixture.load {
            attempts++
            if (attempts == 1) throw IllegalStateException()
            listOf("a")
        }

        whenn()
        fixture.listState.bannerError!!.onRetryClick()

        then()
        assertEquals(2, attempts)
        assertNull(fixture.listState.bannerError)
    }
}
