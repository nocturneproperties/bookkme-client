package me.bookk.site.di

import io.ktor.client.HttpClient
import me.bookk.core.data.HttpClientType
import me.bookk.core.presentation.error.ErrorMapper
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.feature.authorization.domain.api.GetTokenInfo
import me.bookk.feature.authorization.domain.api.RefreshToken
import org.koin.core.context.stopKoin
import org.koin.core.qualifier.named
import org.koin.mp.KoinPlatform
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertSame

class ClientAppTest {

    @AfterTest
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun `provides the authenticated http client`() = runUnitTest {
        given()
        initClientApp()

        whenn()
        val client = KoinPlatform.getKoin().getOrNull<HttpClient>()

        then()
        assertNotNull(client)
    }

    @Test
    fun `provides the unauthenticated http client`() = runUnitTest {
        given()
        initClientApp()

        whenn()
        val client = KoinPlatform.getKoin().getOrNull<HttpClient>(named(HttpClientType.NO_AUTH))

        then()
        assertNotNull(client)
    }

    @Test
    fun `provides the token reader the client authenticates with`() = runUnitTest {
        given()
        initClientApp()

        whenn()
        val getTokenInfo = KoinPlatform.getKoin().getOrNull<GetTokenInfo>()

        then()
        assertNotNull(getTokenInfo)
    }

    @Test
    fun `provides the token refresher the client recovers expired sessions with`() = runUnitTest {
        given()
        initClientApp()

        whenn()
        val refreshToken = KoinPlatform.getKoin().getOrNull<RefreshToken>()

        then()
        assertNotNull(refreshToken)
    }

    @Test
    fun `keeps the started graph when the telegram page opens again`() = runUnitTest {
        given()
        initClientApp()
        val client = KoinPlatform.getKoin().get<HttpClient>()

        whenn()
        initClientApp()

        then()
        assertSame(client, KoinPlatform.getKoin().get<HttpClient>())
    }

    @Test
    fun `provides the error mapper for presenting failures`() = runUnitTest {
        given()
        initClientApp()

        whenn()
        val errorMapper = KoinPlatform.getKoin().getOrNull<ErrorMapper>()

        then()
        assertNotNull(errorMapper)
    }
}
