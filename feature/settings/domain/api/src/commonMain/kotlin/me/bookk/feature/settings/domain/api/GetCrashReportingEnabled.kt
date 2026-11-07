package me.bookk.feature.settings.domain.api

interface GetCrashReportingEnabled {
    suspend operator fun invoke(): Boolean
}
