import FirebaseCrashlytics
import shared

final class IOSCrashReporter: CrashReporter {

	func isCollectionEnabled() -> Bool {
		Crashlytics.crashlytics().isCrashlyticsCollectionEnabled()
	}

	func setCollectionEnabled(enabled: Bool) {
		Crashlytics.crashlytics().setCrashlyticsCollectionEnabled(enabled)
	}

	func deleteUnsentReports() {
		Crashlytics.crashlytics().deleteUnsentReports()
	}
}
