package me.bookk.feature.authorization.domain.impl

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
import me.bookk.feature.authorization.domain.datasource.authorization.AuthorizationDataSource
import me.bookk.feature.authorization.domain.entity.TokenInfo
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
class GetTokenInfoImplTest {

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
        val sut = GetTokenInfoImpl(dataSource)
    }

    @Test
    fun `returns TokenInfo when both tokens are present`() = runUnitTest {
        given()
        val sut = Fixture()
        everySuspend { sut.dataSource.getAccessToken() } returns "access-token"
        everySuspend { sut.dataSource.getRefreshToken() } returns "refresh-token"

        whenn()
        val result = sut.sut()

        then()
        assertEquals(TokenInfo("access-token", "refresh-token"), result)
    }

    @Test
    fun `returns null when access token is missing`() = runUnitTest {
        given()
        val sut = Fixture()
        everySuspend { sut.dataSource.getAccessToken() } returns null
        everySuspend { sut.dataSource.getRefreshToken() } returns "refresh-token"

        whenn()
        val result = sut.sut()

        then()
        assertNull(result)
    }

    @Test
    fun `returns null when refresh token is missing`() = runUnitTest {
        given()
        val sut = Fixture()
        everySuspend { sut.dataSource.getAccessToken() } returns "access-token"
        everySuspend { sut.dataSource.getRefreshToken() } returns null

        whenn()
        val result = sut.sut()

        then()
        assertNull(result)
    }

    @Test
    fun `returns null when both tokens are missing`() = runUnitTest {
        given()
        val sut = Fixture()
        everySuspend { sut.dataSource.getAccessToken() } returns null
        everySuspend { sut.dataSource.getRefreshToken() } returns null

        whenn()
        val result = sut.sut()

        then()
        assertNull(result)
    }
}
