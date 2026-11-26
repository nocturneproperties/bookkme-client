package me.bookk.feature.settings.domain.impl

import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifySuspend
import kotlinx.datetime.LocalDateTime
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.feature.authorization.domain.datasource.registration.PassKeyManager
import me.bookk.feature.settings.domain.api.entity.Passkey
import me.bookk.feature.settings.domain.datasource.passkey.AccountPasskeys
import me.bookk.feature.settings.domain.datasource.passkey.PasskeySettingsDataSource
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.uuid.Uuid

class GetAvailablePasskeysImplTest {
    private class Fixture {
        val dataSource = mock<PasskeySettingsDataSource>()
        val passKeyManager = mock<PassKeyManager> {
            everySuspend { signalAcceptedPasskeys(any()) } returns Unit
        }
        val sut = GetAvailablePasskeysImpl(dataSource, passKeyManager)

        fun stubPasskeys(credentialIds: List<String> = listOf("AAECAw")): AccountPasskeys {
            return AccountPasskeys(
                relyingParty = "bookkme.app",
                userHandle = "dXNlcg",
                passkeys = credentialIds.map { stubPasskey() },
                credentialIds = credentialIds,
            ).also { everySuspend { dataSource.getPasskeys() } returns it }
        }

        private fun stubPasskey() = Passkey(
            id = Uuid.random(),
            name = "iPhone 15",
            isBackedUp = true,
            createdAt = LocalDateTime(2024, 1, 1, 0, 0),
            lastUsedAt = LocalDateTime(2024, 6, 1, 0, 0),
        )
    }

    @Test
    fun `returns the account's passkeys`() = runUnitTest {
        given()
        val fixture = Fixture()
        val account = fixture.stubPasskeys()

        whenn()
        val result = fixture.sut()

        then()
        assertEquals(account.passkeys, result)
    }

    @Test
    fun `tells the device which passkeys the account still accepts`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubPasskeys(listOf("AAECAw", "BAUGBw"))

        whenn()
        fixture.sut()

        then()
        verifySuspend {
            fixture.passKeyManager.signalAcceptedPasskeys(
                PassKeyManager.AcceptedPasskeys(
                    relyingParty = "bookkme.app",
                    userHandle = "dXNlcg",
                    credentialIds = listOf("AAECAw", "BAUGBw"),
                ),
            )
        }
    }

    @Test
    fun `does not tell the device anything when a passkey's credential id is unknown`() =
        runUnitTest {
            given()
            val fixture = Fixture()
            fixture.stubPasskeys(listOf("AAECAw", ""))

            whenn()
            fixture.sut()

            then()
            verifySuspend(VerifyMode.not) { fixture.passKeyManager.signalAcceptedPasskeys(any()) }
        }

    @Test
    fun `returns the passkeys when the device can't be told`() = runUnitTest {
        given()
        val fixture = Fixture()
        val account = fixture.stubPasskeys()
        everySuspend { fixture.passKeyManager.signalAcceptedPasskeys(any()) } throws
            IllegalStateException("provider unavailable")

        whenn()
        val result = fixture.sut()

        then()
        assertEquals(account.passkeys, result)
    }

    @Test
    fun `tells the device the accepted passkeys in the background data fetch`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubPasskeys()

        whenn()
        fixture.sut.doOnBackgroundDataFetch()

        then()
        verifySuspend { fixture.passKeyManager.signalAcceptedPasskeys(any()) }
    }
}
