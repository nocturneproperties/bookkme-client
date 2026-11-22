package me.bookk.feature.business.presentation.screen.link

import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import dev.mokkery.answering.calls
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify
import dev.mokkery.verifySuspend
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.MutableStateFlow
import library.device.api.DeviceFacade
import me.bookk.android.feature.business.resources.BusinessRes
import me.bookk.core.config.SiteUrl
import me.bookk.core.domain.entity.Error
import me.bookk.core.presentation.VmArgs
import me.bookk.core.presentation.error.PresentationNotification
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.designsystem.test.FakeErrorMapper
import me.bookk.designsystem.test.FakeTextFieldState
import me.bookk.designsystem.test.TestException
import me.bookk.designsystem.test.ViewModelTestDispatchers
import me.bookk.designsystem.test.assertMappedSingle
import me.bookk.designsystem.test.assertSingle
import me.bookk.designsystem.test.failOnceThenSuspend
import me.bookk.designsystem.uistate.ValidationState
import me.bookk.feature.business.domain.api.entity.BusinessLink
import me.bookk.feature.business.domain.api.link.GetBusinessLink
import me.bookk.feature.business.domain.api.link.SetBusinessLink
import me.bookk.feature.business.presentation.FakeBusinessStateFactory
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.uuid.Uuid

class BusinessLinkViewModelTest {

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
        val businessId = Uuid.random()
        val cached = MutableStateFlow<BusinessLink?>(null)
        val getBusinessLink = mock<GetBusinessLink> {
            every { flow(businessId) } returns cached
        }
        val setBusinessLink = mock<SetBusinessLink>()
        val device = mock<DeviceFacade>()
        val errorMapper = FakeErrorMapper()

        fun sut() = BusinessLinkViewModel(
            businessId = businessId,
            getBusinessLink = getBusinessLink,
            setBusinessLink = setBusinessLink,
            device = device,
            siteUrl = SiteUrl("https://bookkme.test"),
            stateFactory = FakeBusinessStateFactory(),
            vmArgs = VmArgs(errorMapper),
        )

        fun hasLink(name: String?) {
            cached.value = name?.let { BusinessLink(businessId, it) }
            everySuspend { getBusinessLink.refresh(businessId) } returns Unit
        }

        fun refreshFindsLink(name: String) {
            everySuspend { getBusinessLink.refresh(businessId) } calls {
                cached.value = BusinessLink(businessId, name)
            }
        }

        fun savesAs(name: String) {
            everySuspend { setBusinessLink(businessId, any()) } calls {
                BusinessLink(businessId, name).also { cached.value = it }
            }
        }

        fun saveFailsWith(error: Throwable) {
            everySuspend { setBusinessLink(businessId, any()) } throws error
        }

        fun refreshFailsWith(error: Throwable) {
            everySuspend { getBusinessLink.refresh(businessId) } throws error
        }
    }

    private val BusinessLinkViewModel.nameField: FakeTextFieldState
        get() = uiState.name as FakeTextFieldState

    private fun BusinessLinkViewModel.save(name: String) {
        nameField.type(name)
        uiState.save.onClick?.invoke()
    }

    private fun BusinessLinkViewModel.fieldError(): StringDesc? {
        return uiState.name.supportingTextRes.takeIf {
            uiState.name.validationState == ValidationState.ERROR
        }
    }

    private fun BusinessLinkViewModel.messages(): List<PresentationNotification.Message> {
        return uiState.notifications.presentationNotification
            .filterIsInstance<PresentationNotification.Message>()
    }

    @Test
    fun `shows the saved link and its preview`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.hasLink("my-salon")

        whenn()
        val sut = fixture.sut()

        then()
        assertEquals("my-salon", sut.uiState.name.text)
        assertEquals("bookkme.test/my-salon".desc(), sut.uiState.preview)
        assertTrue(sut.uiState.share.isVisible)
        assertFalse(sut.uiState.refresh.isRefreshing)
    }

    @Test
    fun `shows the saved link when it cannot be refreshed`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.cached.value = BusinessLink(fixture.businessId, "my-salon")
        fixture.refreshFailsWith(TestException())

        whenn()
        val sut = fixture.sut()

        then()
        assertEquals("my-salon", sut.uiState.name.text)
        assertTrue(sut.uiState.share.isVisible)
    }

    @Test
    fun `shows the link found by the refresh`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.refreshFindsLink("my-salon")

        whenn()
        val sut = fixture.sut()

        then()
        assertEquals("my-salon", sut.uiState.name.text)
        assertEquals("bookkme.test/my-salon".desc(), sut.uiState.preview)
    }

    @Test
    fun `keeps the typed name when the link arrives after typing started`() = runUnitTest {
        given()
        val fixture = Fixture()
        val refreshed = CompletableDeferred<Unit>()
        everySuspend { fixture.getBusinessLink.refresh(fixture.businessId) } calls {
            refreshed.await()
            fixture.cached.value = BusinessLink(fixture.businessId, "my-salon")
        }
        val sut = fixture.sut()
        sut.nameField.type("draft")

        whenn()
        refreshed.complete(Unit)

        then()
        assertEquals("draft", sut.uiState.name.text)
        assertEquals("bookkme.test/draft".desc(), sut.uiState.preview)
        assertTrue(sut.uiState.share.isVisible)
    }

    @Test
    fun `leaves the name length to the link rules`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.hasLink(null)

        whenn()
        val sut = fixture.sut()

        then()
        assertEquals(Int.MAX_VALUE, sut.uiState.name.maxLength)
    }

    @Test
    fun `shows the refresh indicator while the link is being fetched`() = runUnitTest {
        given()
        val fixture = Fixture()
        val refreshed = CompletableDeferred<Unit>()
        everySuspend { fixture.getBusinessLink.refresh(fixture.businessId) } calls {
            refreshed.await()
        }

        whenn()
        val sut = fixture.sut()

        then()
        assertTrue(sut.uiState.refresh.isRefreshing)
        refreshed.complete(Unit)
        assertFalse(sut.uiState.refresh.isRefreshing)
    }

    @Test
    fun `leaves the name empty and hides sharing when the business has no link`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.hasLink(null)

        whenn()
        val sut = fixture.sut()

        then()
        assertEquals("", sut.uiState.name.text)
        assertEquals("bookkme.test/".desc(), sut.uiState.preview)
        assertFalse(sut.uiState.share.isVisible)
    }

    @Test
    fun `shows the load error without a notification when refreshing fails`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.refreshFailsWith(TestException())

        whenn()
        val sut = fixture.sut()

        then()
        assertEquals(BusinessRes.strings.business_link_load_error.desc(), sut.uiState.loadError)
        assertTrue(sut.uiState.notifications.presentationNotification.isEmpty())
        assertFalse(sut.uiState.refresh.isRefreshing)
    }

    @Test
    fun `explains in the load error that only the owner can manage the link`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.refreshFailsWith(GetBusinessLink.Error.NotOwner())

        whenn()
        val sut = fixture.sut()

        then()
        assertEquals(
            BusinessRes.strings.business_link_error_not_owner.desc(),
            sut.uiState.loadError,
        )
    }

    @Test
    fun `hands suspended access to the notifications instead of the load error`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.refreshFailsWith(Error.BusinessAccessSuspended("suspended"))

        whenn()
        val sut = fixture.sut()

        then()
        assertNull(sut.uiState.loadError)
        fixture.errorMapper.assertMappedSingle(Error.BusinessAccessSuspended::class)
    }

    @Test
    fun `shows nothing for an ignorable load failure`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.refreshFailsWith(Error.Ignore(TestException()))

        whenn()
        val sut = fixture.sut()

        then()
        assertNull(sut.uiState.loadError)
        assertTrue(sut.uiState.notifications.presentationNotification.isEmpty())
    }

    @Test
    fun `hands an expired session to the notifications instead of the load error`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.refreshFailsWith(Error.Unauthorized(TestException()))

        whenn()
        val sut = fixture.sut()

        then()
        assertNull(sut.uiState.loadError)
        fixture.errorMapper.assertMappedSingle(Error.Unauthorized::class)
    }

    @Test
    fun `shows a notification when the saved link cannot be read`() = runUnitTest {
        given()
        val fixture = Fixture()
        every { fixture.getBusinessLink.flow(fixture.businessId) } returns failOnceThenSuspend()
        fixture.hasLink(null)

        whenn()
        fixture.sut()

        then()
        fixture.errorMapper.assertMappedSingle(TestException::class)
    }

    @Test
    fun `pulling to refresh fetches the link again and clears the load error`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.refreshFailsWith(TestException())
        val sut = fixture.sut()
        fixture.refreshFindsLink("my-salon")

        whenn()
        sut.uiState.refresh.onRefresh()

        then()
        assertNull(sut.uiState.loadError)
        assertEquals("my-salon", sut.uiState.name.text)
    }

    @Test
    fun `disables saving while the name is empty`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.hasLink(null)

        whenn()
        val sut = fixture.sut()

        then()
        assertFalse(sut.uiState.save.isEnabled)
    }

    @Test
    fun `enables saving once the saved link is shown`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.hasLink("my-salon")

        whenn()
        val sut = fixture.sut()

        then()
        assertTrue(sut.uiState.save.isEnabled)
    }

    @Test
    fun `disables saving while the typed name breaks the link rules`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.hasLink("my-salon")
        val sut = fixture.sut()
        val invalidNames =
            listOf("ab", "a".repeat(33), "my.salon", "my_salon", "-salon", "my--salon")

        whenn()
        val results = invalidNames.map {
            sut.nameField.type(it)
            sut.uiState.save.isEnabled
        }

        then()
        assertEquals(List(invalidNames.size) { false }, results)
    }

    @Test
    fun `enables saving when the typed name follows the link rules after normalizing`() =
        runUnitTest {
            given()
            val fixture = Fixture()
            fixture.hasLink(null)
            val sut = fixture.sut()

            whenn()
            sut.nameField.type(" My-Salon-2 ")

            then()
            assertTrue(sut.uiState.save.isEnabled)
        }

    @Test
    fun `keeps saving disabled after saving ends when the name became invalid meanwhile`() =
        runUnitTest {
            given()
            val fixture = Fixture()
            fixture.hasLink(null)
            val saved = CompletableDeferred<BusinessLink>()
            everySuspend { fixture.setBusinessLink(fixture.businessId, any()) } calls {
                saved.await()
            }
            val sut = fixture.sut()
            sut.save("my-salon")
            sut.nameField.type("ab")

            whenn()
            saved.completeExceptionally(SetBusinessLink.Error.Taken())

            then()
            assertFalse(sut.uiState.save.isLoading)
            assertFalse(sut.uiState.save.isEnabled)
        }

    @Test
    fun `keeps saving disabled while a save is in progress`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.hasLink(null)
        everySuspend { fixture.setBusinessLink(fixture.businessId, any()) } calls {
            CompletableDeferred<BusinessLink>().await()
        }
        val sut = fixture.sut()
        sut.save("my-salon")

        whenn()
        sut.nameField.type("my-salon-2")

        then()
        assertFalse(sut.uiState.save.isEnabled)
    }

    @Test
    fun `removes whitespace from the name while typing`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.hasLink(null)
        val sut = fixture.sut()

        whenn()
        sut.nameField.type(" my sa\tlon\n")

        then()
        assertEquals("mysalon", sut.uiState.name.text)
        assertEquals("bookkme.test/mysalon".desc(), sut.uiState.preview)
        assertTrue(sut.uiState.save.isEnabled)
    }

    @Test
    fun `updates the preview with the normalized name while typing`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.hasLink(null)
        val sut = fixture.sut()

        whenn()
        sut.nameField.type(" My-Salon ")

        then()
        assertEquals("bookkme.test/my-salon".desc(), sut.uiState.preview)
    }

    @Test
    fun `saves the typed name for the business`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.hasLink(null)
        fixture.savesAs("my-salon")
        val sut = fixture.sut()

        whenn()
        sut.save("My-Salon")

        then()
        verifySuspend { fixture.setBusinessLink(fixture.businessId, "My-Salon") }
    }

    @Test
    fun `shows the saved link and offers sharing it`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.hasLink(null)
        fixture.savesAs("my-salon")
        val sut = fixture.sut()

        whenn()
        sut.save("My-Salon")

        then()
        assertEquals("my-salon", sut.uiState.name.text)
        assertEquals("bookkme.test/my-salon".desc(), sut.uiState.preview)
        assertTrue(sut.uiState.share.isVisible)
        assertEquals(
            BusinessRes.strings.business_link_saved.desc(),
            sut.uiState.notifications.presentationNotification
                .filterIsInstance<PresentationNotification.GlobalMessage>().single().text,
        )
    }

    @Test
    fun `plays success haptic when the link is saved`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.hasLink(null)
        fixture.savesAs("my-salon")
        val sut = fixture.sut()

        whenn()
        sut.save("my-salon")

        then()
        assertTrue(
            PresentationNotification.SuccessHaptic in
                sut.uiState.notifications.presentationNotification,
        )
    }

    @Test
    fun `does not play success haptic when saving the link fails`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.hasLink(null)
        fixture.saveFailsWith(SetBusinessLink.Error.Taken())
        val sut = fixture.sut()

        whenn()
        sut.save("my-salon")

        then()
        assertFalse(
            PresentationNotification.SuccessHaptic in
                sut.uiState.notifications.presentationNotification,
        )
    }

    @Test
    fun `stops the save loading when saving ends`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.hasLink(null)
        fixture.saveFailsWith(SetBusinessLink.Error.Taken())
        val sut = fixture.sut()

        whenn()
        sut.save("my-salon")

        then()
        assertFalse(sut.uiState.save.isLoading)
    }

    @Test
    fun `shows the naming rules on the field when the server rejects the name`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.hasLink(null)
        fixture.saveFailsWith(SetBusinessLink.Error.Invalid())
        val sut = fixture.sut()

        whenn()
        sut.save("my-salon")

        then()
        assertEquals(BusinessRes.strings.business_link_error_invalid.desc(), sut.fieldError())
    }

    @Test
    fun `explains on the field that a reserved name cannot be used`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.hasLink(null)
        fixture.saveFailsWith(SetBusinessLink.Error.Reserved())
        val sut = fixture.sut()

        whenn()
        sut.save("admin")

        then()
        assertEquals(BusinessRes.strings.business_link_error_reserved.desc(), sut.fieldError())
    }

    @Test
    fun `explains on the field that the name is taken`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.hasLink(null)
        fixture.saveFailsWith(SetBusinessLink.Error.Taken())
        val sut = fixture.sut()

        whenn()
        sut.save("my-salon")

        then()
        assertEquals(BusinessRes.strings.business_link_error_taken.desc(), sut.fieldError())
    }

    @Test
    fun `clears the field error when the name changes`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.hasLink(null)
        fixture.saveFailsWith(SetBusinessLink.Error.Taken())
        val sut = fixture.sut()
        sut.save("my-salon")

        whenn()
        sut.nameField.type("my-salon-2")

        then()
        assertNull(sut.fieldError())
    }

    @Test
    fun `explains the change limit when the link was changed too often`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.hasLink("old-name")
        fixture.saveFailsWith(SetBusinessLink.Error.ChangeLimitReached())
        val sut = fixture.sut()

        whenn()
        sut.save("new-name")

        then()
        assertEquals(
            BusinessRes.strings.business_link_error_change_limit.desc(),
            sut.messages().single().message,
        )
        assertNull(sut.fieldError())
    }

    @Test
    fun `explains that only the owner can change the link`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.hasLink(null)
        fixture.saveFailsWith(SetBusinessLink.Error.NotOwner())
        val sut = fixture.sut()

        whenn()
        sut.save("my-salon")

        then()
        assertEquals(
            BusinessRes.strings.business_link_error_not_owner.desc(),
            sut.messages().single().message,
        )
    }

    @Test
    fun `shows mapped error when saving fails unexpectedly`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.hasLink(null)
        fixture.saveFailsWith(TestException())
        val sut = fixture.sut()

        whenn()
        sut.save("my-salon")

        then()
        fixture.errorMapper.assertMappedSingle(TestException::class)
        sut.uiState.notifications.assertSingle<PresentationNotification.GlobalMessage>()
    }

    @Test
    fun `shares the saved link instead of the unsaved text`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.hasLink("my-salon")
        every { fixture.device.share(any()) } returns Unit
        val sut = fixture.sut()
        sut.nameField.type("draft-name")

        whenn()
        sut.uiState.share.onClick?.invoke()

        then()
        verify { fixture.device.share("https://bookkme.test/my-salon") }
    }

    @Test
    fun `navigates back from the app bar`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.hasLink(null)
        val sut = fixture.sut()

        whenn()
        sut.uiState.appBar.onBackClick?.invoke()

        then()
        assertEquals(
            listOf<BusinessLinkDestinations>(BusinessLinkDestinations.Back),
            sut.uiState.navigation.navigationDestination,
        )
    }
}
