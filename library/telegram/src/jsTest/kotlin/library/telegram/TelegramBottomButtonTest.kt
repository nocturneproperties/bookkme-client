package library.telegram

import dev.icerock.moko.resources.desc.desc
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.designsystem.uistate.startLoading
import me.bookk.site.designsystem.uistate.WebButtonState
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TelegramBottomButtonTest {

    @Test
    fun `shows an enabled button in the active colors`() = runUnitTest {
        given()
        val state = WebButtonState(text = "Continue".desc())

        whenn()
        val spec = state.bottomButtonSpec("Continue", COLORS)

        then()
        assertEquals(
            BottomButtonSpec(
                text = "Continue",
                isVisible = true,
                isActive = true,
                isProgressVisible = false,
                color = ACTIVE,
                textColor = ON_ACTIVE,
            ),
            spec,
        )
    }

    @Test
    fun `hides the button when its state is hidden`() = runUnitTest {
        given()
        val state = WebButtonState(isVisible = false)

        whenn()
        val spec = state.bottomButtonSpec("Retry", COLORS)

        then()
        assertFalse(spec.isVisible)
    }

    @Test
    fun `hides the button while it has no text`() = runUnitTest {
        given()
        val state = WebButtonState()

        whenn()
        val spec = state.bottomButtonSpec("  ", COLORS)

        then()
        assertFalse(spec.isVisible)
    }

    @Test
    fun `shows a disabled button inactive in the inactive colors`() = runUnitTest {
        given()
        val state = WebButtonState(isEnabled = false)

        whenn()
        val spec = state.bottomButtonSpec("Continue", COLORS)

        then()
        assertEquals(
            listOf(false, false, INACTIVE, ON_INACTIVE),
            listOf(spec.isActive, spec.isProgressVisible, spec.color, spec.textColor),
        )
    }

    @Test
    fun `shows progress on an inactive button while loading`() = runUnitTest {
        given()
        val state = WebButtonState().apply { startLoading() }

        whenn()
        val spec = state.bottomButtonSpec("Continue", COLORS)

        then()
        assertEquals(listOf(true, false), listOf(spec.isProgressVisible, spec.isActive))
    }

    @Test
    fun `shortens text longer than telegram accepts`() = runUnitTest {
        given()
        val state = WebButtonState()

        whenn()
        val spec = state.bottomButtonSpec("a".repeat(100), COLORS)

        then()
        assertEquals("a".repeat(63) + "…", spec.text)
    }

    @Test
    fun `keeps a disabled button inactive once its progress ends`() = runUnitTest {
        given()
        val button = FakeBottomButton()
        val loading = WebButtonState().apply { startLoading() }.bottomButtonSpec("Continue", COLORS)
        button.render(loading)
        val disabled = WebButtonState(isEnabled = false).bottomButtonSpec("Continue", COLORS)

        whenn()
        button.render(disabled)

        then()
        assertEquals(
            listOf(true, false, false),
            listOf(button.isVisible, button.isActive, button.isProgressVisible),
        )
    }

    @Test
    fun `shows the progress of a loading button`() = runUnitTest {
        given()
        val button = FakeBottomButton()
        val loading = WebButtonState().apply { startLoading() }.bottomButtonSpec("Continue", COLORS)

        whenn()
        button.render(loading)

        then()
        assertEquals(
            listOf(true, false, true),
            listOf(button.isVisible, button.isActive, button.isProgressVisible),
        )
    }

    @Test
    fun `hides a button without sending its blank text`() = runUnitTest {
        given()
        val button = FakeBottomButton()
        button.render(WebButtonState().bottomButtonSpec("Continue", COLORS))

        whenn()
        button.render(WebButtonState().bottomButtonSpec("", COLORS))

        then()
        assertEquals(listOf(false, "Continue"), listOf(button.isVisible, button.text))
    }

    @Test
    fun `sends the text and colors of a visible button`() = runUnitTest {
        given()
        val button = FakeBottomButton()

        whenn()
        button.render(WebButtonState().bottomButtonSpec("Continue", COLORS))

        then()
        assertTrue(button.isVisible)
        assertEquals(
            listOf("Continue", ACTIVE, ON_ACTIVE),
            listOf(button.text, button.color, button.textColor),
        )
    }

    private class FakeBottomButton : TelegramBottomButton {
        var text = ""
        var color = ""
        var textColor = ""
        var isVisible = false
        var isActive = true
        var isProgressVisible = false

        override fun setParams(params: TelegramBottomButtonParams): TelegramBottomButton {
            params.text?.let {
                require(it.isNotBlank()) { "Bottom button text is required" }
                text = it
            }
            params.color?.let { color = it }
            params.textColor?.let { textColor = it }
            params.isActive?.let { isActive = it }
            params.isVisible?.let {
                require(!it || text.isNotEmpty()) { "Bottom button text is required" }
                isVisible = it
            }
            return this
        }

        override fun hide(): TelegramBottomButton {
            isVisible = false
            return this
        }

        override fun showProgress(leaveActive: Boolean): TelegramBottomButton {
            isActive = leaveActive
            isProgressVisible = true
            return this
        }

        override fun hideProgress(): TelegramBottomButton {
            isActive = true
            isProgressVisible = false
            return this
        }

        override fun onClick(callback: () -> Unit): TelegramBottomButton {
            return this
        }

        override fun offClick(callback: () -> Unit): TelegramBottomButton {
            return this
        }
    }

    private companion object {
        const val ACTIVE = "#000001"
        const val ON_ACTIVE = "#000002"
        const val INACTIVE = "#000003"
        const val ON_INACTIVE = "#000004"
        val COLORS = BottomButtonColors(ACTIVE, ON_ACTIVE, INACTIVE, ON_INACTIVE)
    }
}
