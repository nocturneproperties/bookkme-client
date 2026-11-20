package me.bookk.site.di

import dev.mokkery.mock
import io.ktor.client.HttpClient
import me.bookk.core.data.HttpClientType
import me.bookk.core.presentation.error.ErrorMapper
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.database.account.AccountDatabase
import me.bookk.database.account.dao.LegalDocumentStatusDao
import me.bookk.database.account.dao.NotificationSettingsDao
import me.bookk.database.account.dao.PersonalDataExportDao
import me.bookk.database.account.dao.UserProfileDao
import me.bookk.feature.authorization.domain.api.GetTokenInfo
import me.bookk.feature.authorization.domain.api.LogOut
import me.bookk.feature.authorization.domain.api.RefreshToken
import me.bookk.feature.authorization.domain.api.SignInWithTelegram
import me.bookk.feature.authorization.presentation.screen.telegram.TelegramSignInViewModel
import org.koin.core.context.loadKoinModules
import org.koin.core.context.stopKoin
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named
import org.koin.dsl.module
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

    private fun startClientApp() {
        initClientApp()
        loadKoinModules(module { single<AccountDatabase> { FakeAccountDatabase() } })
    }

    private class FakeAccountDatabase : AccountDatabase {
        override fun profileDao(): UserProfileDao {
            return mock()
        }

        override fun notificationSettingsDao(): NotificationSettingsDao {
            return mock()
        }

        override fun legalDocumentStatusDao(): LegalDocumentStatusDao {
            return mock()
        }

        override fun personalDataExportDao(): PersonalDataExportDao {
            return mock()
        }
    }

    @Test
    fun `provides the authenticated http client`() = runUnitTest {
        given()
        startClientApp()

        whenn()
        val client = KoinPlatform.getKoin().getOrNull<HttpClient>()

        then()
        assertNotNull(client)
    }

    @Test
    fun `provides the unauthenticated http client`() = runUnitTest {
        given()
        startClientApp()

        whenn()
        val client = KoinPlatform.getKoin().getOrNull<HttpClient>(named(HttpClientType.NO_AUTH))

        then()
        assertNotNull(client)
    }

    @Test
    fun `provides the token reader the client authenticates with`() = runUnitTest {
        given()
        startClientApp()

        whenn()
        val getTokenInfo = KoinPlatform.getKoin().getOrNull<GetTokenInfo>()

        then()
        assertNotNull(getTokenInfo)
    }

    @Test
    fun `provides the token refresher the client recovers expired sessions with`() = runUnitTest {
        given()
        startClientApp()

        whenn()
        val refreshToken = KoinPlatform.getKoin().getOrNull<RefreshToken>()

        then()
        assertNotNull(refreshToken)
    }

    @Test
    fun `provides telegram sign-in with everything it fetches after signing in`() = runUnitTest {
        given()
        startClientApp()

        whenn()
        val signInWithTelegram = KoinPlatform.getKoin().getOrNull<SignInWithTelegram>()

        then()
        assertNotNull(signInWithTelegram)
    }

    @Test
    fun `provides log out with every logout action`() = runUnitTest {
        given()
        startClientApp()

        whenn()
        val logOut = KoinPlatform.getKoin().getOrNull<LogOut>()

        then()
        assertNotNull(logOut)
    }

    @Test
    fun `provides the telegram sign-in view model for the launch data`() = runUnitTest {
        given()
        startClientApp()

        whenn()
        val viewModel = KoinPlatform.getKoin().getOrNull<TelegramSignInViewModel> {
            parametersOf("query_id=AAE&hash=abc")
        }

        then()
        assertNotNull(viewModel)
        viewModel.onCleared()
    }

    @Test
    fun `keeps the started graph when the telegram page opens again`() = runUnitTest {
        given()
        startClientApp()
        val client = KoinPlatform.getKoin().get<HttpClient>()

        whenn()
        startClientApp()

        then()
        assertSame(client, KoinPlatform.getKoin().get<HttpClient>())
    }

    @Test
    fun `provides the error mapper for presenting failures`() = runUnitTest {
        given()
        startClientApp()

        whenn()
        val errorMapper = KoinPlatform.getKoin().getOrNull<ErrorMapper>()

        then()
        assertNotNull(errorMapper)
    }
}
