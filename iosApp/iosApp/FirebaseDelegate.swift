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
		return true
	}
	
	func messaging(_ messaging: Messaging, didReceiveRegistration installationId: String?) {
		if let id = installationId {
			TokenBridge.shared.updateInstallationId(token: id)
		}
	}

	func userNotificationCenter(
		_ center: UNUserNotificationCenter,
		willPresent notification: UNNotification,
		withCompletionHandler completionHandler: @escaping (UNNotificationPresentationOptions) -> Void
	) {
		completionHandler([.banner, .sound, .badge])
	}
}
