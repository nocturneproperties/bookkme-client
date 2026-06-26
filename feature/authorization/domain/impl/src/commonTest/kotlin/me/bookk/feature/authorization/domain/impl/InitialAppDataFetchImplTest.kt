package me.bookk.feature.authorization.domain.impl

import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
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
import me.bookk.feature.business.domain.api.business.RefreshBusinessInfo
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class InitialAppDataFetchImplTest {

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
        val userProfileCRUD = mock<UserProfileCRUD>()
        val refreshBusiness = mock<RefreshBusinessInfo>()
        val sut = InitialAppDataFetchImpl(userProfileCRUD, refreshBusiness)
    }

    @Test
    fun `calls updateFromRemote on user profile`() = runUnitTest {
        given()
        val sut = Fixture()
        everySuspend { sut.userProfileCRUD.updateFromRemote() } returns Unit
        everySuspend { sut.refreshBusiness() } returns Unit

        whenn()
        sut.sut()

        then()
        verifySuspend { sut.userProfileCRUD.updateFromRemote() }
    }

    @Test
    fun `calls refreshBusiness`() = runUnitTest {
        given()
        val sut = Fixture()
        everySuspend { sut.userProfileCRUD.updateFromRemote() } returns Unit
        everySuspend { sut.refreshBusiness() } returns Unit

        whenn()
        sut.sut()

        then()
        verifySuspend { sut.refreshBusiness() }
    }

    @Test
    fun `completes even when refreshBusiness throws`() = runUnitTest {
        given()
        val sut = Fixture()
        everySuspend { sut.userProfileCRUD.updateFromRemote() } returns Unit
        everySuspend { sut.refreshBusiness() } throws RuntimeException("network error")

        whenn()
        sut.sut()

        then()
        verifySuspend { sut.userProfileCRUD.updateFromRemote() }
    }
}
