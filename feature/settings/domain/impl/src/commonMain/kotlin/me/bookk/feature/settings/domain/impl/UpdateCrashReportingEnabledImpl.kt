package me.bookk.feature.settings.domain.impl

import library.crashreporting.api.CrashReporter
import me.bookk.feature.settings.domain.api.UpdateCrashReportingEnabled

internal class UpdateCrashReportingEnabledImpl(
    private val crashReporter: CrashReporter,
) : UpdateCrashReportingEnabled {
    override suspend fun invoke(enabled: Boolean) {
        if (enabled) {
            crashReporter.deleteUnsentReports()
            crashReporter.setCollectionEnabled(true)
        } else {
            crashReporter.setCollectionEnabled(false)
            crashReporter.deleteUnsentReports()
        }
    }
}
