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
    
    @Bindable
    var notificationState: IOSNotificationState

    @State var showAlert: Bool = false
	@State var data: PresentationNotificationMessage? = nil
    
    func body(content: Content) -> some View {
        content
			.onChange(of: notificationState.presentationNotification.count) {
				if let value = notificationState.presentationNotification.first {
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
            }
            .alert(data?.title?.localized() ?? "", isPresented: $showAlert, presenting: data) { message in
				alertButtons(buttons: message.buttons)
            } message: { error in
                Text(error.message.localized())
            }
    }
	
	@ViewBuilder
	func alertButtons(buttons: [ButtonDescriptor]) -> some View {
		ForEach(buttons, id: \.id) { button in
			let role = button.actionType == ButtonDescriptor.ActionType.negative ? ButtonRole.destructive : ButtonRole.cancel
			
			Button(role: role) {
				self.showAlert = false
				button.onClick()
				self.notificationState.removeFirst()
			} label: {
				Text(button.text.localized())
			}
		}
	}
}

extension View {
	func handleNotifications(state: PresentationNotificationState) -> some View {
        modifier(NotificationHandler(notificationState: state.impl()))
    }
}
