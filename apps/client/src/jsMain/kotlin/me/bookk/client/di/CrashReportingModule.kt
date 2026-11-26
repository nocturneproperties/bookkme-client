package me.bookk.client.di

import library.crashreporting.api.CrashReporter
import org.koin.core.module.Module
import org.koin.dsl.module

internal class DisabledCrashReporter : CrashReporter {

    override fun isCollectionEnabled(): Boolean {
        return false
    }

    override fun setCollectionEnabled(enabled: Boolean) {
        return
    }

    override fun deleteUnsentReports() {
        return
    }
}

internal fun crashReportingModule(): Module = module {
    single<CrashReporter> { DisabledCrashReporter() }
}
