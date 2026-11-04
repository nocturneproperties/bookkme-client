package me.bookk.feature.authorization.domain.impl

import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.matcher.matches
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifySuspend
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.feature.authorization.domain.api.ConfirmSensitiveAction
import me.bookk.feature.authorization.domain.datasource.authorization.AuthorizationDataSource
import me.bookk.feature.authorization.domain.datasource.authorization.ServerAuthenticationChallenge
import me.bookk.feature.authorization.domain.datasource.registration.PassKeyManager
import me.bookk.feature.authorization.domain.datasource.registration.PasskeyVerificationPayload
import me.bookk.feature.authorization.domain.entity.SensitiveAction
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.uuid.Uuid

@OptIn(ExperimentalCoroutinesApi::class)
class ConfirmSensitiveActionImplTest {
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
        val id = Uuid.parse("00000000-0000-0000-0000-000000000001")
        val dataSource = mock<AuthorizationDataSource>()
        val passKeyManager = mock<PassKeyManager>()
        val sut = ConfirmSensitiveActionImpl(dataSource, passKeyManager)
        init {
            everySuspend { dataSource.getAuthorizationChallenge() } returns
                ServerAuthenticationChallenge("request", "challengeJson", "challenge")
            everySuspend { passKeyManager.authorize(any()) } returns
                PasskeyVerificationPayload("assertion")
            everySuspend { dataSource.confirmSensitiveAction(any()) } returns "single-use"
        }
    }

    @Test
    fun `confirms the exact action and target with a fresh passkey assertion`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val result = fixture.sut(SensitiveAction.DELETE_CLIENT, fixture.id, fixture.id)

        then()
        assertEquals("single-use", result)
        verifySuspend {
            fixture.dataSource.confirmSensitiveAction(
                matches({ "target and assertion" }) {
                    it.action == SensitiveAction.DELETE_CLIENT && it.businessId == fixture.id &&
                        it.targetId == fixture.id &&
                        it.requestId == "request" && it.publicKeyCredentialJson == "assertion"
                },
            )
        }
    }

    @Test
    fun `maps confirmation error 6 to ChallengeExpired`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.dataSource.confirmSensitiveAction(any()) } throws
            me.bookk.core.domain.entity.Error.BusinessError(6, "rejected")

        whenn()
        val result =
            runCatching { fixture.sut(SensitiveAction.DELETE_CLIENT, fixture.id, fixture.id) }

        then()
        assertFailsWith<ConfirmSensitiveAction.Error.ChallengeExpired> { result.getOrThrow() }
    }

    @Test
    fun `maps confirmation error 4 to VerificationFailed`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.dataSource.confirmSensitiveAction(any()) } throws
            me.bookk.core.domain.entity.Error.BusinessError(4, "rejected")

        whenn()
        val result =
            runCatching { fixture.sut(SensitiveAction.DELETE_CLIENT, fixture.id, fixture.id) }

        then()
        assertFailsWith<ConfirmSensitiveAction.Error.VerificationFailed> { result.getOrThrow() }
    }

    @Test
    fun `maps confirmation error 7 to PasskeyNotFound`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.dataSource.confirmSensitiveAction(any()) } throws
            me.bookk.core.domain.entity.Error.BusinessError(7, "rejected")

        whenn()
        val result =
            runCatching { fixture.sut(SensitiveAction.DELETE_CLIENT, fixture.id, fixture.id) }

        then()
        assertFailsWith<ConfirmSensitiveAction.Error.PasskeyNotFound> { result.getOrThrow() }
    }

    @Test
    fun `maps confirmation error 11 to PasskeyNotOwned`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.dataSource.confirmSensitiveAction(any()) } throws
            me.bookk.core.domain.entity.Error.BusinessError(11, "rejected")

        whenn()
        val result =
            runCatching { fixture.sut(SensitiveAction.DELETE_CLIENT, fixture.id, fixture.id) }

        then()
        assertFailsWith<ConfirmSensitiveAction.Error.PasskeyNotOwned> { result.getOrThrow() }
    }

    @Test
    fun `cancelling passkey confirmation never requests a token`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.passKeyManager.authorize(any()) } throws
            PassKeyManager.Error.UserCancelled()

        whenn()
        val result =
            runCatching { fixture.sut(SensitiveAction.DELETE_CLIENT, fixture.id, fixture.id) }

        then()
        assertFailsWith<me.bookk.core.domain.entity.Error.Ignore> { result.getOrThrow() }
        verifySuspend(VerifyMode.exactly(0)) { fixture.dataSource.confirmSensitiveAction(any()) }
    }
}
