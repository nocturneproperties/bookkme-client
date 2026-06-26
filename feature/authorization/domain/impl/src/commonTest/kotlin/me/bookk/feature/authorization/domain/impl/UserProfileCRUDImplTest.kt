package me.bookk.feature.authorization.domain.impl

import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifySuspend
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.feature.authorization.domain.datasource.profile.UserProfileDataSource
import me.bookk.feature.authorization.domain.entity.UserProfile
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.uuid.Uuid

@OptIn(ExperimentalCoroutinesApi::class)
class UserProfileCRUDImplTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private class Fixture {
        val dataSource = mock<UserProfileDataSource>()
        val sut = UserProfileCRUDImpl(dataSource)
    }

    private fun stubProfile(id: Uuid = Uuid.random()) = UserProfile(
        id = id, firstName = "John", lastName = "Doe", email = "john@example.com"
    )

    @Test
    fun `updateFromRemote fetches from backend and upserts`() = runUnitTest {
        given()
        val sut = Fixture()
        val profile = stubProfile()
        everySuspend { sut.dataSource.getProfileFromBackend() } returns profile
        everySuspend { sut.dataSource.upsertProfile(profile) } returns Unit

        whenn()
        sut.sut.updateFromRemote()

        then()
        verifySuspend { sut.dataSource.upsertProfile(profile) }
    }

    @Test
    fun `get returns local profile when available`() = runUnitTest {
        given()
        val sut = Fixture()
        val local = stubProfile()
        everySuspend { sut.dataSource.getProfileFromDatabase() } returns local

        whenn()
        val result = sut.sut.get()

        then()
        assertEquals(local, result)
        verifySuspend(VerifyMode.exactly(0)) { sut.dataSource.getProfileFromBackend() }
    }

    @Test
    fun `get fetches from backend and upserts when local is null`() = runUnitTest {
        given()
        val sut = Fixture()
        val remote = stubProfile()
        everySuspend { sut.dataSource.getProfileFromDatabase() } returns null
        everySuspend { sut.dataSource.getProfileFromBackend() } returns remote
        everySuspend { sut.dataSource.upsertProfile(remote) } returns Unit

        whenn()
        val result = sut.sut.get()

        then()
        assertEquals(remote, result)
        verifySuspend { sut.dataSource.upsertProfile(remote) }
    }

    @Test
    fun `update calls updateProfile and then syncs from backend`() = runUnitTest {
        given()
        val sut = Fixture()
        val updated = stubProfile()
        val backend = updated.copy(firstName = "Synced")
        everySuspend { sut.dataSource.updateProfile(updated) } returns Unit
        everySuspend { sut.dataSource.getProfileFromBackend() } returns backend
        everySuspend { sut.dataSource.updateProfile(backend) } returns Unit

        whenn()
        sut.sut.update(updated)

        then()
        verifySuspend(VerifyMode.order) {
            sut.dataSource.updateProfile(updated)
            sut.dataSource.getProfileFromBackend()
            sut.dataSource.updateProfile(backend)
        }
    }

    @Test
    fun `delete calls deleteProfile with correct id`() = runUnitTest {
        given()
        val sut = Fixture()
        val id = Uuid.random()
        everySuspend { sut.dataSource.deleteProfile(id) } returns Unit

        whenn()
        sut.sut.delete(id)

        then()
        verifySuspend { sut.dataSource.deleteProfile(id) }
    }
}
