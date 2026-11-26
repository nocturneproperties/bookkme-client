package me.bookk.feature.settings.domain.impl

import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.feature.authorization.domain.api.StartTelegramActionConfirmation
import me.bookk.feature.authorization.domain.api.UserProfileCRUD
import me.bookk.feature.authorization.domain.entity.SensitiveAction
import me.bookk.feature.authorization.domain.entity.UserProfile
import me.bookk.feature.settings.domain.api.entity.ActionOutcome
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.uuid.Uuid

class TelegramStartPersonalDataExportImplTest {
    private class Fixture {
        val userId = Uuid.parse("00000000-0000-0000-0000-000000000001")
        val startConfirmation = mock<StartTelegramActionConfirmation> {
            everySuspend { invoke(any(), any(), any()) } returns Unit
        }
        val profile = mock<UserProfileCRUD> {
            everySuspend { get() } returns UserProfile(userId, "Jane", "Doe", "jane@example.com")
        }
        val sut = TelegramStartPersonalDataExportImpl(startConfirmation, profile)
    }

    @Test
    fun `sends a personal data export confirmation to the telegram chat`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val result = fixture.sut()

        then()
        assertEquals(ActionOutcome.ConfirmationSent, result)
        verifySuspend {
            fixture.startConfirmation(
                SensitiveAction.EXPORT_PERSONAL_DATA,
                Uuid.NIL,
                fixture.userId,
            )
        }
    }
}
