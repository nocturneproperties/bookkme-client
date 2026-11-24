package me.bookk.designsystem.uistate

import me.bookk.core.presentation.error.PresentationNotification
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class WebNotificationStateTest {

    @Test
    fun `removes notifications in the order they were added`() = runUnitTest {
        given()
        val sut = WebNotificationState()
        sut.add(PresentationNotification.Ignore)
        sut.add(PresentationNotification.Unauthorized)

        whenn()
        sut.removeFirst()

        then()
        assertEquals(
            listOf<PresentationNotification>(PresentationNotification.Unauthorized),
            sut.presentationNotification.toList(),
        )
    }

    @Test
    fun `ignores removal when there is nothing queued`() = runUnitTest {
        given()
        val sut = WebNotificationState()

        whenn()
        sut.removeFirst()

        then()
        assertTrue(sut.presentationNotification.isEmpty())
    }
}
