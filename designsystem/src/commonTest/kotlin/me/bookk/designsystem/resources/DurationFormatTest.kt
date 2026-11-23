package me.bookk.designsystem.resources

import dev.icerock.moko.resources.format
import me.bookk.android.designsystem.resources.DesignSystem
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class DurationFormatTest {

    @Test
    fun `shows only minutes for a duration under an hour`() = runUnitTest {
        given()
        val duration = 45.minutes

        whenn()
        val text = duration.toStringDesc()

        then()
        assertEquals(DesignSystem.strings.common_duration_minutes.format(45), text)
    }

    @Test
    fun `shows only hours for a whole number of hours`() = runUnitTest {
        given()
        val duration = 2.hours

        whenn()
        val text = duration.toStringDesc()

        then()
        assertEquals(DesignSystem.strings.common_duration_hours.format(2), text)
    }

    @Test
    fun `shows hours and minutes for a duration with both`() = runUnitTest {
        given()
        val duration = 1.hours + 25.minutes

        whenn()
        val text = duration.toStringDesc()

        then()
        assertEquals(DesignSystem.strings.common_duration_hours_minutes.format(1, 25), text)
    }

    @Test
    fun `shows hours past a day without rolling over into days`() = runUnitTest {
        given()
        val duration = 26.hours + 5.minutes

        whenn()
        val text = duration.toStringDesc()

        then()
        assertEquals(DesignSystem.strings.common_duration_hours_minutes.format(26, 5), text)
    }

    @Test
    fun `drops seconds below a minute`() = runUnitTest {
        given()
        val duration = 30.minutes + 59.seconds

        whenn()
        val text = duration.toStringDesc()

        then()
        assertEquals(DesignSystem.strings.common_duration_minutes.format(30), text)
    }

    @Test
    fun `shows zero minutes for a zero duration`() = runUnitTest {
        given()
        val duration = Duration.ZERO

        whenn()
        val text = duration.toStringDesc()

        then()
        assertEquals(DesignSystem.strings.common_duration_minutes.format(0), text)
    }
}
