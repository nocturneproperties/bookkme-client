package me.bookk.feature.settings.presentation.export

import dev.icerock.moko.resources.desc.desc
import dev.icerock.moko.resources.format
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import me.bookk.android.designsystem.resources.DesignSystem
import me.bookk.android.feature.settings.resources.SettingsRes
import me.bookk.core.domain.entity.DataExport
import me.bookk.core.domain.entity.DataExportStatus
import me.bookk.core.domain.entity.Error
import me.bookk.core.presentation.VmArgs
import me.bookk.core.presentation.error.PresentationNotification
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.designsystem.resources.color.ColorToken
import me.bookk.designsystem.test.FakeDateLocalizer
import me.bookk.designsystem.test.FakeErrorMapper
import me.bookk.designsystem.test.TestException
import me.bookk.designsystem.test.ViewModelTestDispatchers
import me.bookk.designsystem.test.failOnceThenSuspend
import me.bookk.feature.settings.domain.api.DownloadPersonalDataExport
import me.bookk.feature.settings.domain.api.StartPersonalDataExport
import me.bookk.feature.settings.presentation.FakeSettingsStateFactory
import okio.Path
import okio.Path.Companion.toPath
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Instant

class PersonalDataExportViewModelTest {
    private val dispatchers = ViewModelTestDispatchers()

    @BeforeTest
    fun setUp() {
        dispatchers.install()
    }

    @AfterTest
    fun tearDown() {
        dispatchers.uninstall()
    }

    private class Fixture {
        val now = Instant.parse("2026-11-05T12:00:00Z")
        val cache = FakeExportCache()
        val getExport = FakeGetPersonalDataExport(cache)
        val startExport = FakeStartPersonalDataExport(cache)
        val file = "/cache/personal_data_exports/1-2/bookk-personal-data.json".toPath()
        val downloadExport = FakeDownloadPersonalDataExport { file }
        val errorMapper = FakeErrorMapper()

        fun sut(): PersonalDataExportViewModel {
            return PersonalDataExportViewModel(
                getExport = getExport,
                startExport = startExport,
                downloadExport = downloadExport,
                clock = object : Clock {
                    override fun now(): Instant {
                        return now
                    }
                },
                dateLocalizer = FakeDateLocalizer(),
                stateFactory = FakeSettingsStateFactory(),
                vmArgs = VmArgs(errorMapper),
            )
        }

        fun share(file: Path = this.file): PersonalDataExportDestinations {
            return PersonalDataExportDestinations.Share(file.toString())
        }

        fun ready(expiresAt: Instant? = null, availableAgainAt: Instant? = null): DataExport {
            return DataExport(DataExportStatus.READY, now, expiresAt, availableAgainAt)
        }

        fun runningWithPendingRefresh(): CompletableDeferred<DataExport> {
            val response = CompletableDeferred<DataExport>()
            cache.value = DataExport(DataExportStatus.RUNNING)
            getExport.server = { response.await() }
            return response
        }
    }

    @Test
    fun `observes the export cache once`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        fixture.sut()

        then()
        assertEquals(1, fixture.getExport.flowCalls)
    }

    @Test
    fun `refreshes once on open when nothing is cached`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        fixture.sut()

        then()
        assertEquals(1, fixture.getExport.refreshCalls)
    }

    @Test
    fun `refreshes on open when the cached export is running`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.cache.value = DataExport(DataExportStatus.RUNNING)

        whenn()
        fixture.sut()

        then()
        assertEquals(1, fixture.getExport.refreshCalls)
    }

    @Test
    fun `does not refresh on open when the cached export is settled`() = runUnitTest {
        given()
        val fixtures = listOf(
            DataExport(DataExportStatus.READY),
            DataExport(DataExportStatus.FAILED),
            DataExport(DataExportStatus.NONE),
            DataExport(
                DataExportStatus.NONE,
                availableAgainAt = Instant.parse("2026-11-06T00:00:00Z"),
            ),
        ).map { export -> Fixture().also { it.cache.value = export } }

        whenn()
        fixtures.forEach { it.sut() }

        then()
        assertEquals(List(fixtures.size) { 0 }, fixtures.map { it.getExport.refreshCalls })
    }

    @Test
    fun `does not refresh again on open when the refreshed export is dropped from the cache`() =
        runUnitTest {
            given()
            val fixture = Fixture()
            fixture.getExport.cachesServerResponse = false
            fixture.getExport.server = {
                fixture.cache.value = null
                fixture.ready(expiresAt = fixture.now)
            }

            whenn()
            fixture.sut()

            then()
            assertEquals(1, fixture.getExport.refreshCalls)
        }

    @Test
    fun `renders the server status written to an empty cache on open`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.getExport.server = { DataExport(DataExportStatus.RUNNING) }

        whenn()
        val sut = fixture.sut()

        then()
        assertTrue(sut.uiState.status.isInProgress)
        assertFalse(sut.uiState.request.isVisible)
    }

    @Test
    fun `disables request and download while the status refreshes`() = runUnitTest {
        given()
        val fixture = Fixture()
        val response = CompletableDeferred<DataExport>()
        fixture.getExport.server = { response.await() }

        whenn()
        val sut = fixture.sut()
        val requestEnabled = sut.uiState.request.isEnabled
        val downloadEnabled = sut.uiState.download.isEnabled
        response.complete(DataExport(DataExportStatus.NONE))

        then()
        assertFalse(requestEnabled)
        assertFalse(downloadEnabled)
        assertTrue(sut.uiState.request.isEnabled)
        assertTrue(sut.uiState.download.isEnabled)
    }

    @Test
    fun `renders a cached running export while its opening refresh is pending`() = runUnitTest {
        given()
        val fixture = Fixture()
        val response = fixture.runningWithPendingRefresh()

        whenn()
        val sut = fixture.sut()
        val showedPreparing = sut.uiState.status.isInProgress
        val showedLoading = sut.uiState.refresh.isLoading
        response.complete(fixture.ready())

        then()
        assertTrue(showedPreparing)
        assertTrue(showedLoading)
        assertTrue(sut.uiState.download.isVisible)
        assertTrue(sut.uiState.download.isEnabled)
        assertTrue(sut.uiState.request.isEnabled)
        assertFalse(sut.uiState.refresh.isLoading)
        assertFalse(sut.uiState.refresh.isVisible)
        assertEquals(1, fixture.getExport.refreshCalls)
    }

    @Test
    fun `polls a running export after the polling interval`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.cache.value = DataExport(DataExportStatus.RUNNING)
        fixture.sut()

        whenn()
        advanceTimeBy(10.seconds)
        runCurrent()

        then()
        assertEquals(2, fixture.getExport.refreshCalls)
    }

    @Test
    fun `does not poll before the polling interval elapses`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.cache.value = DataExport(DataExportStatus.RUNNING)
        fixture.sut()

        whenn()
        advanceTimeBy(9.seconds)
        runCurrent()

        then()
        assertEquals(1, fixture.getExport.refreshCalls)
    }

    @Test
    fun `stops polling once the export is no longer running`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.cache.value = DataExport(DataExportStatus.RUNNING)
        fixture.sut()
        fixture.getExport.server = { fixture.ready() }
        advanceTimeBy(10.seconds)
        runCurrent()

        whenn()
        advanceTimeBy(60.seconds)
        runCurrent()

        then()
        assertEquals(2, fixture.getExport.refreshCalls)
    }

    @Test
    fun `stops polling a running export after thirty attempts`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.cache.value = DataExport(DataExportStatus.RUNNING)
        fixture.sut()

        whenn()
        advanceTimeBy(10.minutes)
        runCurrent()

        then()
        assertEquals(31, fixture.getExport.refreshCalls)
    }

    @Test
    fun `polls an export that starts running after opening`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.cache.value = DataExport(DataExportStatus.NONE)
        val sut = fixture.sut()
        fixture.cache.value = DataExport(DataExportStatus.RUNNING)
        fixture.getExport.server = { fixture.ready() }

        whenn()
        advanceTimeBy(10.seconds)
        runCurrent()

        then()
        assertTrue(sut.uiState.download.isVisible)
        assertFalse(sut.uiState.status.isInProgress)
        assertEquals(1, fixture.getExport.refreshCalls)
        assertEquals(0, fixture.downloadExport.calls)
    }

    @Test
    fun `renders cache changes without starting another refresh`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.cache.value = DataExport(DataExportStatus.RUNNING)
        val sut = fixture.sut()

        whenn()
        fixture.cache.value = fixture.ready()

        then()
        assertTrue(sut.uiState.download.isVisible)
        assertFalse(sut.uiState.status.isInProgress)
        assertEquals(1, fixture.getExport.refreshCalls)
    }

    @Test
    fun `does not render a refresh result until it reaches the cache`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.cache.value = DataExport(DataExportStatus.RUNNING)
        fixture.getExport.cachesServerResponse = false
        fixture.getExport.server = { fixture.ready() }
        val sut = fixture.sut()

        whenn()
        sut.uiState.refresh.onClick?.invoke()

        then()
        assertTrue(sut.uiState.status.isInProgress)
        assertFalse(sut.uiState.download.isVisible)
    }

    @Test
    fun `does not render a start result until it reaches the cache`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.cache.value = DataExport(DataExportStatus.NONE)
        fixture.startExport.cachesServerResponse = false
        fixture.startExport.server = { fixture.ready() }
        val sut = fixture.sut()

        whenn()
        sut.uiState.request.onClick?.invoke()

        then()
        assertTrue(sut.uiState.request.isVisible)
        assertFalse(sut.uiState.download.isVisible)
    }

    @Test
    fun `drops repeated running observations while a status refresh is active`() = runUnitTest {
        given()
        val fixture = Fixture()
        val response = fixture.runningWithPendingRefresh()
        val sut = fixture.sut()

        whenn()
        fixture.cache.value = DataExport(DataExportStatus.RUNNING, requestedAt = fixture.now)
        fixture.cache.value = DataExport(
            DataExportStatus.RUNNING,
            requestedAt = fixture.now + 1.days,
        )
        response.complete(fixture.ready())

        then()
        assertEquals(1, fixture.getExport.refreshCalls)
        assertTrue(sut.uiState.download.isVisible)
    }

    @Test
    fun `shows the request screen when the server has no export`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val sut = fixture.sut()

        then()
        assertTrue(sut.uiState.request.isVisible)
        assertTrue(sut.uiState.request.isEnabled)
        assertFalse(sut.uiState.download.isVisible)
        assertFalse(sut.uiState.refresh.isVisible)
        assertFalse(sut.uiState.refresh.isLoading)
        assertEquals(
            SettingsRes.strings.settings_export_request_title.desc(),
            sut.uiState.status.title,
        )
        assertEquals(
            SettingsRes.strings.settings_export_description.desc(),
            sut.uiState.status.message,
        )
        assertNull(sut.uiState.expiry)
        assertEquals(0, fixture.startExport.calls)
    }

    @Test
    fun `shows the request screen for a cached never requested export`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.cache.value = DataExport(DataExportStatus.NONE)

        whenn()
        val sut = fixture.sut()

        then()
        assertTrue(sut.uiState.request.isVisible)
        assertFalse(sut.uiState.refresh.isVisible)
        assertEquals(
            SettingsRes.strings.settings_export_description.desc(),
            sut.uiState.status.message,
        )
    }

    @Test
    fun `request screen shows the document icon and passkey footer`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val sut = fixture.sut()

        then()
        assertEquals(DesignSystem.images.export_document, sut.uiState.status.icon)
        assertEquals(
            SettingsRes.strings.settings_export_request_footer.desc(),
            sut.uiState.status.footer,
        )
    }

    @Test
    fun `running export shows its message and footer without an icon`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.cache.value = DataExport(DataExportStatus.RUNNING)

        whenn()
        val sut = fixture.sut()

        then()
        assertNull(sut.uiState.status.icon)
        assertEquals(
            SettingsRes.strings.settings_export_running_message.desc(),
            sut.uiState.status.message,
        )
        assertEquals(
            SettingsRes.strings.settings_export_running_footer.desc(),
            sut.uiState.status.footer,
        )
    }

    @Test
    fun `running export offers only the status refresh`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.cache.value = DataExport(DataExportStatus.RUNNING)

        whenn()
        val sut = fixture.sut()

        then()
        assertTrue(sut.uiState.status.isInProgress)
        assertFalse(sut.uiState.request.isVisible)
        assertFalse(sut.uiState.download.isVisible)
        assertTrue(sut.uiState.refresh.isVisible)
        assertTrue(sut.uiState.refresh.isEnabled)
    }

    @Test
    fun `ready export shows the file with success and download icons`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.cache.value = DataExport(DataExportStatus.RUNNING)
        fixture.getExport.server = { fixture.ready() }

        whenn()
        val sut = fixture.sut()

        then()
        assertEquals(DesignSystem.images.export_ready, sut.uiState.status.icon)
        assertEquals(ColorToken.Success, sut.uiState.status.color)
        assertTrue(sut.uiState.status.showsFile)
        assertEquals(DesignSystem.images.download, sut.uiState.download.icon)
        assertNull(sut.uiState.status.message)
        assertEquals(
            SettingsRes.strings.settings_export_ready_message.desc(),
            sut.uiState.status.footer,
        )
        assertTrue(sut.uiState.download.isVisible)
        assertFalse(sut.uiState.refresh.isVisible)
    }

    @Test
    fun `keeps a cached ready export downloadable without refreshing`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.cache.value = fixture.ready(
            expiresAt = fixture.now + 1.days,
            availableAgainAt = fixture.now + 2.days,
        )

        whenn()
        val sut = fixture.sut()

        then()
        assertTrue(sut.uiState.download.isVisible)
        assertTrue(sut.uiState.download.isEnabled)
        assertFalse(sut.uiState.refresh.isVisible)
        assertFalse(sut.uiState.refresh.isLoading)
        assertEquals(0, fixture.getExport.refreshCalls)
    }

    @Test
    fun `shows the backend expiry of a ready export`() = runUnitTest {
        given()
        val fixture = Fixture()
        val expiresAt = Instant.parse("2026-11-06T12:00:00Z")
        fixture.cache.value = fixture.ready(expiresAt, Instant.parse("2026-11-07T12:00:00Z"))

        whenn()
        val sut = fixture.sut()

        then()
        assertEquals(
            expiresAt.toLocalDateTime(TimeZone.currentSystemDefault()).toString().desc(),
            sut.uiState.expiry,
        )
        assertFalse(sut.uiState.request.isVisible)
    }

    @Test
    fun `leaves the expiry empty when the backend sends none`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.cache.value = fixture.ready()

        whenn()
        val sut = fixture.sut()

        then()
        assertNull(sut.uiState.expiry)
        assertTrue(sut.uiState.download.isVisible)
    }

    @Test
    fun `ready and running exports explain that a new request opens after expiry`() = runUnitTest {
        given()
        val fixtures = listOf(
            Fixture().also { it.cache.value = it.ready(expiresAt = it.now + 3.days) },
            Fixture().also {
                it.getExport.server = {
                    DataExport(DataExportStatus.RUNNING, availableAgainAt = it.now + 1.days)
                }
            },
        )

        whenn()
        val availability = fixtures.map { it.sut().uiState.requestAvailability }

        then()
        assertEquals(
            List(2) { SettingsRes.strings.settings_export_request_after_expiry.desc() },
            availability,
        )
    }

    @Test
    fun `blocks a new request during cooldown and shows when it becomes available`() = runUnitTest {
        given()
        val fixture = Fixture()
        val availableAgainAt = Instant.parse("2026-11-06T12:00:00Z")
        fixture.cache.value = DataExport(
            DataExportStatus.NONE,
            availableAgainAt = availableAgainAt,
        )

        whenn()
        val sut = fixture.sut()

        then()
        assertFalse(sut.uiState.request.isVisible)
        assertEquals(
            SettingsRes.strings.settings_export_cooldown.format(
                availableAgainAt.toLocalDateTime(TimeZone.currentSystemDefault()).toString(),
            ),
            sut.uiState.requestAvailability,
        )
        assertTrue(sut.uiState.refresh.isVisible)
        assertTrue(sut.uiState.refresh.isEnabled)
    }

    @Test
    fun `offers a new request at the exact cooldown boundary`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.cache.value = DataExport(DataExportStatus.FAILED, availableAgainAt = fixture.now)

        whenn()
        val sut = fixture.sut()

        then()
        assertTrue(sut.uiState.request.isVisible)
        assertNull(sut.uiState.requestAvailability)
        assertEquals(
            SettingsRes.strings.settings_export_failed_title.desc(),
            sut.uiState.status.title,
        )
    }

    @Test
    fun `restores the request screen when an expired export leaves the cache`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.cache.value = fixture.ready(expiresAt = fixture.now + 1.days)
        val sut = fixture.sut()

        whenn()
        fixture.cache.value = null

        then()
        assertFalse(sut.uiState.download.isVisible)
        assertTrue(sut.uiState.request.isVisible)
        assertFalse(sut.uiState.refresh.isVisible)
        assertNull(sut.uiState.expiry)
        assertNull(sut.uiState.requestAvailability)
        assertEquals(
            SettingsRes.strings.settings_export_request_title.desc(),
            sut.uiState.status.title,
        )
        assertEquals(0, fixture.getExport.refreshCalls)
    }

    @Test
    fun `refresh action turns a running export into a ready download`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.cache.value = DataExport(DataExportStatus.RUNNING)
        val sut = fixture.sut()
        fixture.getExport.server = { fixture.ready() }

        whenn()
        sut.uiState.refresh.onClick?.invoke()

        then()
        assertTrue(sut.uiState.download.isVisible)
        assertFalse(sut.uiState.status.isInProgress)
        assertEquals(0, fixture.downloadExport.calls)
    }

    @Test
    fun `failed status refresh keeps the cached status and offers an inline retry`() = runUnitTest {
        given()
        val fixture = Fixture()
        val response = fixture.runningWithPendingRefresh()
        val sut = fixture.sut()

        whenn()
        response.completeExceptionally(TestException())

        then()
        assertEquals(
            SettingsRes.strings.settings_export_load_error.desc(),
            sut.uiState.error,
        )
        assertEquals(
            SettingsRes.strings.settings_export_running_title.desc(),
            sut.uiState.status.title,
        )
        assertFalse(sut.uiState.request.isVisible)
        assertFalse(sut.uiState.download.isVisible)
        assertTrue(sut.uiState.refresh.isVisible)
        assertTrue(sut.uiState.refresh.isEnabled)
        assertTrue(sut.uiState.notifications.presentationNotification.isEmpty())
    }

    @Test
    fun `clears the inline error after a successful retry`() = runUnitTest {
        given()
        val fixture = Fixture()
        val response = fixture.runningWithPendingRefresh()
        val sut = fixture.sut()
        response.completeExceptionally(TestException())
        fixture.getExport.server = { DataExport(DataExportStatus.NONE) }

        whenn()
        sut.uiState.refresh.onClick?.invoke()
        testScheduler.advanceUntilIdle()

        then()
        assertNull(sut.uiState.error)
        assertTrue(sut.uiState.request.isVisible)
    }

    @Test
    fun `cache observation failure offers one inline retry`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.getExport.observation = failOnceThenSuspend(TestException())

        whenn()
        val sut = fixture.sut()

        then()
        assertEquals(SettingsRes.strings.settings_export_load_error.desc(), sut.uiState.error)
        assertTrue(sut.uiState.refresh.isVisible)
        assertTrue(sut.uiState.notifications.presentationNotification.isEmpty())
    }

    @Test
    fun `authentication failures use the shared notification handler`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.cache.value = DataExport(DataExportStatus.RUNNING)
        fixture.getExport.server = { throw Error.Unauthorized(TestException()) }

        whenn()
        val sut = fixture.sut()

        then()
        assertNull(sut.uiState.error)
        assertTrue(fixture.errorMapper.mappedErrors.single() is Error.Unauthorized)
        assertEquals(1, sut.uiState.notifications.presentationNotification.size)
    }

    @Test
    fun `starts an export and shows its progress when the request is tapped`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.sut()

        whenn()
        sut.uiState.request.onClick?.invoke()

        then()
        assertEquals(1, fixture.startExport.calls)
        assertTrue(sut.uiState.status.isInProgress)
        assertFalse(sut.uiState.request.isLoading)
    }

    @Test
    fun `plays success haptic when an export request succeeds`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.sut()

        whenn()
        sut.uiState.request.onClick?.invoke()

        then()
        assertEquals(
            listOf(PresentationNotification.SuccessHaptic),
            sut.uiState.notifications.presentationNotification,
        )
    }

    @Test
    fun `does not play success haptic when an export request fails`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.startExport.server = { throw TestException() }
        val sut = fixture.sut()

        whenn()
        sut.uiState.request.onClick?.invoke()

        then()
        assertTrue(sut.uiState.notifications.presentationNotification.isEmpty())
        assertEquals(SettingsRes.strings.settings_export_request_error.desc(), sut.uiState.error)
        assertTrue(sut.uiState.request.isEnabled)
    }

    @Test
    fun `passkey cancellation leaves the request available without an error`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.startExport.server = { throw Error.Ignore(TestException()) }
        val sut = fixture.sut()

        whenn()
        sut.uiState.request.onClick?.invoke()

        then()
        assertNull(sut.uiState.error)
        assertTrue(sut.uiState.request.isEnabled)
        assertTrue(sut.uiState.notifications.presentationNotification.isEmpty())
    }

    @Test
    fun `explains passkey confirmation failures without retrying`() = runUnitTest {
        given()
        val fixtures = listOf(
            StartPersonalDataExport.Error.ConfirmationFailed(),
            StartPersonalDataExport.Error.ConfirmationRequired(),
        ).map { error ->
            Fixture().also { it.startExport.server = { throw error } }
        }
        val suts = fixtures.map { it.sut() }

        whenn()
        suts.forEach { it.uiState.request.onClick?.invoke() }

        then()
        assertEquals(
            List(2) { SettingsRes.strings.settings_export_confirmation_error.desc() },
            suts.map { it.uiState.error },
        )
        assertEquals(listOf(1, 1), fixtures.map { it.startExport.calls })
        assertTrue(suts.all { it.uiState.notifications.presentationNotification.isEmpty() })
    }

    @Test
    fun `rate limit rejection hides the request and asks for a status refresh`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.startExport.server = { throw StartPersonalDataExport.Error.TooFrequent() }
        val sut = fixture.sut()

        whenn()
        sut.uiState.request.onClick?.invoke()

        then()
        assertFalse(sut.uiState.request.isVisible)
        assertNull(sut.uiState.status.footer)
        assertEquals(
            SettingsRes.strings.settings_export_request_title.desc(),
            sut.uiState.status.title,
        )
        assertEquals(SettingsRes.strings.settings_export_too_frequent.desc(), sut.uiState.error)
        assertTrue(sut.uiState.refresh.isVisible)
        assertTrue(sut.uiState.refresh.isEnabled)
    }

    @Test
    fun `status refresh after a rate limit restores the request for an unchanged status`() =
        runUnitTest {
            given()
            val fixture = Fixture()
            fixture.cache.value = DataExport(DataExportStatus.NONE)
            fixture.startExport.server = { throw StartPersonalDataExport.Error.TooFrequent() }
            val sut = fixture.sut()
            sut.uiState.request.onClick?.invoke()

            whenn()
            sut.uiState.refresh.onClick?.invoke()

            then()
            assertTrue(sut.uiState.request.isVisible)
            assertFalse(sut.uiState.refresh.isVisible)
            assertNull(sut.uiState.error)
        }

    @Test
    fun `drops duplicate requests and lifecycle refreshes while a request is confirmed`() =
        runUnitTest {
            given()
            val fixture = Fixture()
            val result = CompletableDeferred<DataExport>()
            fixture.startExport.server = { result.await() }
            val sut = fixture.sut()
            val request = sut.uiState.request.onClick
            request?.invoke()

            whenn()
            request?.invoke()
            sut.onViewPresented()
            result.complete(DataExport(DataExportStatus.RUNNING))

            then()
            assertEquals(1, fixture.startExport.calls)
            assertEquals(1, fixture.getExport.refreshCalls)
            assertTrue(sut.uiState.status.isInProgress)
        }

    @Test
    fun `shares the downloaded export file`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.cache.value = fixture.ready()
        val sut = fixture.sut()

        whenn()
        sut.uiState.download.onClick?.invoke()

        then()
        assertEquals(listOf(fixture.share()), sut.uiState.navigation.navigationDestination)
        assertTrue(sut.uiState.notifications.presentationNotification.isEmpty())
        assertNull(sut.uiState.error)
    }

    @Test
    fun `downloads and shares again on every press`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.cache.value = fixture.ready(expiresAt = fixture.now + 1.days)
        val sut = fixture.sut()
        sut.uiState.download.onClick?.invoke()

        whenn()
        sut.uiState.download.onClick?.invoke()

        then()
        assertEquals(2, fixture.downloadExport.calls)
        assertEquals(
            listOf(fixture.share(), fixture.share()),
            sut.uiState.navigation.navigationDestination,
        )
    }

    @Test
    fun `shares the file path returned by each download`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.cache.value = fixture.ready(expiresAt = fixture.now + 1.days)
        val sut = fixture.sut()
        sut.uiState.download.onClick?.invoke()
        fixture.cache.value = fixture.ready(expiresAt = fixture.now + 2.days)
        val updated = "/cache/personal_data_exports/3-4/bookk-personal-data.json".toPath()
        fixture.downloadExport.file = { updated }

        whenn()
        sut.uiState.download.onClick?.invoke()

        then()
        assertEquals(
            listOf(fixture.share(), fixture.share(updated)),
            sut.uiState.navigation.navigationDestination,
        )
    }

    @Test
    fun `failed download keeps the download available without requesting another export`() =
        runUnitTest {
            given()
            val fixture = Fixture()
            fixture.cache.value = fixture.ready()
            fixture.downloadExport.file = { throw TestException() }
            val sut = fixture.sut()

            whenn()
            sut.uiState.download.onClick?.invoke()

            then()
            assertEquals(
                SettingsRes.strings.settings_export_download_error.desc(),
                sut.uiState.error,
            )
            assertTrue(sut.uiState.download.isVisible)
            assertTrue(sut.uiState.download.isEnabled)
            assertEquals(0, fixture.startExport.calls)
        }

    @Test
    fun `retrying a failed download shares the file and clears the error`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.cache.value = fixture.ready(expiresAt = fixture.now + 1.days)
        fixture.downloadExport.file = { throw TestException() }
        val sut = fixture.sut()
        sut.uiState.download.onClick?.invoke()
        fixture.downloadExport.file = { fixture.file }

        whenn()
        sut.uiState.download.onClick?.invoke()

        then()
        assertEquals(listOf(fixture.share()), sut.uiState.navigation.navigationDestination)
        assertNull(sut.uiState.error)
    }

    @Test
    fun `unavailable download stops presenting the export as ready and offers a refresh`() =
        runUnitTest {
            given()
            val fixture = Fixture()
            fixture.cache.value = fixture.ready(expiresAt = fixture.now + 1.days)
            fixture.downloadExport.file = { throw DownloadPersonalDataExport.Error.NotReady() }
            val sut = fixture.sut()

            whenn()
            sut.uiState.download.onClick?.invoke()

            then()
            assertFalse(sut.uiState.status.showsFile)
            assertNull(sut.uiState.status.footer)
            assertEquals(DesignSystem.images.export_document, sut.uiState.status.icon)
            assertEquals(ColorToken.ActionText, sut.uiState.status.color)
            assertFalse(sut.uiState.download.isVisible)
            assertFalse(sut.uiState.request.isVisible)
            assertEquals(SettingsRes.strings.settings_export_unavailable.desc(), sut.uiState.error)
            assertTrue(sut.uiState.refresh.isVisible)
            assertTrue(sut.uiState.refresh.isEnabled)
            assertTrue(sut.uiState.navigation.navigationDestination.isEmpty())
        }

    @Test
    fun `refresh after an unavailable download shows the server status`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.cache.value = fixture.ready(expiresAt = fixture.now + 1.days)
        fixture.downloadExport.file = { throw DownloadPersonalDataExport.Error.NotReady() }
        val sut = fixture.sut()
        sut.uiState.download.onClick?.invoke()
        fixture.getExport.server = { DataExport(DataExportStatus.NONE) }

        whenn()
        sut.uiState.refresh.onClick?.invoke()

        then()
        assertEquals(1, fixture.getExport.refreshCalls)
        assertTrue(sut.uiState.request.isVisible)
        assertFalse(sut.uiState.download.isVisible)
        assertNull(sut.uiState.error)
    }

    @Test
    fun `cache updates of a ready export keep the download available`() = runUnitTest {
        given()
        val fixture = Fixture()
        val ready = fixture.ready(expiresAt = fixture.now + 1.days)
        fixture.cache.value = ready
        val sut = fixture.sut()
        sut.uiState.download.onClick?.invoke()
        fixture.cache.value = ready
        fixture.cache.value = ready.copy(availableAgainAt = fixture.now + 2.days)

        whenn()
        sut.uiState.download.onClick?.invoke()

        then()
        assertTrue(sut.uiState.download.isVisible)
        assertTrue(sut.uiState.download.isEnabled)
        assertEquals(2, fixture.downloadExport.calls)
    }

    @Test
    fun `hides the download until the cached export is ready again`() = runUnitTest {
        given()
        val interruptions = listOf(null) + listOf(
            DataExportStatus.NONE,
            DataExportStatus.RUNNING,
            DataExportStatus.FAILED,
        ).map { DataExport(it) }
        val fixtures = interruptions.map { Fixture().also { it.cache.value = it.ready() } }
        val suts = fixtures.map { it.sut() }

        whenn()
        val hiddenWhileInterrupted = fixtures.zip(interruptions).zip(suts).map { (pair, sut) ->
            val (fixture, interruption) = pair
            fixture.cache.value = interruption
            !sut.uiState.download.isVisible
        }
        fixtures.forEach { it.cache.value = it.ready() }

        then()
        assertEquals(List(interruptions.size) { true }, hiddenWhileInterrupted)
        assertTrue(suts.all { it.uiState.download.isVisible })
    }

    @Test
    fun `drops duplicate downloads while a download is active`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.cache.value = fixture.ready()
        val result = CompletableDeferred<Path>()
        fixture.downloadExport.file = { result.await() }
        val sut = fixture.sut()
        val download = sut.uiState.download.onClick
        download?.invoke()

        whenn()
        download?.invoke()
        result.complete(fixture.file)

        then()
        assertEquals(1, fixture.downloadExport.calls)
        assertEquals(listOf(fixture.share()), sut.uiState.navigation.navigationDestination)
    }

    @Test
    fun `back action returns to Settings`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.sut()

        whenn()
        sut.uiState.appBar.onBackClick?.invoke()

        then()
        assertEquals(
            listOf(PersonalDataExportDestinations.Back),
            sut.uiState.navigation.navigationDestination,
        )
    }
}
