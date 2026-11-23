package me.bookk.designsystem.resources

import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.format
import me.bookk.android.designsystem.resources.DesignSystem
import kotlin.time.Duration

private const val MINUTES_IN_HOUR = 60

fun Duration.toStringDesc(): StringDesc {
    val hours = inWholeHours.toInt()
    val minutes = (inWholeMinutes % MINUTES_IN_HOUR).toInt()
    return when {
        hours == 0 -> DesignSystem.strings.common_duration_minutes.format(minutes)
        minutes == 0 -> DesignSystem.strings.common_duration_hours.format(hours)
        else -> DesignSystem.strings.common_duration_hours_minutes.format(hours, minutes)
    }
}
