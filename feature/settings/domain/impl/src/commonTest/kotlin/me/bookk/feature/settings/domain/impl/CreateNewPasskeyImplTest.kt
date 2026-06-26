package me.bookk.feature.settings.domain.impl

import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.matcher.matches
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.LocalDateTime
import me.bookk.core.domain.entity.Error
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.feature.authorization.domain.datasource.registration.PassKeyManager
import me.bookk.feature.authorization.domain.datasource.registration.PasskeyVerificationPayload
import me.bookk.feature.settings.domain.api.CreateNewPasskey
import me.bookk.feature.settings.domain.api.GetAvailablePasskeys
import me.bookk.feature.settings.domain.api.entity.Passkey
import me.bookk.feature.settings.domain.datasource.passkey.AddPasskeyChallenge
import me.bookk.feature.settings.domain.datasource.passkey.PasskeySettingsDataSource
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.uuid.Uuid

@OptIn(ExperimentalCoroutinesApi::class)
class CreateNewPasskeyImplTest {

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
        val passkeySettingsDataSource = mock<PasskeySettingsDataSource>()
        val passKeyManager = mock<PassKeyManager>()
        val getAvailablePasskeys = mock<GetAvailablePasskeys>()
        val sut = CreateNewPasskeyImpl(passkeySettingsDataSource, passKeyManager, getAvailablePasskeys)
    }

    private fun stubChallenge() = AddPasskeyChallenge(
        requestId = "req-id",
        displayName = "John Doe",
        jsonChallengeData = "{}",
        challenge = "challenge-bytes",
        userHandle = "user-handle"
    )

    private fun stubPasskey() = Passkey(
        id = Uuid.random(), name = "iPhone", isBackedUp = true,
        createdAt = LocalDateTime(2024, 1, 1, 0, 0),
        lastUsedAt = LocalDateTime(2024, 6, 1, 0, 0)
    )

    @Test
    fun `returns updated passkeys list on success`() = runUnitTest {
        given()
        val sut = Fixture()
        val challenge = stubChallenge()
        val payload = PasskeyVerificationPayload("{}")
        val passkeys = listOf(stubPasskey())
        everySuspend { sut.passkeySettingsDataSource.getRegistrationChallengeForNewPasskey() } returns challenge
        everySuspend { sut.passKeyManager.create(any()) } returns payload
        everySuspend { sut.passkeySettingsDataSource.sendVerifiedPasskey(any()) } returns Unit
        everySuspend { sut.getAvailablePasskeys() } returns passkeys

        whenn()
        val result = sut.sut()

        then()
        assertEquals(passkeys, result)
    }

    @Test
    fun `sends verified passkey with requestId from challenge`() = runUnitTest {
        given()
        val sut = Fixture()
        val challenge = stubChallenge()
        val payload = PasskeyVerificationPayload("{\"key\":\"value\"}")
        everySuspend { sut.passkeySettingsDataSource.getRegistrationChallengeForNewPasskey() } returns challenge
        everySuspend { sut.passKeyManager.create(any()) } returns payload
        everySuspend { sut.passkeySettingsDataSource.sendVerifiedPasskey(any()) } returns Unit
        everySuspend { sut.getAvailablePasskeys() } returns emptyList()

        whenn()
        sut.sut()

        then()
        verifySuspend {
            sut.passkeySettingsDataSource.sendVerifiedPasskey(
                matches({ "match" }) { it.requestId == challenge.requestId && it.publicKeyCredentialJson == payload.jsonPayload }
            )
        }
    }

    @Test
    fun `throws Ignore when PassKeyManager throws UserCancelled`() = runUnitTest {
        given()
        val sut = Fixture()
        everySuspend { sut.passkeySettingsDataSource.getRegistrationChallengeForNewPasskey() } returns stubChallenge()
        everySuspend { sut.passKeyManager.create(any()) } throws PassKeyManager.Error.UserCancelled()

        whenn()
        then()
        assertFailsWith<Error.Ignore> {
            sut.sut()
        }
    }

    @Test
    fun `throws AccountCreationFailed when PassKeyManager throws other error`() = runUnitTest {
        given()
        val sut = Fixture()
        everySuspend { sut.passkeySettingsDataSource.getRegistrationChallengeForNewPasskey() } returns stubChallenge()
        everySuspend { sut.passKeyManager.create(any()) } throws PassKeyManager.Error.Unknown(null)

        whenn()
        then()
        assertFailsWith<CreateNewPasskey.Error.AccountCreationFailed> {
            sut.sut()
        }
    }
}
