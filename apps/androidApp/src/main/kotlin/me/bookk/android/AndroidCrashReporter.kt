package me.bookk.android

import com.google.firebase.crashlytics.FirebaseCrashlytics
import library.crashreporting.api.CrashReporter

internal class AndroidCrashReporter : CrashReporter {
    private val crashlytics: FirebaseCrashlytics
        get() = FirebaseCrashlytics.getInstance()

    override fun isCollectionEnabled(): Boolean {
        return crashlytics.isCrashlyticsCollectionEnabled
    }

    override fun setCollectionEnabled(enabled: Boolean) {
        crashlytics.isCrashlyticsCollectionEnabled = enabled
    }

    override fun deleteUnsentReports() {
        crashlytics.deleteUnsentReports()
    }
}
