package me.bookk.designsystem.components

import kotlinx.datetime.LocalDate

private const val TOUCH_SCREEN_MIN_POINTS = 1
private val AppleTouchDevices = Regex("iPhone|iPad|iPod")

enum class DateCommitMode {
    ON_CHANGE,
    ON_CLOSE,
    ;

    companion object {
        fun of(userAgent: String, maxTouchPoints: Int): DateCommitMode {
            val isIpadReportingMac =
                userAgent.contains("Macintosh") && maxTouchPoints > TOUCH_SCREEN_MIN_POINTS
            return when (AppleTouchDevices.containsMatchIn(userAgent) || isIpadReportingMac) {
                true -> ON_CLOSE
                false -> ON_CHANGE
            }
        }
    }
}

class DatePickerCommit(
    private val mode: DateCommitMode,
) {
    private var heldDate: LocalDate? = null

    fun change(date: LocalDate?): LocalDate? {
        if (mode == DateCommitMode.ON_CHANGE) return date
        heldDate = date
        return null
    }

    fun close(): LocalDate? {
        return heldDate.also { heldDate = null }
    }
}
