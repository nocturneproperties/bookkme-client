package me.bookk.feature.settings.presentation.passkey

import dev.icerock.moko.resources.desc.desc
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifySuspend
import kotlinx.datetime.LocalDateTime
import me.bookk.core.presentation.VmArgs
import me.bookk.core.presentation.error.ActionType
import me.bookk.core.presentation.error.PresentationNotification
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.designsystem.test.FakeDateLocalizer
import me.bookk.designsystem.test.FakeErrorMapper
import me.bookk.designsystem.test.TestException
import me.bookk.designsystem.test.ViewModelTestDispatchers
import me.bookk.designsystem.test.assertMappedSingle
import me.bookk.designsystem.test.assertSingle
import me.bookk.designsystem.test.tap
import me.bookk.feature.settings.domain.api.CreateNewPasskey
import me.bookk.feature.settings.domain.api.DeletePasskey
import me.bookk.feature.settings.domain.api.GetAvailablePasskeys
import me.bookk.feature.settings.domain.api.entity.Passkey
import me.bookk.feature.settings.presentation.FakeSettingsStateFactory
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.uuid.Uuid

class PasskeyViewModelTest {

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
        val deletePasskey = mock<DeletePasskey>()
        val createNewPasskey = mock<CreateNewPasskey>()
        val getAvailablePasskeys = mock<GetAvailablePasskeys> {
            everySuspend { invoke() } returns emptyList()
        }
        val errorMapper = FakeErrorMapper()

        fun sut() = PasskeyViewModel(
            deletePasskey = deletePasskey,
            createNewPasskey = createNewPasskey,
            getAvailablePasskeys = getAvailablePasskeys,
            dateLocalizer = FakeDateLocalizer(),
            settingsStateFactory = FakeSettingsStateFactory(),
            vmArgs = VmArgs(errorMapper)
        )
    }

    private fun passkey(name: String = "iPhone") = Passkey(
        id = Uuid.random(),
        name = name,
        isBackedUp = true,
        createdAt = LocalDateTime(2024, 1, 1, 0, 0),
        lastUsedAt = LocalDateTime(2024, 1, 2, 0, 0)
    )

    @Test
    fun `loads passkeys on start and stops refreshing`() = runUnitTest {
        given()
        val fixture = Fixture()
        val key = passkey()
        everySuspend { fixture.getAvailablePasskeys() } returns listOf(key)

        whenn()
        val sut = fixture.sut()

        then()
        assertEquals(listOf(key.id), sut.uiState.passkeys.map { it.id })
        assertFalse(sut.uiState.refresh.isRefreshing)
    }

    @Test
    fun `single passkey is not deletable`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.getAvailablePasskeys() } returns listOf(passkey())

        whenn()
        val sut = fixture.sut()

        then()
        assertFalse(sut.uiState.passkeys.single().isDeletable)
    }

    @Test
    fun `passkeys are deletable when there are several`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.getAvailablePasskeys() } returns listOf(passkey("a"), passkey("b"))

        whenn()
        val sut = fixture.sut()

        then()
        assertTrue(sut.uiState.passkeys.all { it.isDeletable })
    }

    @Test
    fun `adds passkey and reloads list`() = runUnitTest {
        given()
        val fixture = Fixture()
        val added = passkey("New")
        everySuspend { fixture.createNewPasskey() } returns listOf(added)
        val sut = fixture.sut()
        everySuspend { fixture.getAvailablePasskeys() } returns listOf(added)

        whenn()
        sut.onAddPasskeyClick()

        then()
        verifySuspend { fixture.createNewPasskey() }
        assertEquals(listOf(added.id), sut.uiState.passkeys.map { it.id })
        assertTrue(sut.uiState.addPasskeyButton.isEnabled)
        assertFalse(sut.uiState.addPasskeyButton.isLoading)
    }

    @Test
    fun `shows mapped error when adding passkey fails`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.createNewPasskey() } throws TestException()
        val sut = fixture.sut()

        whenn()
        sut.onAddPasskeyClick()

        then()
        fixture.errorMapper.assertMappedSingle(TestException::class)
        assertTrue(sut.uiState.addPasskeyButton.isEnabled)
    }

    @Test
    fun `deletes passkey after confirmation`() = runUnitTest {
        given()
        val fixture = Fixture()
        val key = passkey()
        everySuspend { fixture.deletePasskey(any()) } returns Unit
        val sut = fixture.sut()
        sut.onDeletePasskeyClick(PasskeyState.PasskeyItem(key.id, key.name, true, true, "".desc()))

        whenn()
        sut.uiState.notification.assertSingle<PresentationNotification.Message>().tap(ActionType.NEGATIVE)

        then()
        verifySuspend { fixture.deletePasskey(key.id) }
    }

    @Test
    fun `deletes every passkey of a batch after confirmation`() = runUnitTest {
        given()
        val fixture = Fixture()
        val ids = listOf(Uuid.random(), Uuid.random())
        everySuspend { fixture.deletePasskey(any()) } returns Unit
        val sut = fixture.sut()
        sut.onDeletePasskeyList(ids.map { PasskeyState.PasskeyItem(it, "key", true, true, "".desc()) })

        whenn()
        sut.uiState.notification.assertSingle<PresentationNotification.Message>().tap(ActionType.NEGATIVE)

        then()
        verifySuspend(VerifyMode.exactly(2)) { fixture.deletePasskey(any()) }
    }

    @Test
    fun `shows mapped error when passkeys cannot be loaded`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.getAvailablePasskeys() } throws TestException()

        whenn()
        val sut = fixture.sut()

        then()
        fixture.errorMapper.assertMappedSingle(TestException::class)
        assertFalse(sut.uiState.refresh.isRefreshing)
    }
}
