package me.bookk.feature.settings.domain.api

interface UpdateCrashReportingEnabled {
    suspend operator fun invoke(enabled: Boolean)
}
