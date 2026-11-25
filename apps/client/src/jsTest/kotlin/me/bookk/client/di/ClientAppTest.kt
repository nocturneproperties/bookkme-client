package me.bookk.client.di

import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.matcher.any
import dev.mokkery.mock
import io.ktor.client.HttpClient
import kotlinx.coroutines.flow.emptyFlow
import me.bookk.core.data.HttpClientType
import me.bookk.core.presentation.error.ErrorMapper
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.database.ClientDaos
import me.bookk.database.DatabaseStorage
import me.bookk.database.dao.AppointmentDao
import me.bookk.database.dao.AppointmentRequestDao
import me.bookk.database.dao.AppointmentSettingsDao
import me.bookk.database.dao.BookableEmployeeDao
import me.bookk.database.dao.BookingInfoDao
import me.bookk.database.dao.BusinessDao
import me.bookk.database.dao.BusinessDataExportDao
import me.bookk.database.dao.LegalDocumentStatusDao
import me.bookk.database.dao.NotificationSettingsDao
import me.bookk.database.dao.PersonalDataExportDao
import me.bookk.database.dao.SavedBusinessDao
import me.bookk.database.dao.ServiceDao
import me.bookk.database.dao.ServiceGroupDao
import me.bookk.database.dao.UserProfileDao
import me.bookk.feature.appointments.domain.api.SaveBusiness
import me.bookk.feature.authorization.domain.api.GetTokenInfo
import me.bookk.feature.authorization.domain.api.LogOut
import me.bookk.feature.authorization.domain.api.RefreshToken
import me.bookk.feature.authorization.domain.api.SignInWithTelegram
import me.bookk.feature.authorization.presentation.screen.clientbootstrap.ClientBootstrapViewModel
import me.bookk.feature.authorization.presentation.screen.telegram.TelegramSignInViewModel
import me.bookk.feature.booking.domain.api.GetBookingAppointmentDetails
import me.bookk.feature.booking.domain.api.GetBookingCatalog
import me.bookk.feature.booking.domain.api.GetBookingPreparation
import me.bookk.feature.booking.domain.api.GetBookingRequestDetails
import me.bookk.feature.booking.domain.api.GetBookingSlots
import me.bookk.feature.booking.domain.api.GetBookingSpecialists
import me.bookk.feature.booking.domain.api.GetBookingTimetable
import me.bookk.feature.booking.domain.api.GetBusinessMessage
import me.bookk.feature.booking.domain.api.GetSentBooking
import me.bookk.feature.booking.domain.api.SendBookingRequest
import me.bookk.feature.booking.domain.api.SetBookingServices
import me.bookk.feature.booking.domain.api.SetBookingSlot
import me.bookk.feature.booking.domain.api.SetBookingSpecialist
import me.bookk.feature.booking.domain.api.WithdrawBookingRequest
import me.bookk.feature.booking.domain.api.entity.SentBookingRequest
import me.bookk.feature.booking.domain.datasource.BookingPreparationDataSource
import me.bookk.feature.booking.presentation.screen.datetime.BookingDateTimeViewModel
import me.bookk.feature.booking.presentation.screen.details.BookingAppointmentDetailsViewModel
import me.bookk.feature.booking.presentation.screen.details.BookingRequestDetailsViewModel
import me.bookk.feature.booking.presentation.screen.review.BookingReviewViewModel
import me.bookk.feature.booking.presentation.screen.sent.BookingSentViewModel
import me.bookk.feature.booking.presentation.screen.services.BookingServicesViewModel
import me.bookk.feature.booking.presentation.screen.specialist.BookingSpecialistViewModel
import me.bookk.feature.business.domain.api.business.GetBusinesses
import me.bookk.feature.business.domain.api.link.ResolveBusinessLink
import me.bookk.feature.dashboard.presentation.screen.clienthome.ClientHomeViewModel
import me.bookk.feature.employees.domain.api.GetBookableEmployees
import me.bookk.feature.services.domain.api.service.GetBusinessServices
import me.bookk.feature.settings.presentation.dashboard.SettingsDashboardViewModel
import me.bookk.feature.settings.presentation.privacy.PrivacySettingsViewModel
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
import kotlin.time.Instant
import kotlin.uuid.Uuid

class ClientAppTest {

    @AfterTest
    fun tearDown() {
        stopKoin()
    }

    private fun startClientApp() {
        initClientApp()
        loadKoinModules(
            module {
                single<ClientDaos> { FakeClientDaos() }
                single<DatabaseStorage> { FakeDatabaseStorage() }
            },
        )
    }

    private class FakeDatabaseStorage : DatabaseStorage {
        override suspend fun isPersistent(): Boolean {
            return true
        }
    }

    private class FakeClientDaos : ClientDaos {
        override fun profileDao(): UserProfileDao {
            return mock {
                every { observeProfile() } returns emptyFlow()
            }
        }

        override fun notificationSettingsDao(): NotificationSettingsDao {
            return mock()
        }

        override fun legalDocumentStatusDao(): LegalDocumentStatusDao {
            return mock {
                every { observeAll() } returns emptyFlow()
            }
        }

        override fun personalDataExportDao(): PersonalDataExportDao {
            return mock()
        }

        override fun businessDao(): BusinessDao {
            return mock {
                every { observeBusinesses(any()) } returns emptyFlow()
            }
        }

        override fun businessDataExportDao(): BusinessDataExportDao {
            return mock()
        }

        override fun savedBusinessDao(): SavedBusinessDao {
            return mock {
                every { observeAll() } returns emptyFlow()
            }
        }

        override fun serviceDao(): ServiceDao {
            return mock {
                every { observe(any()) } returns emptyFlow()
            }
        }

        override fun serviceGroupDao(): ServiceGroupDao {
            return mock()
        }

        override fun appointmentDao(): AppointmentDao {
            return mock {
                every { observeScheduled() } returns emptyFlow()
                every { observeById(any()) } returns emptyFlow()
            }
        }

        override fun appointmentRequestDao(): AppointmentRequestDao {
            return mock {
                every { observePending() } returns emptyFlow()
                every { observeById(any()) } returns emptyFlow()
            }
        }

        override fun appointmentSettingsDao(): AppointmentSettingsDao {
            return mock()
        }

        override fun bookableEmployeeDao(): BookableEmployeeDao {
            return mock {
                every { observeEmployees(any()) } returns emptyFlow()
            }
        }

        override fun bookingInfoDao(): BookingInfoDao {
            return mock {
                every { observe(any()) } returns emptyFlow()
            }
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

    @Test
    fun `provides resolving the business link a client opened`() = runUnitTest {
        given()
        startClientApp()

        whenn()
        val resolveBusinessLink = KoinPlatform.getKoin().getOrNull<ResolveBusinessLink>()

        then()
        assertNotNull(resolveBusinessLink)
    }

    @Test
    fun `provides the business services the services step lists`() = runUnitTest {
        given()
        startClientApp()

        whenn()
        val getBusinessServices = KoinPlatform.getKoin().getOrNull<GetBusinessServices>()

        then()
        assertNotNull(getBusinessServices)
    }

    @Test
    fun `provides the booking catalog the services step lists`() = runUnitTest {
        given()
        startClientApp()

        whenn()
        val getBookingCatalog = KoinPlatform.getKoin().getOrNull<GetBookingCatalog>()

        then()
        assertNotNull(getBookingCatalog)
    }

    @Test
    fun `provides the booking preparation the services step saves into`() = runUnitTest {
        given()
        startClientApp()

        whenn()
        val koin = KoinPlatform.getKoin()
        val useCases = listOf(
            koin.getOrNull<GetBookingPreparation>(),
            koin.getOrNull<SetBookingServices>(),
        )

        then()
        useCases.forEach { assertNotNull(it) }
    }

    @Test
    fun `keeps one booking preparation for every screen of the app`() = runUnitTest {
        given()
        startClientApp()
        val first = KoinPlatform.getKoin().get<BookingPreparationDataSource>()

        whenn()
        val second = KoinPlatform.getKoin().get<BookingPreparationDataSource>()

        then()
        assertSame(first, second)
    }

    @Test
    fun `provides the services view model for the opened business`() = runUnitTest {
        given()
        startClientApp()

        whenn()
        val viewModel = KoinPlatform.getKoin().getOrNull<BookingServicesViewModel> {
            parametersOf(Uuid.random())
        }

        then()
        assertNotNull(viewModel)
        viewModel.onCleared()
    }

    @Test
    fun `provides the bookable employees the specialist step lists`() = runUnitTest {
        given()
        startClientApp()

        whenn()
        val getBookableEmployees = KoinPlatform.getKoin().getOrNull<GetBookableEmployees>()

        then()
        assertNotNull(getBookableEmployees)
    }

    @Test
    fun `provides the booking specialists the specialist step lists and saves`() = runUnitTest {
        given()
        startClientApp()

        whenn()
        val koin = KoinPlatform.getKoin()
        val useCases = listOf(
            koin.getOrNull<GetBookingSpecialists>(),
            koin.getOrNull<SetBookingSpecialist>(),
        )

        then()
        useCases.forEach { assertNotNull(it) }
    }

    @Test
    fun `provides the specialist view model for the opened business`() = runUnitTest {
        given()
        startClientApp()

        whenn()
        val viewModel = KoinPlatform.getKoin().getOrNull<BookingSpecialistViewModel> {
            parametersOf(Uuid.random())
        }

        then()
        assertNotNull(viewModel)
        viewModel.onCleared()
    }

    @Test
    fun `provides the timetable and free slots the date and time step shows and saves`() =
        runUnitTest {
            given()
            startClientApp()

            whenn()
            val koin = KoinPlatform.getKoin()
            val useCases = listOf(
                koin.getOrNull<GetBookingTimetable>(),
                koin.getOrNull<GetBookingSlots>(),
                koin.getOrNull<SetBookingSlot>(),
            )

            then()
            useCases.forEach { assertNotNull(it) }
        }

    @Test
    fun `provides the date and time view model for the opened business`() = runUnitTest {
        given()
        startClientApp()

        whenn()
        val viewModel = KoinPlatform.getKoin().getOrNull<BookingDateTimeViewModel> {
            parametersOf(Uuid.random())
        }

        then()
        assertNotNull(viewModel)
        viewModel.onCleared()
    }

    @Test
    fun `provides the business message and sending the request the review step uses`() =
        runUnitTest {
            given()
            startClientApp()

            whenn()
            val koin = KoinPlatform.getKoin()
            val useCases = listOf(
                koin.getOrNull<GetBusinessMessage>(),
                koin.getOrNull<SendBookingRequest>(),
            )

            then()
            useCases.forEach { assertNotNull(it) }
        }

    @Test
    fun `provides the review view model for the opened business`() = runUnitTest {
        given()
        startClientApp()

        whenn()
        val viewModel = KoinPlatform.getKoin().getOrNull<BookingReviewViewModel> {
            parametersOf(Uuid.random())
        }

        then()
        assertNotNull(viewModel)
        viewModel.onCleared()
    }

    @Test
    fun `provides the sent request the request sent step shows`() = runUnitTest {
        given()
        startClientApp()

        whenn()
        val useCase = KoinPlatform.getKoin().getOrNull<GetSentBooking>()

        then()
        assertNotNull(useCase)
    }

    @Test
    fun `provides the request sent view model for the sent request`() = runUnitTest {
        given()
        startClientApp()
        val request = SentBookingRequest(Uuid.random(), Uuid.random(), Instant.fromEpochSeconds(0))

        whenn()
        val viewModel = KoinPlatform.getKoin().getOrNull<BookingSentViewModel> {
            parametersOf(request)
        }

        then()
        assertNotNull(viewModel)
        viewModel.onCleared()
    }

    @Test
    fun `provides the request, the appointment and the withdrawal the details screens use`() =
        runUnitTest {
            given()
            startClientApp()

            whenn()
            val koin = KoinPlatform.getKoin()
            val useCases = listOf(
                koin.getOrNull<GetBookingRequestDetails>(),
                koin.getOrNull<GetBookingAppointmentDetails>(),
                koin.getOrNull<WithdrawBookingRequest>(),
            )

            then()
            useCases.forEach { assertNotNull(it) }
        }

    @Test
    fun `provides the request details view model for the opened request`() = runUnitTest {
        given()
        startClientApp()

        whenn()
        val viewModel = KoinPlatform.getKoin().getOrNull<BookingRequestDetailsViewModel> {
            parametersOf(Uuid.random())
        }

        then()
        assertNotNull(viewModel)
        viewModel.onCleared()
    }

    @Test
    fun `provides the appointment details view model for the opened appointment`() = runUnitTest {
        given()
        startClientApp()

        whenn()
        val viewModel = KoinPlatform.getKoin().getOrNull<BookingAppointmentDetailsViewModel> {
            parametersOf(Uuid.random())
        }

        then()
        assertNotNull(viewModel)
        viewModel.onCleared()
    }

    @Test
    fun `provides the settings view model`() = runUnitTest {
        given()
        startClientApp()

        whenn()
        val viewModel = KoinPlatform.getKoin().getOrNull<SettingsDashboardViewModel>()

        then()
        assertNotNull(viewModel)
        viewModel.onCleared()
    }

    @Test
    fun `provides the privacy settings view model`() = runUnitTest {
        given()
        startClientApp()

        whenn()
        val viewModel = KoinPlatform.getKoin().getOrNull<PrivacySettingsViewModel>()

        then()
        assertNotNull(viewModel)
        viewModel.onCleared()
    }
}
