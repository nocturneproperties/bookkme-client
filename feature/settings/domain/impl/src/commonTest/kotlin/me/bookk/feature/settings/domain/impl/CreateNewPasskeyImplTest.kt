package me.bookk.feature.settings.domain.impl

import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.mockk
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
        val passkeySettingsDataSource = mockk<PasskeySettingsDataSource>()
        val passKeyManager = mockk<PassKeyManager>()
        val getAvailablePasskeys = mockk<GetAvailablePasskeys>()
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
        val fixture = Fixture()
        val challenge = stubChallenge()
        val payload = PasskeyVerificationPayload("{}")
        val passkeys = listOf(stubPasskey())
        coEvery { fixture.passkeySettingsDataSource.getRegistrationChallengeForNewPasskey() } returns challenge
        coEvery { fixture.passKeyManager.create(any()) } returns payload
        coJustRun { fixture.passkeySettingsDataSource.sendVerifiedPasskey(any()) }
        coEvery { fixture.getAvailablePasskeys() } returns passkeys

        whenn()
        val result = fixture.sut()

        then()
        assertEquals(passkeys, result)
    }

    @Test
    fun `sends verified passkey with requestId from challenge`() = runUnitTest {
        given()
        val fixture = Fixture()
        val challenge = stubChallenge()
        val payload = PasskeyVerificationPayload("{\"key\":\"value\"}")
        coEvery { fixture.passkeySettingsDataSource.getRegistrationChallengeForNewPasskey() } returns challenge
        coEvery { fixture.passKeyManager.create(any()) } returns payload
        coJustRun { fixture.passkeySettingsDataSource.sendVerifiedPasskey(any()) }
        coEvery { fixture.getAvailablePasskeys() } returns emptyList()

        whenn()
        fixture.sut()

        then()
        coVerify {
            fixture.passkeySettingsDataSource.sendVerifiedPasskey(
                match { it.requestId == challenge.requestId && it.publicKeyCredentialJson == payload.jsonPayload }
            )
        }
    }

    @Test
    fun `throws Ignore when PassKeyManager throws UserCancelled`() = runUnitTest {
        given()
        val fixture = Fixture()
        coEvery { fixture.passkeySettingsDataSource.getRegistrationChallengeForNewPasskey() } returns stubChallenge()
        coEvery { fixture.passKeyManager.create(any()) } throws PassKeyManager.Error.UserCancelled()

        whenn()
        then()
        assertFailsWith<Error.Ignore> {
            fixture.sut()
        }
    }

    @Test
    fun `throws AccountCreationFailed when PassKeyManager throws other error`() = runUnitTest {
        given()
        val fixture = Fixture()
        coEvery { fixture.passkeySettingsDataSource.getRegistrationChallengeForNewPasskey() } returns stubChallenge()
        coEvery { fixture.passKeyManager.create(any()) } throws PassKeyManager.Error.Unknown(null)

        whenn()
        then()
        assertFailsWith<CreateNewPasskey.Error.AccountCreationFailed> {
            fixture.sut()
        }
    }
}
