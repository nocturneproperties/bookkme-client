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
import me.bookk.feature.authorization.domain.datasource.authorization.AuthorizationDataSource
import me.bookk.feature.authorization.domain.entity.TokenInfo
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class RefreshTokenImplTest {

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
        val dataSource = mockk<AuthorizationDataSource>()
        val sut = RefreshTokenImpl(dataSource)
    }

    @Test
    fun `returns new token info`() = runUnitTest {
        given()
        val fixture = Fixture()
        val refreshToken = "old-refresh"
        val newToken = TokenInfo("new-access", "new-refresh")
        coEvery { fixture.dataSource.refreshToken(refreshToken) } returns newToken
        coJustRun { fixture.dataSource.saveAuthorizationTokens(newToken) }

        whenn()
        val result = fixture.sut(refreshToken)

        then()
        assertEquals(newToken, result)
    }

    @Test
    fun `saves new tokens after refreshing`() = runUnitTest {
        given()
        val fixture = Fixture()
        val refreshToken = "old-refresh"
        val newToken = TokenInfo("new-access", "new-refresh")
        coEvery { fixture.dataSource.refreshToken(refreshToken) } returns newToken
        coJustRun { fixture.dataSource.saveAuthorizationTokens(newToken) }

        whenn()
        fixture.sut(refreshToken)

        then()
        coVerify { fixture.dataSource.saveAuthorizationTokens(newToken) }
    }
}
