package me.bookk.feature.settings.presentation.export

import dev.icerock.moko.resources.desc.desc
import dev.icerock.moko.resources.format
import dev.mokkery.answering.calls
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verify
import dev.mokkery.verifySuspend
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.MutableSharedFlow
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
import me.bookk.feature.settings.domain.api.GetPersonalDataExport
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

    private class ExportCache {
        val snapshots = MutableSharedFlow<DataExport?>(replay = 1, extraBufferCapacity = 1).apply {
            tryEmit(null)
        }
        var value: DataExport?
            get() {
                return snapshots.replayCache.last()
            }
            set(value) {
                check(snapshots.tryEmit(value))
            }
    }

    private class Fixture {
        val now = Instant.parse("2026-11-05T12:00:00Z")
        val cached = ExportCache()
        val remoteExport = mock<GetPersonalDataExport> {
            everySuspend { refresh() } calls { cached.value ?: DataExport(DataExportStatus.NONE) }
        }
        val getExport = mock<GetPersonalDataExport> {
            every { flow() } returns cached.snapshots
            everySuspend { refresh() } calls {
                remoteExport.refresh().also { cached.value = it }
            }
        }
        val startResponse = mock<StartPersonalDataExport> {
            everySuspend { invoke() } returns DataExport(DataExportStatus.RUNNING)
        }
        val startExport = mock<StartPersonalDataExport> {
            everySuspend { invoke() } calls {
                startResponse().also { cached.value = it }
            }
        }
        val file = "/cache/personal_data_exports/1-2/bookk-personal-data.json".toPath()
        val downloadExport = mock<DownloadPersonalDataExport> {
            everySuspend { invoke() } returns file
        }
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
    }

    @Test
    fun `keeps a cached ready export available without refreshing`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.cached.value = fixture.ready(
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
        assertNotNull(sut.uiState.requestAvailability)
        verifySuspend(VerifyMode.exactly(0)) { fixture.remoteExport.refresh() }
    }

    @Test
    fun `refreshes on open when nothing usable is cached`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        fixture.sut()

        then()
        verifySuspend(VerifyMode.exactly(1)) { fixture.remoteExport.refresh() }
    }

    @Test
    fun `trusts cached settled statuses on open`() = runUnitTest {
        given()
        val fixtures = listOf(DataExportStatus.NONE, DataExportStatus.FAILED).map { status ->
            Fixture().also { it.cached.value = DataExport(status) }
        }

        whenn()
        fixtures.forEach { it.sut() }

        then()
        fixtures.forEach {
            verifySuspend(VerifyMode.exactly(0)) { it.remoteExport.refresh() }
        }
    }

    @Test
    fun `refreshes on open when the cached export is running`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.cached.value = DataExport(DataExportStatus.RUNNING)

        whenn()
        fixture.sut()

        then()
        verifySuspend(VerifyMode.exactly(1)) { fixture.remoteExport.refresh() }
    }

    @Test
    fun `refreshes only once on open when the refreshed export still maps to nothing`() =
        runUnitTest {
            given()
            val fixture = Fixture()
            everySuspend { fixture.getExport.refresh() } calls {
                fixture.cached.value = null
                fixture.ready(expiresAt = fixture.now)
            }

            whenn()
            fixture.sut()

            then()
            verifySuspend(VerifyMode.exactly(1)) { fixture.getExport.refresh() }
        }

    @Test
    fun `server status replaces an empty cache after opening`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.remoteExport.refresh() } returns DataExport(DataExportStatus.RUNNING)

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
        everySuspend { fixture.remoteExport.refresh() } calls { response.await() }

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
    fun `polls a running export after the polling interval`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.cached.value = DataExport(DataExportStatus.RUNNING)
        fixture.sut()

        whenn()
        advanceTimeBy(10.seconds)
        runCurrent()

        then()
        verifySuspend(VerifyMode.exactly(2)) { fixture.remoteExport.refresh() }
    }

    @Test
    fun `does not poll before the polling interval elapses`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.cached.value = DataExport(DataExportStatus.RUNNING)
        fixture.sut()

        whenn()
        advanceTimeBy(9.seconds)
        runCurrent()

        then()
        verifySuspend(VerifyMode.exactly(1)) { fixture.remoteExport.refresh() }
    }

    @Test
    fun `stops polling once the export is no longer running`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.cached.value = DataExport(DataExportStatus.RUNNING)
        fixture.sut()
        everySuspend { fixture.remoteExport.refresh() } returns fixture.ready()
        advanceTimeBy(10.seconds)
        runCurrent()

        whenn()
        advanceTimeBy(60.seconds)
        runCurrent()

        then()
        verifySuspend(VerifyMode.exactly(2)) { fixture.remoteExport.refresh() }
    }

    @Test
    fun `stops polling a running export after thirty attempts`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.cached.value = DataExport(DataExportStatus.RUNNING)
        fixture.sut()

        whenn()
        advanceTimeBy(10.minutes)
        runCurrent()

        then()
        verifySuspend(VerifyMode.exactly(31)) { fixture.remoteExport.refresh() }
    }

    @Test
    fun `shows the default screen when the server has no export`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val sut = fixture.sut()

        then()
        assertFalse(sut.uiState.download.isVisible)
        assertTrue(sut.uiState.request.isVisible)
        assertEquals(SettingsRes.strings.settings_export_request_title.desc(), sut.uiState.status.title)
        assertEquals(SettingsRes.strings.settings_export_description.desc(), sut.uiState.status.message)
        assertNull(sut.uiState.expiry)
    }

    @Test
    fun `allows explicit refresh when a ready download becomes unavailable`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.cached.value = fixture.ready(expiresAt = fixture.now + 1.days)
        everySuspend { fixture.downloadExport() } throws DownloadPersonalDataExport.Error.NotReady()
        val sut = fixture.sut()
        sut.uiState.download.onClick?.invoke()
        everySuspend { fixture.remoteExport.refresh() } returns DataExport(DataExportStatus.NONE)

        whenn()
        sut.uiState.refresh.onClick?.invoke()

        then()
        verifySuspend(VerifyMode.exactly(1)) { fixture.remoteExport.refresh() }
        assertTrue(sut.uiState.request.isVisible)
        assertFalse(sut.uiState.download.isVisible)
        assertNull(sut.uiState.error)
    }

    @Test
    fun `shows the default screen for a never requested export`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.cached.value = DataExport(DataExportStatus.NONE)

        whenn()
        val sut = fixture.sut()

        then()
        assertTrue(sut.uiState.request.isVisible)
        assertFalse(sut.uiState.refresh.isVisible)
        assertEquals(SettingsRes.strings.settings_export_description.desc(), sut.uiState.status.message)
    }

    @Test
    fun `renders a cached running export while its opening refresh is pending`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.cached.value = DataExport(DataExportStatus.RUNNING)
        val response = CompletableDeferred<DataExport>()
        everySuspend { fixture.remoteExport.refresh() } calls { response.await() }

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
        verifySuspend(VerifyMode.exactly(1)) { fixture.getExport.refresh() }
    }

    @Test
    fun `polls a later running observation after the polling interval`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.cached.value = DataExport(DataExportStatus.NONE)
        val sut = fixture.sut()
        fixture.cached.value = DataExport(DataExportStatus.RUNNING)
        everySuspend { fixture.remoteExport.refresh() } returns fixture.ready()

        whenn()
        advanceTimeBy(10.seconds)
        runCurrent()

        then()
        assertTrue(sut.uiState.download.isVisible)
        assertFalse(sut.uiState.status.isInProgress)
        verifySuspend(VerifyMode.exactly(1)) { fixture.remoteExport.refresh() }
        verifySuspend(VerifyMode.exactly(0)) { fixture.downloadExport() }
    }

    @Test
    fun `subscribes once and refreshes on open only for running records`() = runUnitTest {
        given()
        val rows = listOf(
            DataExport(DataExportStatus.RUNNING),
            DataExport(DataExportStatus.READY),
            DataExport(DataExportStatus.FAILED),
            DataExport(DataExportStatus.NONE, requestedAt = Instant.parse("2026-11-01T00:00:00Z")),
            DataExport(
                DataExportStatus.NONE,
                availableAgainAt = Instant.parse("2026-11-06T00:00:00Z"),
            ),
        )
        val fixtures = rows.map { row ->
            Fixture().also { fixture ->
                fixture.cached.value = row
                everySuspend { fixture.getExport.refresh() } returns row
            }
        }

        whenn()
        fixtures.forEach { it.sut() }

        then()
        fixtures.forEach { fixture ->
            verify(VerifyMode.exactly(1)) { fixture.getExport.flow() }
            val expected = if (fixture.cached.value?.status == DataExportStatus.RUNNING) 1 else 0
            verifySuspend(VerifyMode.exactly(expected)) { fixture.getExport.refresh() }
        }
    }

    @Test
    fun `renders database changes without starting another refresh`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.cached.value = DataExport(DataExportStatus.RUNNING)
        everySuspend { fixture.getExport.refresh() } returns DataExport(DataExportStatus.RUNNING)
        val sut = fixture.sut()

        whenn()
        fixture.cached.value = fixture.ready()

        then()
        assertTrue(sut.uiState.download.isVisible)
        assertFalse(sut.uiState.status.isInProgress)
        verifySuspend(VerifyMode.exactly(1)) { fixture.getExport.refresh() }
    }

    @Test
    fun `refresh return values are not rendered before the database changes`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.cached.value = DataExport(DataExportStatus.RUNNING)
        everySuspend { fixture.getExport.refresh() } returns fixture.ready()
        val sut = fixture.sut()

        whenn()
        sut.uiState.refresh.onClick?.invoke()

        then()
        assertTrue(sut.uiState.status.isInProgress)
        assertFalse(sut.uiState.download.isVisible)
    }

    @Test
    fun `start return values are not rendered before the database changes`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.cached.value = DataExport(DataExportStatus.NONE)
        everySuspend { fixture.startExport() } returns fixture.ready()
        val sut = fixture.sut()

        whenn()
        sut.uiState.request.onClick?.invoke()

        then()
        assertTrue(sut.uiState.request.isVisible)
        assertFalse(sut.uiState.download.isVisible)
    }

    @Test
    fun `failed opening refresh preserves cached data and offers retry`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.cached.value = DataExport(DataExportStatus.RUNNING)
        val response = CompletableDeferred<DataExport>()
        everySuspend { fixture.remoteExport.refresh() } calls { response.await() }
        val sut = fixture.sut()

        whenn()
        response.completeExceptionally(TestException())

        then()
        assertTrue(sut.uiState.status.isInProgress)
        assertFalse(sut.uiState.download.isVisible)
        assertNotNull(sut.uiState.error)
        assertTrue(sut.uiState.refresh.isVisible)
        assertTrue(sut.uiState.notifications.presentationNotification.isEmpty())
    }

    @Test
    fun `database observation failures offer one inline retry`() = runUnitTest {
        given()
        val fixture = Fixture()
        every { fixture.getExport.flow() } returns failOnceThenSuspend(TestException())

        whenn()
        val sut = fixture.sut()

        then()
        assertNotNull(sut.uiState.error)
        assertTrue(sut.uiState.refresh.isVisible)
        assertTrue(sut.uiState.notifications.presentationNotification.isEmpty())
    }

    @Test
    fun `same cached status restores the default screen after rate limit recovery`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.cached.value = DataExport(DataExportStatus.NONE)
        everySuspend { fixture.startResponse() } throws StartPersonalDataExport.Error.TooFrequent()
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
    fun `ready status removes the loading refresh control after opening`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.cached.value = DataExport(DataExportStatus.RUNNING)
        everySuspend { fixture.remoteExport.refresh() } returns fixture.ready()
        val sut = fixture.sut()

        whenn()
        sut.onViewPresented()

        then()
        assertTrue(sut.uiState.download.isVisible)
        assertFalse(sut.uiState.refresh.isVisible)
    }

    @Test
    fun `request presentation matches the approved document and passkey design`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.sut()

        whenn()
        sut.onViewPresented()

        then()
        assertEquals(DesignSystem.images.export_document, sut.uiState.status.icon)
        assertEquals(SettingsRes.strings.settings_export_request_footer.desc(), sut.uiState.status.footer)
        assertFalse(sut.uiState.refresh.isVisible)
        assertTrue(sut.uiState.request.isVisible)
    }

    @Test
    fun `preparing presentation separates the status message and footer`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.cached.value = DataExport(DataExportStatus.RUNNING)
        everySuspend { fixture.getExport.refresh() } returns DataExport(DataExportStatus.RUNNING)
        val sut = fixture.sut()

        whenn()
        sut.onViewPresented()

        then()
        assertNull(sut.uiState.status.icon)
        assertEquals(
            SettingsRes.strings.settings_export_running_message.desc(),
            sut.uiState.status.message,
        )
        assertEquals(SettingsRes.strings.settings_export_running_footer.desc(), sut.uiState.status.footer)
        assertTrue(sut.uiState.refresh.isVisible)
    }

    @Test
    fun `ready presentation shows the file with success and download icons`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.cached.value = DataExport(DataExportStatus.RUNNING)
        everySuspend { fixture.remoteExport.refresh() } returns fixture.ready()
        val sut = fixture.sut()

        whenn()
        sut.onViewPresented()

        then()
        assertEquals(DesignSystem.images.export_ready, sut.uiState.status.icon)
        assertEquals(ColorToken.Success, sut.uiState.status.color)
        assertTrue(sut.uiState.status.showsFile)
        assertEquals(DesignSystem.images.download, sut.uiState.download.icon)
        assertNull(sut.uiState.status.message)
        assertEquals(SettingsRes.strings.settings_export_ready_message.desc(), sut.uiState.status.footer)
        assertFalse(sut.uiState.refresh.isVisible)
    }

    @Test
    fun `missing cached status shows the request screen once the server has no export`() =
        runUnitTest {
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
            verifySuspend(VerifyMode.exactly(0)) { fixture.startExport() }
        }

    @Test
    fun `shows preparing status without offering another request or download`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.cached.value = DataExport(DataExportStatus.RUNNING)
        everySuspend { fixture.getExport.refresh() } returns DataExport(DataExportStatus.RUNNING)
        val sut = fixture.sut()

        whenn()
        sut.onViewPresented()

        then()
        assertTrue(sut.uiState.status.isInProgress)
        assertFalse(sut.uiState.request.isVisible)
        assertFalse(sut.uiState.download.isVisible)
        assertTrue(sut.uiState.refresh.isEnabled)
    }

    @Test
    fun `shows ready download and backend expiry during the request cooldown`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.cached.value = DataExport(DataExportStatus.RUNNING)
        everySuspend { fixture.remoteExport.refresh() } returns fixture.ready(
            Instant.parse("2026-11-06T12:00:00Z"),
            Instant.parse("2026-11-07T12:00:00Z"),
        )
        val sut = fixture.sut()

        whenn()
        sut.onViewPresented()

        then()
        assertTrue(sut.uiState.download.isVisible)
        assertFalse(sut.uiState.request.isVisible)
        assertNotNull(sut.uiState.expiry)
        assertEquals(
            SettingsRes.strings.settings_export_request_after_expiry.desc(),
            sut.uiState.requestAvailability,
        )
        assertFalse(sut.uiState.status.isInProgress)
    }

    @Test
    fun `does not fabricate missing expiry or cooldown timestamps`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.cached.value = DataExport(DataExportStatus.RUNNING)
        everySuspend { fixture.remoteExport.refresh() } returns fixture.ready()
        val sut = fixture.sut()

        whenn()
        sut.onViewPresented()

        then()
        assertNull(sut.uiState.expiry)
        assertTrue(sut.uiState.download.isVisible)
    }

    @Test
    fun `null observation after expiry restores the default request screen`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.cached.value = fixture.ready(expiresAt = fixture.now + 1.days)
        val sut = fixture.sut()

        whenn()
        fixture.cached.value = null

        then()
        assertFalse(sut.uiState.download.isVisible)
        assertTrue(sut.uiState.request.isVisible)
        assertFalse(sut.uiState.refresh.isVisible)
        assertNull(sut.uiState.expiry)
        assertNull(sut.uiState.requestAvailability)
        assertEquals(SettingsRes.strings.settings_export_request_title.desc(), sut.uiState.status.title)
        assertEquals(SettingsRes.strings.settings_export_description.desc(), sut.uiState.status.message)
        verifySuspend(VerifyMode.exactly(0)) { fixture.remoteExport.refresh() }
    }

    @Test
    fun `ready export explains that a new request opens after it expires`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.cached.value = fixture.ready(expiresAt = fixture.now + 3.days)

        whenn()
        val sut = fixture.sut()

        then()
        assertEquals(
            SettingsRes.strings.settings_export_request_after_expiry.desc(),
            sut.uiState.requestAvailability,
        )
    }

    @Test
    fun `running export explains that a new request opens after it expires`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.remoteExport.refresh() } returns DataExport(
            DataExportStatus.RUNNING,
            availableAgainAt = fixture.now + 1.days,
        )

        whenn()
        val sut = fixture.sut()

        then()
        assertEquals(
            SettingsRes.strings.settings_export_request_after_expiry.desc(),
            sut.uiState.requestAvailability,
        )
    }

    @Test
    fun `blocks a new request during cooldown without hiding its availability date`() =
        runUnitTest {
            given()
            val fixture = Fixture()
            fixture.cached.value = DataExport(DataExportStatus.RUNNING)
            everySuspend { fixture.remoteExport.refresh() } returns DataExport(
                DataExportStatus.NONE,
                availableAgainAt = Instant.parse("2026-11-06T12:00:00Z"),
            )
            val sut = fixture.sut()

            whenn()
            sut.onViewPresented()

            then()
            assertFalse(sut.uiState.request.isVisible)
            assertEquals(
                SettingsRes.strings.settings_export_cooldown.format(
                    Instant.parse("2026-11-06T12:00:00Z")
                        .toLocalDateTime(TimeZone.currentSystemDefault())
                        .toString(),
                ),
                sut.uiState.requestAvailability,
            )
            assertTrue(sut.uiState.refresh.isEnabled)
        }

    @Test
    fun `offers a new request at the exact cooldown boundary`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.cached.value = DataExport(DataExportStatus.RUNNING)
        everySuspend { fixture.remoteExport.refresh() } returns DataExport(
            DataExportStatus.FAILED,
            availableAgainAt = fixture.now,
        )
        val sut = fixture.sut()

        whenn()
        sut.onViewPresented()

        then()
        assertTrue(sut.uiState.request.isVisible)
        assertNull(sut.uiState.requestAvailability)
        assertEquals(SettingsRes.strings.settings_export_failed_title.desc(), sut.uiState.status.title)
    }

    @Test
    fun `refreshes running status into a ready download`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.cached.value = DataExport(DataExportStatus.RUNNING)
        everySuspend { fixture.getExport.refresh() } returns DataExport(DataExportStatus.RUNNING)
        val sut = fixture.sut()
        sut.onViewPresented()
        everySuspend { fixture.getExport.refresh() } calls {
            fixture.ready().also { fixture.cached.value = it }
        }

        whenn()
        sut.uiState.refresh.onClick?.invoke()

        then()
        assertTrue(sut.uiState.download.isVisible)
        assertFalse(sut.uiState.status.isInProgress)
        verifySuspend(VerifyMode.exactly(0)) { fixture.downloadExport() }
    }

    @Test
    fun `shows an inline retry after a status failure`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.cached.value = DataExport(DataExportStatus.RUNNING)
        val response = CompletableDeferred<DataExport>()
        everySuspend { fixture.remoteExport.refresh() } calls { response.await() }
        val sut = fixture.sut()

        whenn()
        response.completeExceptionally(TestException())

        then()
        assertNotNull(sut.uiState.error)
        assertEquals(SettingsRes.strings.settings_export_running_title.desc(), sut.uiState.status.title)
        assertFalse(sut.uiState.request.isVisible)
        assertFalse(sut.uiState.download.isVisible)
        assertTrue(sut.uiState.refresh.isEnabled)
        assertTrue(sut.uiState.refresh.isVisible)
        assertTrue(sut.uiState.notifications.presentationNotification.isEmpty())
    }

    @Test
    fun `clears the inline error after a successful retry`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.cached.value = DataExport(DataExportStatus.RUNNING)
        val response = CompletableDeferred<DataExport>()
        everySuspend { fixture.remoteExport.refresh() } calls { response.await() }
        val sut = fixture.sut()
        response.completeExceptionally(TestException())
        everySuspend { fixture.remoteExport.refresh() } returns DataExport(DataExportStatus.NONE)

        whenn()
        sut.uiState.refresh.onClick?.invoke()
        testScheduler.advanceUntilIdle()

        then()
        assertNull(sut.uiState.error)
        assertTrue(sut.uiState.request.isVisible)
    }

    @Test
    fun `requests an export only after the user taps the action`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.sut()

        whenn()
        sut.uiState.request.onClick?.invoke()

        then()
        verifySuspend(VerifyMode.exactly(1)) { fixture.startExport() }
        assertTrue(sut.uiState.status.isInProgress)
        assertFalse(sut.uiState.request.isLoading)
    }

    @Test
    fun `plays success haptic when an export request succeeds`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.sut()
        sut.onViewPresented()

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
        everySuspend { fixture.startResponse() } throws TestException()
        val sut = fixture.sut()
        sut.onViewPresented()

        whenn()
        sut.uiState.request.onClick?.invoke()

        then()
        assertTrue(
            sut.uiState.notifications.presentationNotification.none {
                it is PresentationNotification.SuccessHaptic
            },
        )
        assertNotNull(sut.uiState.error)
        assertTrue(sut.uiState.request.isEnabled)
    }

    @Test
    fun `passkey cancellation leaves the request available without an error`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.startResponse() } throws Error.Ignore(TestException())
        val sut = fixture.sut()
        sut.onViewPresented()

        whenn()
        sut.uiState.request.onClick?.invoke()

        then()
        assertNull(sut.uiState.error)
        assertTrue(sut.uiState.request.isEnabled)
        assertTrue(sut.uiState.notifications.presentationNotification.isEmpty())
    }

    @Test
    fun `explains confirmation failures without automatically retrying`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.sut()
        sut.onViewPresented()

        whenn()
        listOf(
            StartPersonalDataExport.Error.ConfirmationFailed(),
            StartPersonalDataExport.Error.ConfirmationRequired(),
        ).forEach { error ->
            everySuspend { fixture.startResponse() } throws error
            sut.uiState.request.onClick?.invoke()
            assertEquals(
                SettingsRes.strings.settings_export_confirmation_error.desc(),
                sut.uiState.error,
            )
        }

        then()
        verifySuspend(VerifyMode.exactly(2)) { fixture.startExport() }
        assertTrue(sut.uiState.notifications.presentationNotification.isEmpty())
    }

    @Test
    fun `rate limit rejection removes the request footer`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.startResponse() } throws StartPersonalDataExport.Error.TooFrequent()
        val sut = fixture.sut()

        whenn()
        sut.uiState.request.onClick?.invoke()

        then()
        assertNull(sut.uiState.status.footer)
        assertEquals(SettingsRes.strings.settings_export_request_title.desc(), sut.uiState.status.title)
    }

    @Test
    fun `unavailable download no longer presents the export as ready`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.cached.value = fixture.ready(expiresAt = fixture.now + 1.days)
        everySuspend { fixture.downloadExport() } throws DownloadPersonalDataExport.Error.NotReady()
        val sut = fixture.sut()

        whenn()
        sut.uiState.download.onClick?.invoke()

        then()
        assertFalse(sut.uiState.status.showsFile)
        assertNull(sut.uiState.status.footer)
        assertEquals(DesignSystem.images.export_document, sut.uiState.status.icon)
        assertEquals(ColorToken.ActionText, sut.uiState.status.color)
        assertEquals(SettingsRes.strings.settings_export_request_title.desc(), sut.uiState.status.title)
    }

    @Test
    fun `rate limit rejection asks for status refresh before another request`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.startResponse() } throws StartPersonalDataExport.Error.TooFrequent()
        val sut = fixture.sut()
        sut.onViewPresented()

        whenn()
        sut.uiState.request.onClick?.invoke()

        then()
        assertFalse(sut.uiState.request.isVisible)
        assertEquals(SettingsRes.strings.settings_export_too_frequent.desc(), sut.uiState.error)
        assertTrue(sut.uiState.refresh.isEnabled)
        assertTrue(sut.uiState.refresh.isVisible)
    }

    @Test
    fun `delegates each download press to the download operation`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.cached.value = fixture.ready(expiresAt = fixture.now + 1.days)
        val sut = fixture.sut()
        sut.uiState.download.onClick?.invoke()

        whenn()
        sut.uiState.download.onClick?.invoke()

        then()
        verifySuspend(VerifyMode.exactly(2)) { fixture.downloadExport() }
        assertEquals(
            listOf(fixture.share(), fixture.share()),
            sut.uiState.navigation.navigationDestination,
        )
        assertNull(sut.uiState.error)
    }

    @Test
    fun `failed downloads are requested again on retry`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.cached.value = fixture.ready(expiresAt = fixture.now + 1.days)
        everySuspend { fixture.downloadExport() } throws TestException()
        val sut = fixture.sut()
        sut.uiState.download.onClick?.invoke()
        everySuspend { fixture.downloadExport() } returns fixture.file

        whenn()
        sut.uiState.download.onClick?.invoke()

        then()
        verifySuspend(VerifyMode.exactly(2)) { fixture.downloadExport() }
        assertEquals(listOf(fixture.share()), sut.uiState.navigation.navigationDestination)
        assertNull(sut.uiState.error)
    }

    @Test
    fun `shares the download result after export timestamps change`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.cached.value = fixture.ready(expiresAt = fixture.now + 1.days)
        val sut = fixture.sut()
        sut.uiState.download.onClick?.invoke()
        fixture.cached.value = fixture.ready(expiresAt = fixture.now + 2.days).copy(
            requestedAt = fixture.now + 1.days,
        )
        val updated = "/cache/personal_data_exports/3-4/bookk-personal-data.json".toPath()
        everySuspend { fixture.downloadExport() } returns updated

        whenn()
        sut.uiState.download.onClick?.invoke()

        then()
        verifySuspend(VerifyMode.exactly(2)) { fixture.downloadExport() }
        assertEquals(
            listOf(fixture.share(), fixture.share(updated)),
            sut.uiState.navigation.navigationDestination,
        )
    }

    @Test
    fun `status and cooldown updates keep the download action available`() = runUnitTest {
        given()
        val fixture = Fixture()
        val ready = fixture.ready(expiresAt = fixture.now + 1.days)
        fixture.cached.value = ready
        val sut = fixture.sut()
        sut.uiState.download.onClick?.invoke()
        fixture.cached.value = ready
        fixture.cached.value = ready.copy(availableAgainAt = fixture.now + 2.days)
        assertTrue(sut.uiState.download.isVisible)
        assertTrue(sut.uiState.download.isEnabled)

        whenn()
        sut.uiState.download.onClick?.invoke()

        then()
        verifySuspend(VerifyMode.exactly(2)) { fixture.downloadExport() }
        assertEquals(2, sut.uiState.navigation.navigationDestination.size)
    }

    @Test
    fun `cleared or non ready status hides downloads until ready again`() = runUnitTest {
        given()
        val statuses = listOf(null) + listOf(
            DataExportStatus.NONE,
            DataExportStatus.RUNNING,
            DataExportStatus.FAILED,
        ).map { DataExport(it) }
        val fixtures = statuses.map { status ->
            val fixture = Fixture()
            val ready = fixture.ready(expiresAt = fixture.now + 1.days)
            fixture.cached.value = ready
            val sut = fixture.sut()
            sut.uiState.download.onClick?.invoke()
            fixture.cached.value = status
            assertFalse(sut.uiState.download.isVisible)
            fixture.cached.value = ready
            assertTrue(sut.uiState.download.isVisible)
            fixture to sut
        }

        whenn()
        fixtures.forEach { (_, sut) -> sut.uiState.download.onClick?.invoke() }

        then()
        fixtures.forEach { (fixture, _) ->
            verifySuspend(VerifyMode.exactly(2)) { fixture.downloadExport() }
        }
    }

    @Test
    fun `shares the downloaded export file`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.cached.value = DataExport(DataExportStatus.RUNNING)
        everySuspend { fixture.remoteExport.refresh() } returns fixture.ready()
        val sut = fixture.sut()
        sut.onViewPresented()

        whenn()
        sut.uiState.download.onClick?.invoke()

        then()
        assertEquals(listOf(fixture.share()), sut.uiState.navigation.navigationDestination)
        assertTrue(sut.uiState.notifications.presentationNotification.isEmpty())
    }

    @Test
    fun `unavailable download hides stale actions and offers status refresh`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.cached.value = DataExport(DataExportStatus.RUNNING)
        everySuspend { fixture.remoteExport.refresh() } returns fixture.ready()
        everySuspend { fixture.downloadExport() } throws DownloadPersonalDataExport.Error.NotReady()
        val sut = fixture.sut()
        sut.onViewPresented()

        whenn()
        sut.uiState.download.onClick?.invoke()

        then()
        assertFalse(sut.uiState.download.isVisible)
        assertFalse(sut.uiState.request.isVisible)
        assertEquals(SettingsRes.strings.settings_export_unavailable.desc(), sut.uiState.error)
        assertTrue(sut.uiState.refresh.isEnabled)
        assertTrue(sut.uiState.refresh.isVisible)
        assertTrue(sut.uiState.navigation.navigationDestination.isEmpty())
    }

    @Test
    fun `failed download can be retried without requesting another export`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.cached.value = DataExport(DataExportStatus.RUNNING)
        everySuspend { fixture.remoteExport.refresh() } returns fixture.ready()
        everySuspend { fixture.downloadExport() } throws TestException()
        val sut = fixture.sut()
        sut.onViewPresented()

        whenn()
        sut.uiState.download.onClick?.invoke()

        then()
        assertNotNull(sut.uiState.error)
        assertTrue(sut.uiState.download.isVisible)
        assertTrue(sut.uiState.download.isEnabled)
        verifySuspend(VerifyMode.exactly(0)) { fixture.startExport() }
    }

    @Test
    fun `drops duplicate requests and lifecycle refreshes during confirmation`() = runUnitTest {
        given()
        val fixture = Fixture()
        val result = CompletableDeferred<DataExport>()
        everySuspend { fixture.startResponse() } calls { result.await() }
        val sut = fixture.sut()
        sut.onViewPresented()
        val request = sut.uiState.request.onClick
        request?.invoke()

        whenn()
        request?.invoke()
        sut.onViewPresented()
        result.complete(DataExport(DataExportStatus.RUNNING))

        then()
        verifySuspend(VerifyMode.exactly(1)) { fixture.startExport() }
        verifySuspend(VerifyMode.exactly(1)) { fixture.remoteExport.refresh() }
        assertTrue(sut.uiState.status.isInProgress)
    }

    @Test
    fun `drops duplicate downloads while a download is active`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.cached.value = DataExport(DataExportStatus.RUNNING)
        everySuspend { fixture.remoteExport.refresh() } returns fixture.ready()
        val result = CompletableDeferred<Path>()
        everySuspend { fixture.downloadExport() } calls { result.await() }
        val sut = fixture.sut()
        sut.onViewPresented()
        val download = sut.uiState.download.onClick
        download?.invoke()

        whenn()
        download?.invoke()
        result.complete(fixture.file)

        then()
        verifySuspend(VerifyMode.exactly(1)) { fixture.downloadExport() }
        assertEquals(listOf(fixture.share()), sut.uiState.navigation.navigationDestination)
    }

    @Test
    fun `drops repeated running observations while a status refresh is active`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.cached.value = DataExport(DataExportStatus.RUNNING)
        val result = CompletableDeferred<DataExport>()
        everySuspend { fixture.remoteExport.refresh() } calls { result.await() }
        val sut = fixture.sut()

        whenn()
        fixture.cached.value = DataExport(DataExportStatus.RUNNING, requestedAt = fixture.now)
        fixture.cached.value = DataExport(DataExportStatus.RUNNING, requestedAt = fixture.now + 1.days)
        result.complete(fixture.ready())

        then()
        verifySuspend(VerifyMode.exactly(1)) { fixture.remoteExport.refresh() }
        assertTrue(sut.uiState.download.isVisible)
    }

    @Test
    fun `authentication failures use the shared notification handler`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.cached.value = DataExport(DataExportStatus.RUNNING)
        everySuspend { fixture.remoteExport.refresh() } throws Error.Unauthorized(TestException())
        val sut = fixture.sut()

        whenn()
        sut.onViewPresented()

        then()
        assertNull(sut.uiState.error)
        assertEquals(1, fixture.errorMapper.mappedErrors.size)
        assertTrue(fixture.errorMapper.mappedErrors.single() is Error.Unauthorized)
        assertEquals(1, sut.uiState.notifications.presentationNotification.size)
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
