package me.bookk.feature.settings.presentation.dashboard

import dev.icerock.moko.resources.desc.desc
import dev.mokkery.answering.calls
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifySuspend
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import library.device.api.DeviceFacade
import me.bookk.android.feature.settings.resources.SettingsRes
import me.bookk.core.presentation.VmArgs
import me.bookk.core.presentation.error.ActionType
import me.bookk.core.presentation.error.PresentationNotification
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.designsystem.resources.color.ColorToken
import me.bookk.designsystem.test.FakeErrorMapper
import me.bookk.designsystem.test.TestException
import me.bookk.designsystem.test.ViewModelTestDispatchers
import me.bookk.designsystem.test.assertMappedSingle
import me.bookk.designsystem.test.assertSingle
import me.bookk.designsystem.test.failOnceThenSuspend
import me.bookk.designsystem.test.tap
import me.bookk.feature.settings.domain.api.AcceptLegalDocuments
import me.bookk.feature.settings.domain.api.GetLegalDocumentStatuses
import me.bookk.feature.settings.domain.api.GetSettings
import me.bookk.feature.settings.domain.api.UpdateColorScheme
import me.bookk.feature.settings.domain.api.entity.AcceptedLegalDocuments
import me.bookk.feature.settings.domain.api.entity.ColorScheme
import me.bookk.feature.settings.domain.api.entity.LegalDocument
import me.bookk.feature.settings.domain.api.entity.LegalDocumentStatus
import me.bookk.feature.settings.domain.api.entity.LegalDocumentVersion
import me.bookk.feature.settings.domain.api.entity.Settings
import me.bookk.feature.settings.domain.api.entity.SettingsProfile
import me.bookk.feature.settings.presentation.FakeSettingsStateFactory
import me.bookk.feature.settings.presentation.dashboard.AppearanceSection.UIColorScheme
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue
import kotlin.time.Instant

class SettingsDashboardViewModelTest {

    private val dispatchers = ViewModelTestDispatchers()

    @BeforeTest
    fun setUp() {
        dispatchers.install()
    }

    @AfterTest
    fun tearDown() {
        dispatchers.uninstall()
    }

    @Test
    fun `sets the personal data export account label`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val sut = fixture.sut()

        then()
        assertEquals(
            SettingsRes.strings.settings_export_title.desc(),
            sut.uiState.account.dataExport.text,
        )
    }

    private class Fixture {
        val getSettings = mock<GetSettings> {
            everySuspend { invoke() } returns
                Settings(ColorScheme.DARK, SettingsProfile("Anna", "Smith", "anna@example.com"))
        }
        val updateColorScheme = mock<UpdateColorScheme>()
        val statuses = MutableStateFlow<List<LegalDocumentStatus>>(emptyList())
        val remoteStatuses = mock<GetLegalDocumentStatuses> {
            everySuspend { refresh() } returns listOf(privacy(), terms())
        }
        val getLegalDocumentStatuses = mock<GetLegalDocumentStatuses> {
            every { flow() } returns statuses
            everySuspend { refresh() } calls {
                remoteStatuses.refresh().also { statuses.value = it }
            }
        }
        val acceptLegalDocuments = mock<AcceptLegalDocuments> {
            everySuspend { invoke(any()) } returns Unit
        }
        val deviceFacade = mock<DeviceFacade> {
            every { openUrlPreview(any()) } returns Unit
        }
        val errorMapper = FakeErrorMapper()

        fun sut(): SettingsDashboardViewModel {
            return SettingsDashboardViewModel(
                getSettings = getSettings,
                getLegalDocumentStatuses = getLegalDocumentStatuses,
                acceptLegalDocuments = acceptLegalDocuments,
                updateColorScheme = updateColorScheme,
                deviceFacade = deviceFacade,
                settingsStateFactory = FakeSettingsStateFactory(),
                vmArgs = VmArgs(errorMapper),
            )
        }
    }

    @Test
    fun `shows both static document rows before the status flow emits`() = runUnitTest {
        given()
        val fixture = Fixture()
        every { fixture.getLegalDocumentStatuses.flow() } returns emptyFlow()

        whenn()
        val sut = fixture.sut()

        then()
        val documents = sut.uiState.legal.documents
        assertEquals(listOf("PRIVACY_POLICY", "TERMS_OF_SERVICE"), documents.map { it.id })
        assertEquals(
            listOf(
                SettingsRes.strings.settings_support_privacy_label.desc(),
                SettingsRes.strings.settings_support_terms_label.desc(),
            ),
            documents.map { it.title },
        )
        documents.forEach {
            assertEquals(SettingsRes.strings.settings_legal_unknown.desc(), it.status)
            assertEquals(ColorToken.SecondaryText, it.statusColor)
            assertNull(it.version)
            assertNull(it.onClick)
        }
        assertTrue(!sut.uiState.legal.accept.isVisible)
    }

    @Test
    fun `keeps document rows in static order when statuses arrive in a different order`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.remoteStatuses.refresh() } returns listOf(terms(), privacy())
        val sut = fixture.sut()
        val initialIds = sut.uiState.legal.documents.map { it.id }

        whenn()
        sut.onViewPresented()

        then()
        assertEquals(initialIds, sut.uiState.legal.documents.map { it.id })
        assertEquals(listOf("privacy-v2", "terms-v3"), sut.uiState.legal.documents.map { it.version })
    }

    @Test
    fun `keeps an unknown row for a document missing from the loaded statuses`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.remoteStatuses.refresh() } returns listOf(privacy())
        val sut = fixture.sut()

        whenn()
        sut.onViewPresented()

        then()
        val documents = sut.uiState.legal.documents
        assertEquals(2, documents.size)
        assertEquals(SettingsRes.strings.settings_legal_pending.desc(), documents[0].status)
        assertTrue(documents[0].onClick != null)
        assertEquals(SettingsRes.strings.settings_legal_unknown.desc(), documents[1].status)
        assertNull(documents[1].onClick)
        assertTrue(sut.uiState.legal.accept.isVisible)
    }

    @Test
    fun `refreshes when no job is active regardless of presentation loading flags`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.sut()
        sut.uiState.legal.isLoading = true
        sut.uiState.legal.accept.isLoading = true

        whenn()
        sut.onViewPresented()

        then()
        verifySuspend(VerifyMode.exactly(1)) { fixture.getLegalDocumentStatuses.refresh() }
        assertEquals(listOf("privacy-v2", "terms-v3"), sut.uiState.legal.documents.map { it.version })
    }

    @Test
    fun `drops repeated refreshes while a refresh job is active`() = runUnitTest {
        given()
        val fixture = Fixture()
        val response = CompletableDeferred<List<LegalDocumentStatus>>()
        everySuspend { fixture.remoteStatuses.refresh() } calls { response.await() }
        val sut = fixture.sut()
        sut.onViewPresented()

        whenn()
        sut.uiState.legal.isLoading = false
        sut.onViewPresented()
        response.complete(listOf(privacy()))

        then()
        verifySuspend(VerifyMode.exactly(1)) { fixture.getLegalDocumentStatuses.refresh() }
        assertEquals("privacy-v2", sut.uiState.legal.documents.first().version)
    }

    @Test
    fun `drops repeated acceptance while an acceptance job is active`() = runUnitTest {
        given()
        val fixture = Fixture()
        val save = CompletableDeferred<Unit>()
        everySuspend { fixture.acceptLegalDocuments(any()) } calls { save.await() }
        val sut = fixture.sut()
        sut.onViewPresented()
        val accept = sut.uiState.legal.accept.onClick

        whenn()
        accept?.invoke()
        accept?.invoke()
        save.complete(Unit)

        then()
        verifySuspend(VerifyMode.exactly(1)) { fixture.acceptLegalDocuments(any()) }
        verifySuspend(VerifyMode.exactly(2)) { fixture.getLegalDocumentStatuses.refresh() }
    }

    @Test
    fun `renders cached documents while the remote refresh is pending`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.statuses.value = listOf(privacy(version = "cached-v1"))
        val response = CompletableDeferred<List<LegalDocumentStatus>>()
        everySuspend { fixture.remoteStatuses.refresh() } calls { response.await() }
        val sut = fixture.sut()

        whenn()
        sut.onViewPresented()
        val versions = sut.uiState.legal.documents.map { it.version }
        val wasEnabled = sut.uiState.legal.accept.isEnabled
        response.complete(listOf(privacy()))

        then()
        assertEquals(listOf("cached-v1", null), versions)
        assertTrue(wasEnabled)
        assertEquals(listOf("privacy-v2", null), sut.uiState.legal.documents.map { it.version })
    }

    @Test
    fun `recovers from a refresh failure when cached statuses are unchanged`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.statuses.value = listOf(privacy())
        everySuspend { fixture.remoteStatuses.refresh() } throws TestException()
        val sut = fixture.sut()
        sut.onViewPresented()
        everySuspend { fixture.remoteStatuses.refresh() } returns listOf(privacy())

        whenn()
        sut.uiState.legal.retry.onClick?.invoke()

        then()
        assertEquals(SettingsRes.strings.settings_legal_consent.desc(), sut.uiState.legal.message)
        assertTrue(!sut.uiState.legal.retry.isVisible)
        assertTrue(sut.uiState.legal.accept.isEnabled)
    }

    @Test
    fun `renders changes from the local flow without another refresh`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.sut()
        sut.onViewPresented()

        whenn()
        fixture.statuses.value = listOf(privacy(accepted = true, version = "updated-v4"))

        then()
        assertEquals("updated-v4", sut.uiState.legal.documents.first().version)
        assertTrue(!sut.uiState.legal.accept.isVisible)
        verifySuspend(VerifyMode.exactly(1)) { fixture.getLegalDocumentStatuses.refresh() }
    }

    @Test
    fun `renders the flow snapshot instead of the refresh return value`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.statuses.value = listOf(privacy(version = "cached-v1"))
        everySuspend { fixture.getLegalDocumentStatuses.refresh() } returns listOf(terms())
        val sut = fixture.sut()

        whenn()
        sut.onViewPresented()

        then()
        assertEquals("cached-v1", sut.uiState.legal.documents.first().version)
    }

    @Test
    fun `keeps document rows and callbacks unchanged until the flow emits`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.statuses.value = listOf(privacy(version = "cached-v1"))
        val refreshed = listOf(privacy(version = "fresh-v2"))
        everySuspend { fixture.getLegalDocumentStatuses.refresh() } returns refreshed
        val sut = fixture.sut()
        val cachedRows = sut.uiState.legal.documents
        val cachedCallback = sut.uiState.legal.accept.onClick

        whenn()
        sut.onViewPresented()
        val rowsAfterRefresh = sut.uiState.legal.documents
        val callbackAfterRefresh = sut.uiState.legal.accept.onClick
        fixture.statuses.value = refreshed

        then()
        assertSame(cachedRows, rowsAfterRefresh)
        assertSame(cachedCallback, callbackAfterRefresh)
        assertTrue(sut.uiState.legal.accept.isEnabled)
        assertEquals("fresh-v2", sut.uiState.legal.documents.first().version)
    }

    @Test
    fun `keeps cached rows and acceptance available when refreshing fails`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.statuses.value = listOf(privacy(version = "cached-v1"))
        everySuspend { fixture.remoteStatuses.refresh() } throws TestException()
        val sut = fixture.sut()

        whenn()
        sut.onViewPresented()

        then()
        assertEquals("cached-v1", sut.uiState.legal.documents.first().version)
        assertTrue(sut.uiState.legal.retry.isVisible)
        assertTrue(sut.uiState.notification.presentationNotification.isEmpty())
        assertTrue(sut.uiState.legal.accept.isVisible)
        assertTrue(sut.uiState.legal.accept.isEnabled)
    }

    @Test
    fun `restores unknown rows when the local cache is cleared`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.sut()
        sut.onViewPresented()

        whenn()
        fixture.statuses.value = emptyList()

        then()
        assertEquals(2, sut.uiState.legal.documents.size)
        assertTrue(sut.uiState.legal.documents.all { it.onClick == null })
        assertTrue(!sut.uiState.legal.accept.isVisible)
        verifySuspend(VerifyMode.exactly(0)) { fixture.acceptLegalDocuments(any()) }
    }

    @Test
    fun `shows local observation errors inline without a notification`() = runUnitTest {
        given()
        val fixture = Fixture()
        every { fixture.getLegalDocumentStatuses.flow() } returns failOnceThenSuspend()
        val sut = fixture.sut()

        whenn()
        sut.onViewPresented()

        then()
        assertEquals(SettingsRes.strings.settings_legal_load_error.desc(), sut.uiState.legal.message)
        assertTrue(sut.uiState.notification.presentationNotification.isEmpty())
        assertTrue(!sut.uiState.legal.accept.isVisible)
    }

    @Test
    fun `renders profile and color scheme when presented`() = runUnitTest {
        given()
        val sut = Fixture().sut()

        whenn()
        sut.onViewPresented()

        then()
        assertEquals(UIColorScheme.DARK, sut.uiState.appearance.colorScheme)
        assertEquals("Anna".desc(), sut.uiState.profile.name)
        assertEquals("Smith".desc(), sut.uiState.profile.lastName)
        assertEquals("anna@example.com".desc(), sut.uiState.profile.email)
    }

    @Test
    fun `shows mapped error when settings cannot be loaded`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.getSettings() } throws TestException()
        val sut = fixture.sut()

        whenn()
        sut.onViewPresented()

        then()
        fixture.errorMapper.assertMappedSingle(TestException::class)
    }

    @Test
    fun `saves selected color scheme`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.updateColorScheme.invoke(any()) } returns Unit
        val sut = fixture.sut()

        whenn()
        sut.onSchemeSelected(UIColorScheme.LIGHT)

        then()
        verifySuspend { fixture.updateColorScheme.invoke(ColorScheme.LIGHT) }
        assertEquals(UIColorScheme.LIGHT, sut.uiState.appearance.colorScheme)
    }

    @Test
    fun `plays success haptic when color scheme is saved`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.updateColorScheme.invoke(any()) } returns Unit
        val sut = fixture.sut()

        whenn()
        sut.onSchemeSelected(UIColorScheme.LIGHT)

        then()
        assertEquals(
            1,
            sut.uiState.notification.presentationNotification.count {
                it is PresentationNotification.SuccessHaptic
            },
        )
    }

    @Test
    fun `does not play success haptic when color scheme save fails`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.updateColorScheme.invoke(any()) } throws TestException()
        val sut = fixture.sut()

        whenn()
        sut.onSchemeSelected(UIColorScheme.LIGHT)

        then()
        assertTrue(
            sut.uiState.notification.presentationNotification.none {
                it is PresentationNotification.SuccessHaptic
            },
        )
    }

    @Test
    fun `keeps previous scheme when saving fails`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.updateColorScheme.invoke(any()) } throws TestException()
        val sut = fixture.sut()

        whenn()
        sut.onSchemeSelected(UIColorScheme.LIGHT)

        then()
        assertEquals(UIColorScheme.SYSTEM, sut.uiState.appearance.colorScheme)
        fixture.errorMapper.assertMappedSingle(TestException::class)
    }

    @Test
    fun `asks for confirmation before logging out`() = runUnitTest {
        given()
        val sut = Fixture().sut()

        whenn()
        sut.onLogOutClick()

        then()
        sut.uiState.notification.assertSingle<PresentationNotification.Message>()
    }

    @Test
    fun `posts unauthorized after logout is confirmed`() = runUnitTest {
        given()
        val sut = Fixture().sut()
        sut.onLogOutClick()
        val dialog = sut.uiState.notification.assertSingle<PresentationNotification.Message>()
        sut.uiState.notification.removeFirst()

        whenn()
        dialog.tap(ActionType.NEGATIVE)

        then()
        assertEquals(
            listOf<PresentationNotification>(PresentationNotification.Unauthorized),
            sut.uiState.notification.presentationNotification,
        )
    }

    @Test
    fun `navigates to edit profile from app bar action`() = runUnitTest {
        given()
        val sut = Fixture().sut()

        whenn()
        sut.uiState.appBar.actions.items.single().onClick()

        then()
        assertEquals(
            listOf<SettingsDashboardDestination>(SettingsDashboardDestination.EditProfile),
            sut.uiState.navigation.navigationDestination,
        )
    }

    @Test
    fun `shows current versions and acceptance status`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.sut()

        whenn()
        sut.onViewPresented()

        then()
        val documents = sut.uiState.legal.documents
        assertEquals(listOf("privacy-v2", "terms-v3"), documents.map { it.version })
        assertEquals(SettingsRes.strings.settings_legal_pending.desc(), documents[0].status)
        assertEquals(SettingsRes.strings.settings_legal_accepted.desc(), documents[1].status)
        assertTrue(sut.uiState.legal.accept.isVisible)
    }

    @Test
    fun `opens the published privacy URL without accepting`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.sut()
        sut.onViewPresented()

        whenn()
        sut.uiState.legal.documents[0].onClick?.invoke()

        then()
        verify { fixture.deviceFacade.openUrlPreview("https://bookk.test/privacy") }
        verifySuspend(VerifyMode.exactly(0)) { fixture.acceptLegalDocuments(any()) }
    }

    @Test
    fun `opens the published terms URL without accepting`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.sut()
        sut.onViewPresented()

        whenn()
        sut.uiState.legal.documents[1].onClick?.invoke()

        then()
        verify { fixture.deviceFacade.openUrlPreview("https://bookk.test/terms") }
        verifySuspend(VerifyMode.exactly(0)) { fixture.acceptLegalDocuments(any()) }
    }

    @Test
    fun `submits only pending displayed versions`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.sut()
        sut.onViewPresented()

        whenn()
        sut.uiState.legal.accept.onClick?.invoke()

        then()
        verifySuspend {
            fixture.acceptLegalDocuments(AcceptedLegalDocuments(privacyPolicy = "privacy-v2"))
        }
    }

    @Test
    fun `submits both pending versions`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.remoteStatuses.refresh() } returns
            listOf(privacy(), terms(accepted = false))
        val sut = fixture.sut()
        sut.onViewPresented()

        whenn()
        sut.uiState.legal.accept.onClick?.invoke()

        then()
        verifySuspend {
            fixture.acceptLegalDocuments(AcceptedLegalDocuments("privacy-v2", "terms-v3"))
        }
    }

    @Test
    fun `accepts a terms only publication`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.remoteStatuses.refresh() } returns listOf(terms(accepted = false))
        val sut = fixture.sut()
        sut.onViewPresented()

        whenn()
        sut.uiState.legal.accept.onClick?.invoke()

        then()
        verifySuspend {
            fixture.acceptLegalDocuments(AcceptedLegalDocuments(termsOfService = "terms-v3"))
        }
    }

    @Test
    fun `hides acceptance when all current versions are accepted`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.remoteStatuses.refresh() } returns
            listOf(privacy(accepted = true), terms())
        val sut = fixture.sut()

        whenn()
        sut.onViewPresented()

        then()
        assertTrue(!sut.uiState.legal.accept.isVisible)
        verifySuspend(VerifyMode.exactly(0)) { fixture.acceptLegalDocuments(any()) }
    }

    @Test
    fun `keeps both static rows when no documents are published`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.remoteStatuses.refresh() } returns emptyList()
        val sut = fixture.sut()

        whenn()
        sut.onViewPresented()

        then()
        assertEquals(2, sut.uiState.legal.documents.size)
        assertTrue(sut.uiState.legal.documents.all { it.onClick == null })
        assertNull(sut.uiState.legal.message)
        assertTrue(!sut.uiState.legal.accept.isVisible)
    }

    @Test
    fun `hides acceptance before any documents are loaded`() = runUnitTest {
        given()
        val fixture = Fixture()
        val response = CompletableDeferred<List<LegalDocumentStatus>>()
        everySuspend { fixture.remoteStatuses.refresh() } calls { response.await() }
        val sut = fixture.sut()
        sut.onViewPresented()

        whenn()
        val wasLoading = sut.uiState.legal.isLoading
        val wasVisible = sut.uiState.legal.accept.isVisible
        response.complete(emptyList())

        then()
        assertTrue(wasLoading)
        assertTrue(!wasVisible)
        verifySuspend(VerifyMode.exactly(0)) { fixture.acceptLegalDocuments(any()) }
    }

    @Test
    fun `offers retry and blocks acceptance after loading failure`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.remoteStatuses.refresh() } throws TestException()
        val sut = fixture.sut()

        whenn()
        sut.onViewPresented()

        then()
        assertTrue(sut.uiState.legal.retry.isVisible)
        assertEquals(
            SettingsRes.strings.settings_legal_load_error.desc(),
            sut.uiState.legal.message,
        )
        assertTrue(sut.uiState.notification.presentationNotification.isEmpty())
        assertTrue(!sut.uiState.legal.accept.isVisible)
        verifySuspend(VerifyMode.exactly(0)) { fixture.acceptLegalDocuments(any()) }
    }

    @Test
    fun `recovers from a failed load through retry`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.remoteStatuses.refresh() } throws TestException()
        val sut = fixture.sut()
        sut.onViewPresented()

        whenn()
        everySuspend { fixture.remoteStatuses.refresh() } returns listOf(privacy())
        sut.uiState.legal.retry.onClick?.invoke()

        then()
        assertEquals(2, sut.uiState.legal.documents.size)
        assertTrue(!sut.uiState.legal.retry.isVisible)
        assertTrue(sut.uiState.legal.accept.isEnabled)
    }

    @Test
    fun `refreshes server acceptance after saving`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.sut()
        sut.onViewPresented()

        whenn()
        everySuspend { fixture.remoteStatuses.refresh() } returns
            listOf(privacy(accepted = true), terms())
        sut.uiState.legal.accept.onClick?.invoke()

        then()
        assertTrue(!sut.uiState.legal.accept.isVisible)
        assertEquals(
            SettingsRes.strings.settings_legal_accepted.desc(),
            sut.uiState.legal.documents[0].status,
        )
    }

    @Test
    fun `keeps a newly published version pending after saving`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.sut()
        sut.onViewPresented()

        whenn()
        everySuspend { fixture.remoteStatuses.refresh() } returns
            listOf(privacy(version = "privacy-v4"))
        sut.uiState.legal.accept.onClick?.invoke()

        then()
        assertEquals("privacy-v4", sut.uiState.legal.documents.first().version)
        assertEquals(
            SettingsRes.strings.settings_legal_pending.desc(),
            sut.uiState.legal.documents.first().status,
        )
        verifySuspend {
            fixture.acceptLegalDocuments(AcceptedLegalDocuments(privacyPolicy = "privacy-v2"))
        }
    }

    @Test
    fun `reloads unknown versions and requires another explicit acceptance`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.acceptLegalDocuments(any()) } throws
            AcceptLegalDocuments.Error.UnknownVersion()
        val sut = fixture.sut()
        sut.onViewPresented()

        whenn()
        everySuspend { fixture.remoteStatuses.refresh() } returns
            listOf(privacy(version = "privacy-v4"))
        sut.uiState.legal.accept.onClick?.invoke()

        then()
        assertEquals("privacy-v4", sut.uiState.legal.documents.first().version)
        assertTrue(sut.uiState.legal.accept.isEnabled)
        verifySuspend(VerifyMode.exactly(1)) { fixture.acceptLegalDocuments(any()) }
        assertTrue(
            sut.uiState.notification.presentationNotification.any {
                it is PresentationNotification.GlobalMessage
            },
        )
    }

    @Test
    fun `submits the versions from the current document callback`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.sut()
        sut.onViewPresented()
        everySuspend { fixture.remoteStatuses.refresh() } returns
            listOf(privacy(version = "privacy-v4"))
        sut.onViewPresented()

        whenn()
        sut.uiState.legal.accept.onClick?.invoke()

        then()
        verifySuspend(VerifyMode.exactly(1)) {
            fixture.acceptLegalDocuments(AcceptedLegalDocuments(privacyPolicy = "privacy-v4"))
        }
    }

    @Test
    fun `deduplicates acceptance while allowing a separate refresh`() = runUnitTest {
        given()
        val fixture = Fixture()
        val save = CompletableDeferred<Unit>()
        everySuspend { fixture.acceptLegalDocuments(any()) } calls { save.await() }
        val sut = fixture.sut()
        sut.onViewPresented()

        whenn()
        val accept = sut.uiState.legal.accept.onClick
        accept?.invoke()
        accept?.invoke()
        sut.onViewPresented()
        val wasSaving = sut.uiState.legal.accept.isLoading
        save.complete(Unit)

        then()
        assertTrue(wasSaving)
        verifySuspend(VerifyMode.exactly(1)) { fixture.acceptLegalDocuments(any()) }
        verifySuspend(VerifyMode.exactly(3)) { fixture.getLegalDocumentStatuses.refresh() }
    }

    @Test
    fun `plays success haptic when legal acceptance is saved`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.sut()
        sut.onViewPresented()

        whenn()
        sut.uiState.legal.accept.onClick?.invoke()

        then()
        assertEquals(
            1,
            sut.uiState.notification.presentationNotification.count {
                it is PresentationNotification.SuccessHaptic
            },
        )
    }

    @Test
    fun `does not play success haptic when legal acceptance fails`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.acceptLegalDocuments(any()) } throws TestException()
        val sut = fixture.sut()
        sut.onViewPresented()

        whenn()
        sut.uiState.legal.accept.onClick?.invoke()

        then()
        assertTrue(
            sut.uiState.notification.presentationNotification.none {
                it is PresentationNotification.SuccessHaptic
            },
        )
        fixture.errorMapper.assertMappedSingle(TestException::class)
        assertTrue(sut.uiState.legal.accept.isEnabled)
    }

    @Test
    fun `keeps cached pending documents available when post acceptance refresh fails`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.sut()
        sut.onViewPresented()

        whenn()
        everySuspend { fixture.remoteStatuses.refresh() } throws TestException()
        sut.uiState.legal.accept.onClick?.invoke()

        then()
        assertEquals(listOf("privacy-v2", "terms-v3"), sut.uiState.legal.documents.map { it.version })
        assertTrue(sut.uiState.legal.retry.isVisible)
        assertTrue(sut.uiState.legal.accept.isVisible)
        assertTrue(sut.uiState.legal.accept.isEnabled)
    }

    @Test
    fun `explains explicit acceptance beside pending documents`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.sut()

        whenn()
        sut.onViewPresented()

        then()
        assertEquals(SettingsRes.strings.settings_legal_consent.desc(), sut.uiState.legal.message)
        assertEquals(
            SettingsRes.strings.settings_legal_accept.desc(),
            sut.uiState.legal.accept.text,
        )
    }

    @Test
    fun `distinguishes pending documents from quiet accepted states`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.sut()

        whenn()
        sut.onViewPresented()

        then()
        val documents = sut.uiState.legal.documents
        assertEquals(ColorToken.ActionText, documents[0].statusColor)
        assertEquals(ColorToken.SecondaryText, documents[1].statusColor)
    }

    @Test
    fun `keeps document rows and acceptance available while refreshing`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.sut()
        sut.onViewPresented()
        val displayed = sut.uiState.legal.documents
        val response = CompletableDeferred<List<LegalDocumentStatus>>()
        everySuspend { fixture.remoteStatuses.refresh() } calls { response.await() }

        whenn()
        sut.onViewPresented()
        val refreshingDocuments = sut.uiState.legal.documents
        val wasLoading = sut.uiState.legal.isLoading
        val wasEnabled = sut.uiState.legal.accept.isEnabled
        response.complete(listOf(privacy(), terms()))

        then()
        assertEquals(displayed, refreshingDocuments)
        assertTrue(wasLoading)
        assertTrue(wasEnabled)
        verifySuspend(VerifyMode.exactly(0)) { fixture.acceptLegalDocuments(any()) }
    }

    @Test
    fun `removes the consent explanation after all documents are accepted`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.remoteStatuses.refresh() } returns
            listOf(privacy(accepted = true), terms())
        val sut = fixture.sut()

        whenn()
        sut.onViewPresented()

        then()
        assertEquals(null, sut.uiState.legal.message)
        assertTrue(!sut.uiState.legal.accept.isVisible)
    }

    companion object {
        private fun privacy(
            accepted: Boolean = false,
            version: String = "privacy-v2",
        ): LegalDocumentStatus {
            return status(LegalDocument.PRIVACY_POLICY, version, "privacy", accepted)
        }

        private fun terms(accepted: Boolean = true): LegalDocumentStatus {
            return status(LegalDocument.TERMS_OF_SERVICE, "terms-v3", "terms", accepted)
        }

        private fun status(
            document: LegalDocument,
            version: String,
            path: String,
            accepted: Boolean,
        ): LegalDocumentStatus {
            return LegalDocumentStatus(
                LegalDocumentVersion(
                    document,
                    version,
                    "https://bookk.test/$path",
                    Instant.parse("2026-11-01T00:00:00Z"),
                ),
                if (accepted) Instant.parse("2026-11-02T00:00:00Z") else null,
            )
        }
    }
}
