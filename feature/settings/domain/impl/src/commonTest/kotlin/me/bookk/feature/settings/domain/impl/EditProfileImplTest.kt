package me.bookk.feature.settings.domain.impl

import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.matcher.matches
import dev.mokkery.mock
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
import me.bookk.feature.authorization.domain.api.UserProfileCRUD
import me.bookk.feature.authorization.domain.entity.UserProfile
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.uuid.Uuid

@OptIn(ExperimentalCoroutinesApi::class)
class EditProfileImplTest {

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
        val profileCRUD = mock<UserProfileCRUD>()
        val sut = EditProfileImpl(profileCRUD)
    }

    @Test
    fun `calls update with merged profile fields`() = runUnitTest {
        given()
        val fixture = Fixture()
        val existing = UserProfile(id = Uuid.random(), firstName = "Old", lastName = "Name", email = "old@example.com")
        everySuspend { fixture.profileCRUD.get() } returns existing
        everySuspend { fixture.profileCRUD.update(any()) } returns Unit

        whenn()
        fixture.sut("New", "Lastname", "new@example.com")

        then()
        verifySuspend {
            fixture.profileCRUD.update(
                matches({ "match" }) { it.firstName == "New" && it.lastName == "Lastname" && it.email == "new@example.com" }
            )
        }
    }

    @Test
    fun `preserves profile id when updating`() = runUnitTest {
        given()
        val fixture = Fixture()
        val id = Uuid.random()
        val existing = UserProfile(id = id, firstName = "Old", lastName = "Name", email = "old@e.com")
        everySuspend { fixture.profileCRUD.get() } returns existing
        everySuspend { fixture.profileCRUD.update(any()) } returns Unit

        whenn()
        fixture.sut("A", "B", "a@b.com")

        then()
        verifySuspend { fixture.profileCRUD.update(matches({ "match" }) { it.id == id }) }
    }
}
