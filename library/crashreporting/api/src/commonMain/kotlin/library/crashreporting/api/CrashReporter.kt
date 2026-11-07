package library.crashreporting.api

interface CrashReporter {
    fun isCollectionEnabled(): Boolean
    fun setCollectionEnabled(enabled: Boolean)
    fun deleteUnsentReports()
}
