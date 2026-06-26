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
        val fixture = Fixture()
        val profile = stubProfile()
        everySuspend { fixture.dataSource.getProfileFromBackend() } returns profile
        everySuspend { fixture.dataSource.upsertProfile(profile) } returns Unit

        whenn()
        fixture.sut.updateFromRemote()

        then()
        verifySuspend { fixture.dataSource.upsertProfile(profile) }
    }

    @Test
    fun `get returns local profile when available`() = runUnitTest {
        given()
        val fixture = Fixture()
        val local = stubProfile()
        everySuspend { fixture.dataSource.getProfileFromDatabase() } returns local

        whenn()
        val result = fixture.sut.get()

        then()
        assertEquals(local, result)
        verifySuspend(VerifyMode.exactly(0)) { fixture.dataSource.getProfileFromBackend() }
    }

    @Test
    fun `get fetches from backend and upserts when local is null`() = runUnitTest {
        given()
        val fixture = Fixture()
        val remote = stubProfile()
        everySuspend { fixture.dataSource.getProfileFromDatabase() } returns null
        everySuspend { fixture.dataSource.getProfileFromBackend() } returns remote
        everySuspend { fixture.dataSource.upsertProfile(remote) } returns Unit

        whenn()
        val result = fixture.sut.get()

        then()
        assertEquals(remote, result)
        verifySuspend { fixture.dataSource.upsertProfile(remote) }
    }

    @Test
    fun `update calls updateProfile and then syncs from backend`() = runUnitTest {
        given()
        val fixture = Fixture()
        val updated = stubProfile()
        val backend = updated.copy(firstName = "Synced")
        everySuspend { fixture.dataSource.updateProfile(updated) } returns Unit
        everySuspend { fixture.dataSource.getProfileFromBackend() } returns backend
        everySuspend { fixture.dataSource.updateProfile(backend) } returns Unit

        whenn()
        fixture.sut.update(updated)

        then()
        verifySuspend(VerifyMode.order) {
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
        everySuspend { fixture.dataSource.deleteProfile(id) } returns Unit

        whenn()
        fixture.sut.delete(id)

        then()
        verifySuspend { fixture.dataSource.deleteProfile(id) }
    }
}
