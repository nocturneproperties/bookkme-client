//
//  FirebaseDelegate.swift
//  iosApp
//
//  Created by BookkMe on 08.07.2026.
//  Copyright © 2026 BookkMe. All rights reserved.
//
import UIKit
import FirebaseCore
import FirebaseCrashlytics
import FirebaseInstallations
import FirebaseMessaging
import shared

class FirebaseDelegate: NSObject, UIApplicationDelegate, UNUserNotificationCenterDelegate, MessagingDelegate {

	func application(
		_ application: UIApplication,
		didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey : Any]? = nil
	) -> Bool {
		FirebaseApp.configure()
		Messaging.messaging().delegate = self
		UNUserNotificationCenter.current().delegate = self
		obtainAndRegisterInstallationId()
		registerCrashReporter()
		return true
	}
	
	func application(
		_ application: UIApplication,
		didRegisterForRemoteNotificationsWithDeviceToken deviceToken: Data
	) {
		Messaging.messaging().apnsToken = deviceToken
	}
	
	private func obtainAndRegisterInstallationId() {
		Task {
			if let id = try? await Installations.installations().installationID() {
				TokenBridge.shared.updateInstallationId(token: id)
			}
		}
	}

	private func registerCrashReporter() {
		CrashReporter.shared.handler = { name, reason, message in
			let error = NSError(
				domain: name,
				code: 0,
				userInfo: [NSLocalizedDescriptionKey: message ?? reason, "reason": reason]
			)
			Crashlytics.crashlytics().record(error: error)
		}
	}

}
