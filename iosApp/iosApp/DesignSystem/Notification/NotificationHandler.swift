//
//  ErrorHandler.swift
//  iosApp
//
//  Created by Antiufieiev Michael on 24.01.2025.
//  Copyright © 2025 BookkMe. All rights reserved.
//

import SwiftUI
import Combine
import shared

struct NotificationHandler: ViewModifier {
    
    @ObservedObject
    var notificationState: IOSNotificationState

    @State var showAlert: Bool = false
	@State var data: PresentationNotificationMessage? = nil
    
    func body(content: Content) -> some View {
        content
			.onReceive(notificationState.$presentationNotification.flatMap(\.publisher)) { value in
                switch value {
                case is PresentationNotificationMessage:
                    data = value as? PresentationNotificationMessage
                    showAlert = true
                    break
				case is PresentationNotificationIgnore:
					notificationState.removeFirst()
                    break
				case is PresentationNotificationGlobalMessage:
					notificationState.removeFirst()
					break
                default:
					notificationState.removeFirst()
                    break
                }
            }
            .alert(data?.title?.localized() ?? "", isPresented: $showAlert, presenting: data) { error in
                Button(role: .cancel) {
                    showAlert = false
					notificationState.removeFirst()
                } label: {
                    Text(error.buttonText.localized())
                }
            } message: { error in
                Text(error.message.localized())
            }
    }
}

extension View {
	func handleNotifications(state: PresentationNotificationState) -> some View {
        modifier(NotificationHandler(notificationState: state.impl()))
    }
}
