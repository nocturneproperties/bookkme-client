package me.bookk.feature.settings.domain.impl

import library.crashreporting.api.CrashReporter
import me.bookk.feature.settings.domain.api.GetCrashReportingEnabled

internal class GetCrashReportingEnabledImpl(
    private val crashReporter: CrashReporter,
) : GetCrashReportingEnabled {
    override suspend fun invoke(): Boolean {
        return crashReporter.isCollectionEnabled()
    }
}
