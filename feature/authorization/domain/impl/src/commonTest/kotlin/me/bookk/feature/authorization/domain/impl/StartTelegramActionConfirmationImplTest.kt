package me.bookk.feature.authorization.domain.impl

import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.feature.authorization.domain.datasource.authorization.AuthorizationDataSource
import me.bookk.feature.authorization.domain.datasource.authorization.TelegramActionConfirmationStart
import me.bookk.feature.authorization.domain.entity.SensitiveAction
import kotlin.test.Test
import kotlin.uuid.Uuid

class StartTelegramActionConfirmationImplTest {
    private class Fixture {
        val userId = Uuid.parse("00000000-0000-0000-0000-000000000001")
        val dataSource = mock<AuthorizationDataSource> {
            everySuspend { startTelegramActionConfirmation(any()) } returns Unit
        }
        val sut = StartTelegramActionConfirmationImpl(dataSource)
    }

    @Test
    fun `asks the server to send the confirmation for the action and target`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        fixture.sut(SensitiveAction.EXPORT_PERSONAL_DATA, Uuid.NIL, fixture.userId)

        then()
        verifySuspend {
            fixture.dataSource.startTelegramActionConfirmation(
                TelegramActionConfirmationStart(
                    action = SensitiveAction.EXPORT_PERSONAL_DATA,
                    businessId = Uuid.NIL,
                    targetId = fixture.userId,
                ),
            )
        }
    }
}
