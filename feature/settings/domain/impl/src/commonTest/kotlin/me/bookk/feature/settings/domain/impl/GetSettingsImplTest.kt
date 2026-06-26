package me.bookk.feature.settings.domain.impl

import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
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
import me.bookk.feature.settings.domain.api.GetColorScheme
import me.bookk.feature.settings.domain.api.entity.ColorScheme
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.uuid.Uuid

@OptIn(ExperimentalCoroutinesApi::class)
class GetSettingsImplTest {

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
        val getColorScheme = mock<GetColorScheme>()
        val userProfileCRUD = mock<UserProfileCRUD>()
        val sut = GetSettingsImpl(getColorScheme, userProfileCRUD)
    }

    private fun stubProfile() = UserProfile(
        id = Uuid.random(), firstName = "John", lastName = "Doe", email = "john@example.com"
    )

    @Test
    fun `returns settings combining color scheme and profile`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.getColorScheme() } returns ColorScheme.DARK
        everySuspend { fixture.userProfileCRUD.get() } returns stubProfile()

        whenn()
        val result = fixture.sut()

        then()
        assertEquals(ColorScheme.DARK, result.colorScheme)
        assertEquals("John", result.profile.firstName)
        assertEquals("Doe", result.profile.lastName)
        assertEquals("john@example.com", result.profile.email)
    }
}
