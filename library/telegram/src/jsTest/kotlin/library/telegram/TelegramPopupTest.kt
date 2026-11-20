package library.telegram

import dev.icerock.moko.resources.desc.desc
import me.bookk.core.presentation.error.ActionType
import me.bookk.core.presentation.error.ButtonDescriptor
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class TelegramPopupTest {

    @Test
    fun `keeps the title, message and buttons`() = runUnitTest {
        given()
        val ok = ButtonDescriptor("OK".desc())

        whenn()
        val popup = TelegramPopup.create(
            "Title",
            "Message",
            listOf(TelegramPopupButton.of(ok, "OK")),
        )

        then()
        assertEquals(
            listOf("Title", "Message", listOf(ok.id.toString() to "OK")),
            listOf(popup.title, popup.message, popup.buttons.map { it.id to it.text }),
        )
    }

    @Test
    fun `drops a blank title`() = runUnitTest {
        given()
        val title = " "

        whenn()
        val popup = TelegramPopup.create(title, "Message", emptyList())

        then()
        assertNull(popup.title)
    }

    @Test
    fun `offers a close button when there are no buttons`() = runUnitTest {
        given()
        val buttons = emptyList<TelegramPopupButton>()

        whenn()
        val popup = TelegramPopup.create(null, "Message", buttons)

        then()
        assertEquals(listOf(TelegramPopupButtonType.CLOSE), popup.buttons.map { it.type })
    }

    @Test
    fun `keeps at most the three buttons telegram accepts`() = runUnitTest {
        given()
        val buttons = (1..4).map { TelegramPopupButton.action("Button $it") {} }

        whenn()
        val popup = TelegramPopup.create(null, "Message", buttons)

        then()
        assertEquals(listOf("Button 1", "Button 2", "Button 3"), popup.buttons.map { it.text })
    }

    @Test
    fun `shortens a message longer than telegram accepts`() = runUnitTest {
        given()
        val message = "a".repeat(300)

        whenn()
        val popup = TelegramPopup.create(null, message, emptyList())

        then()
        assertEquals("a".repeat(255) + "…", popup.message)
    }

    @Test
    fun `shortens a title longer than telegram accepts`() = runUnitTest {
        given()
        val title = "a".repeat(100)

        whenn()
        val popup = TelegramPopup.create(title, "Message", emptyList())

        then()
        assertEquals("a".repeat(63) + "…", popup.title)
    }

    @Test
    fun `shows a negative action as a destructive button`() = runUnitTest {
        given()
        val delete = ButtonDescriptor("Delete".desc(), ActionType.NEGATIVE)

        whenn()
        val button = TelegramPopupButton.of(delete, "Delete")

        then()
        assertEquals(TelegramPopupButtonType.DESTRUCTIVE, button.type)
    }

    @Test
    fun `shows other actions as default buttons`() = runUnitTest {
        given()
        val actions = listOf(ActionType.POSITIVE, ActionType.CANCEL, ActionType.CONFIRM)

        whenn()
        val types = actions.map {
            TelegramPopupButton.of(ButtonDescriptor("x".desc(), it), "x").type
        }

        then()
        assertEquals(List(actions.size) { TelegramPopupButtonType.DEFAULT }, types)
    }

    @Test
    fun `runs the action of the tapped button`() = runUnitTest {
        given()
        val tapped = mutableListOf<String>()
        val keep = ButtonDescriptor("Keep".desc(), onClick = { tapped += "keep" })
        val delete = ButtonDescriptor("Delete".desc(), ActionType.NEGATIVE, onClick = {
            tapped +=
                "delete"
        })
        val popup = TelegramPopup.create(
            null,
            "Delete?",
            listOf(TelegramPopupButton.of(keep, "Keep"), TelegramPopupButton.of(delete, "Delete")),
        )

        whenn()
        popup.onClosed(delete.id.toString())

        then()
        assertEquals(listOf("delete"), tapped)
    }

    @Test
    fun `runs no action when the popup is dismissed`() = runUnitTest {
        given()
        val tapped = mutableListOf<String>()
        val popup = TelegramPopup.create(
            null,
            "Message",
            listOf(
                TelegramPopupButton.action("Open") {
                    tapped += "open"
                },
                TelegramPopupButton.close(),
            ),
        )

        whenn()
        popup.onClosed(null)
        popup.onClosed("")

        then()
        assertTrue(tapped.isEmpty())
    }

    @Test
    fun `offers the action of a global message next to close`() = runUnitTest {
        given()
        val tapped = mutableListOf<String>()

        whenn()
        val popup = TelegramPopup.globalMessage("Saved", "Undo") { tapped += "undo" }
        popup.onClosed(popup.buttons.first().id)

        then()
        assertEquals(
            listOf(TelegramPopupButtonType.DEFAULT, TelegramPopupButtonType.CLOSE),
            popup.buttons.map { it.type },
        )
        assertEquals(listOf("undo"), tapped)
    }

    @Test
    fun `offers only close for a global message without an action`() = runUnitTest {
        given()
        val actionText: String? = null

        whenn()
        val popup = TelegramPopup.globalMessage("Saved", actionText, null)

        then()
        assertEquals(listOf(TelegramPopupButtonType.CLOSE), popup.buttons.map { it.type })
    }

    @Test
    fun `leaves out an empty title and the text of a close button`() = runUnitTest {
        given()
        val popup = TelegramPopup.create(null, "Message", emptyList())

        whenn()
        val params = popup.toParams().asDynamic()

        then()
        assertEquals(
            listOf(false, false),
            listOf(params.hasOwnProperty("title"), params.buttons[0].hasOwnProperty("text")),
        )
    }
}
