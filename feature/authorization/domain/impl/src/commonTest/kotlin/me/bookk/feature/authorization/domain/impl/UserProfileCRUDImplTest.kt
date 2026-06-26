package me.bookk.feature.authorization.domain.impl

import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.mockk
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
        val dataSource = mockk<UserProfileDataSource>()
        val sut = UserProfileCRUDImpl(dataSource)
    }

    private fun stubProfile(id: Uuid = Uuid.random()) = UserProfile(
        id = id, firstName = "John", lastName = "Doe", email = "john@example.com"
    )

    @Test
    fun `updateFromRemote fetches from backend and upserts`() = runUnitTest {
        given()
        val fixture = Fixture()
        val profile = stubProfile()
        coEvery { fixture.dataSource.getProfileFromBackend() } returns profile
        coJustRun { fixture.dataSource.upsertProfile(profile) }

        whenn()
        fixture.fixture.updateFromRemote()

        then()
        coVerify { fixture.dataSource.upsertProfile(profile) }
    }

    @Test
    fun `get returns local profile when available`() = runUnitTest {
        given()
        val fixture = Fixture()
        val local = stubProfile()
        coEvery { fixture.dataSource.getProfileFromDatabase() } returns local

        whenn()
        val result = fixture.fixture.get()

        then()
        assertEquals(local, result)
        coVerify(exactly = 0) { fixture.dataSource.getProfileFromBackend() }
    }

    @Test
    fun `get fetches from backend and upserts when local is null`() = runUnitTest {
        given()
        val fixture = Fixture()
        val remote = stubProfile()
        coEvery { fixture.dataSource.getProfileFromDatabase() } returns null
        coEvery { fixture.dataSource.getProfileFromBackend() } returns remote
        coJustRun { fixture.dataSource.upsertProfile(remote) }

        whenn()
        val result = fixture.fixture.get()

        then()
        assertEquals(remote, result)
        coVerify { fixture.dataSource.upsertProfile(remote) }
    }

    @Test
    fun `update calls updateProfile and then syncs from backend`() = runUnitTest {
        given()
        val fixture = Fixture()
        val updated = stubProfile()
        val backend = updated.copy(firstName = "Synced")
        coJustRun { fixture.dataSource.updateProfile(updated) }
        coEvery { fixture.dataSource.getProfileFromBackend() } returns backend
        coJustRun { fixture.dataSource.updateProfile(backend) }

        whenn()
        fixture.fixture.update(updated)

        then()
        coVerify(ordering = io.mockk.Ordering.SEQUENCE) {
            fixture.dataSource.updateProfile(updated)
            fixture.dataSource.getProfileFromBackend()
            fixture.dataSource.updateProfile(backend)
        }
    }

    @Test
    fun `delete calls deleteProfile with correct id`() = runUnitTest {
        given()
        val fixture = Fixture()
        val id = Uuid.random()
        coJustRun { fixture.dataSource.deleteProfile(id) }

        whenn()
        fixture.fixture.delete(id)

        then()
        coVerify { fixture.dataSource.deleteProfile(id) }
    }
}
