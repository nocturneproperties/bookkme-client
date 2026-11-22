package me.bookk.client.di

import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.mock
import io.ktor.client.HttpClient
import kotlinx.coroutines.flow.emptyFlow
import me.bookk.core.data.HttpClientType
import me.bookk.core.presentation.error.ErrorMapper
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.database.AccountDaos
import me.bookk.database.AppointmentDaos
import me.bookk.database.ClientDaos
import me.bookk.database.dao.AppointmentDao
import me.bookk.database.dao.AppointmentRequestDao
import me.bookk.database.dao.AppointmentSettingsDao
import me.bookk.database.dao.BusinessDao
import me.bookk.database.dao.BusinessDataExportDao
import me.bookk.database.dao.LegalDocumentStatusDao
import me.bookk.database.dao.NotificationSettingsDao
import me.bookk.database.dao.PersonalDataExportDao
import me.bookk.database.dao.SavedBusinessDao
import me.bookk.database.dao.UserProfileDao
import me.bookk.feature.appointments.domain.api.SaveBusiness
import me.bookk.feature.authorization.domain.api.GetTokenInfo
import me.bookk.feature.authorization.domain.api.LogOut
import me.bookk.feature.authorization.domain.api.RefreshToken
import me.bookk.feature.authorization.domain.api.SignInWithTelegram
import me.bookk.feature.authorization.presentation.screen.clientbootstrap.ClientBootstrapViewModel
import me.bookk.feature.authorization.presentation.screen.telegram.TelegramSignInViewModel
import me.bookk.feature.business.domain.api.business.GetBusinesses
import me.bookk.feature.dashboard.presentation.screen.clienthome.ClientHomeViewModel
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
        loadKoinModules(
            module {
                single<AccountDaos> { FakeAccountDaos() }
                single<ClientDaos> { FakeClientDaos() }
                single<AppointmentDaos> { FakeAppointmentDaos() }
            },
        )
    }

    private class FakeClientDaos : ClientDaos {
        override fun businessDao(): BusinessDao {
            return mock()
        }

        override fun businessDataExportDao(): BusinessDataExportDao {
            return mock()
        }

        override fun savedBusinessDao(): SavedBusinessDao {
            return mock {
                every { observeAll() } returns emptyFlow()
            }
        }
    }

    private class FakeAppointmentDaos : AppointmentDaos {
        override fun appointmentDao(): AppointmentDao {
            return mock {
                every { observeScheduled() } returns emptyFlow()
            }
        }

        override fun appointmentRequestDao(): AppointmentRequestDao {
            return mock {
                every { observePending() } returns emptyFlow()
            }
        }

        override fun appointmentSettingsDao(): AppointmentSettingsDao {
            return mock()
        }
    }

    private class FakeAccountDaos : AccountDaos {
        override fun profileDao(): UserProfileDao {
            return mock {
                every { observeProfile() } returns emptyFlow()
            }
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
    fun `provides the telegram bootstrap view model`() = runUnitTest {
        given()
        startClientApp()

        whenn()
        val viewModel = KoinPlatform.getKoin().getOrNull<ClientBootstrapViewModel>()

        then()
        assertNotNull(viewModel)
        viewModel.onCleared()
    }

    @Test
    fun `keeps the started graph when the page opens again`() = runUnitTest {
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

    @Test
    fun `provides the client home view model for the launch parameter`() = runUnitTest {
        given()
        startClientApp()

        whenn()
        val viewModel = KoinPlatform.getKoin().getOrNull<ClientHomeViewModel> { parametersOf("") }

        then()
        assertNotNull(viewModel)
        viewModel.onCleared()
    }

    @Test
    fun `provides saving the business a client opened`() = runUnitTest {
        given()
        startClientApp()

        whenn()
        val saveBusiness = KoinPlatform.getKoin().getOrNull<SaveBusiness>()

        then()
        assertNotNull(saveBusiness)
    }

    @Test
    fun `provides the business reader the client home names businesses with`() = runUnitTest {
        given()
        startClientApp()

        whenn()
        val getBusinesses = KoinPlatform.getKoin().getOrNull<GetBusinesses>()

        then()
        assertNotNull(getBusinesses)
    }
}
