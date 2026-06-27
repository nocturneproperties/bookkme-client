package me.bookk.feature.authorization.domain.impl

import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.mock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.feature.authorization.domain.datasource.authorization.AuthorizationDataSource
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class IsUserLoggedInImplTest {

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
        val dataSource = mock<AuthorizationDataSource>()
        val sut = IsUserLoggedInImpl(dataSource)
    }

    @Test
    fun `returns true when access token is present`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.dataSource.getAccessToken() } returns "access-token"

        whenn()
        val result = fixture.sut()

        then()
        assertTrue(result)
    }

    @Test
    fun `returns false when access token is null`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.dataSource.getAccessToken() } returns null

        whenn()
        val result = fixture.sut()

        then()
        assertFalse(result)
    }

    @Test
    fun `asFlow emits value from getIsAuthorizedFlow`() = runUnitTest {
        given()
        val fixture = Fixture()
        every { fixture.dataSource.getIsAuthorizedFlow() } returns flowOf(true)

        whenn()
        val result = fixture.sut.asFlow().first()

        then()
        assertTrue(result)
    }
}
