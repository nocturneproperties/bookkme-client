package me.bookk.feature.authorization.domain.impl

import dev.mokkery.answering.returns
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
        val dataSource = mock<AuthorizationDataSource>()
        val sut = RefreshTokenImpl(dataSource)
    }

    @Test
    fun `returns new token info`() = runUnitTest {
        given()
        val fixture = Fixture()
        val refreshToken = "old-refresh"
        val newToken = TokenInfo("new-access", "new-refresh")
        everySuspend { fixture.dataSource.refreshToken(refreshToken) } returns newToken
        everySuspend { fixture.dataSource.saveAuthorizationTokens(newToken) } returns Unit

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
        everySuspend { fixture.dataSource.refreshToken(refreshToken) } returns newToken
        everySuspend { fixture.dataSource.saveAuthorizationTokens(newToken) } returns Unit

        whenn()
        fixture.sut(refreshToken)

        then()
        verifySuspend { fixture.dataSource.saveAuthorizationTokens(newToken) }
    }
}
