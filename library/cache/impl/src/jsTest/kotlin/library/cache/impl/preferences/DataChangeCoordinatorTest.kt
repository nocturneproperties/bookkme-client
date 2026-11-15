package library.cache.impl.preferences

import androidx.datastore.core.InterProcessCoordinator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import kotlin.test.Test
import kotlin.test.assertEquals

class DataChangeCoordinatorTest {
    private class FakeCoordinator : InterProcessCoordinator {
        var version = 0

        override val updateNotifications: Flow<Unit> = emptyFlow()

        override suspend fun <T> lock(block: suspend () -> T): T {
            return block()
        }

        override suspend fun <T> tryLock(block: suspend (Boolean) -> T): T {
            return block(true)
        }

        override suspend fun getVersion(): Int {
            return version
        }

        override suspend fun incrementAndGetVersion(): Int {
            version += 1
            return version
        }
    }

    private class Fixture {
        val changedKeys = MutableSharedFlow<String?>()
        val delegate = FakeCoordinator()
        val sut = DataChangeCoordinator(delegate, DATA_KEY, changedKeys)
    }

    @Test
    fun `notifies an update when another tab writes the data key`() = runUnitTest {
        given()
        val fixture = Fixture()
        val notifications = mutableListOf<Unit>()
        val collection = launch(UnconfinedTestDispatcher(testScheduler)) {
            fixture.sut.updateNotifications.toList(notifications)
        }

        whenn()
        fixture.changedKeys.emit(DATA_KEY)

        then()
        assertEquals(1, notifications.size)
        collection.cancel()
    }

    @Test
    fun `does not notify when another tab only bumps the version`() = runUnitTest {
        given()
        val fixture = Fixture()
        val notifications = mutableListOf<Unit>()
        val collection = launch(UnconfinedTestDispatcher(testScheduler)) {
            fixture.sut.updateNotifications.toList(notifications)
        }

        whenn()
        fixture.changedKeys.emit("datastore_LOCAL_${DATA_KEY}_version")
        fixture.changedKeys.emit("other_prefs.preferences_pb")

        then()
        assertEquals(0, notifications.size)
        collection.cancel()
    }

    @Test
    fun `notifies an update when another tab clears the storage`() = runUnitTest {
        given()
        val fixture = Fixture()
        val notifications = mutableListOf<Unit>()
        val collection = launch(UnconfinedTestDispatcher(testScheduler)) {
            fixture.sut.updateNotifications.toList(notifications)
        }

        whenn()
        fixture.changedKeys.emit(null)

        then()
        assertEquals(1, notifications.size)
        collection.cancel()
    }

    @Test
    fun `reports the version of the wrapped coordinator`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.delegate.version = 7

        whenn()
        val version = fixture.sut.getVersion()

        then()
        assertEquals(7, version)
    }

    private companion object {
        const val DATA_KEY = "clients_prefs.preferences_pb"
    }
}
