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
	
	@EnvironmentObject var logOutHandler: LogOutHandler
    
    @Bindable
    var notificationState: IOSNotificationState

	@State var data: PresentationNotificationMessage? = nil
	@State var inputData: PresentationNotificationInputMessage? = nil

    func body(content: Content) -> some View {
        content
			.onChange(of: notificationState.presentationNotification.count) {
				if let value = notificationState.presentationNotification.first {
					switch value {
					case is PresentationNotificationMessage:
						data = value as? PresentationNotificationMessage
						break
					case is PresentationNotificationInputMessage:
						let message = value as? PresentationNotificationInputMessage
						inputData = message
						break
					case is PresentationNotificationIgnore:
						notificationState.removeFirst()
						break
					case is PresentationNotificationGlobalMessage:
						notificationState.removeFirst()
						break
					case is PresentationNotificationUnauthorized:
						notificationState.removeFirst()
						logOutHandler.onLogOut()
						break
					default:
						notificationState.removeFirst()
						break
					}
				}
            }
			.modifier(NotificationAlert(notificationState: notificationState, data: data))
			.modifier(NotificationInputAlert(notificationState: notificationState, data: inputData))
    }
}

struct NotificationAlert: ViewModifier {

	@State var isPresented = false

	let notificationState: IOSNotificationState
	let data: PresentationNotificationMessage?

	func body(content: Content) -> some View {
		content
			.onChange(of: data?.id) { _, newValue in
				isPresented = newValue != nil
			}
			.alert(data?.title?.localized() ?? "", isPresented: $isPresented, presenting: data) { message in
				alertContent(message: message)
			} message: { message in
				Text(message.message.localized())
			}
	}
	
	@ViewBuilder
	func alertContent(message: PresentationNotificationMessage) -> some View {
		ForEach(message.buttons, id: \.id) { button in
			let role = button.actionType == ActionType.negative ? ButtonRole.destructive : ButtonRole.cancel
			
			Button(role: role) {
				self.isPresented = false
				button.onClick()
				self.notificationState.removeFirst()
			} label: {
				Text(button.text.localized())
			}
		}
	}
}

struct NotificationInputAlert: ViewModifier {

	@State var inputText: String = ""
	@State var isPresented = false
	
	let notificationState: IOSNotificationState
	let data: PresentationNotificationInputMessage?

	func body(content: Content) -> some View {
		content
			.onChange(of: data?.id) { _, newValue in
				isPresented = newValue != nil
			}
			.alert(data?.title.localized() ?? "", isPresented: $isPresented, presenting: data) { message in
				alertContent(message: message)
			} message: { message in
				Text(message.message.localized())
			}
	}
	
	@ViewBuilder
	func alertContent(message: PresentationNotificationInputMessage) -> some View {
		TextField(message.placeholder?.localized() ?? "", text: $inputText)
		
		Button(role: .cancel) {
			self.isPresented = false
			message.onCancel()
			self.notificationState.removeFirst()
		} label: {
			Text(message.cancelText.localized())
		}
		
		let confirmRole: ButtonRole? = message.confirmActionType == ActionType.negative ? .destructive : nil
		Button(role: confirmRole) {
			self.isPresented = false
			message.onConfirm(inputText)
			self.notificationState.removeFirst()
		} label: {
			Text(message.confirmText.localized())
		}
	}
}


extension PresentationNotificationInputMessage:@retroactive Identifiable {}
extension PresentationNotificationMessage:@retroactive Identifiable {}

extension View {
	func handleNotifications(_ state: PresentationNotificationState) -> some View {
        modifier(NotificationHandler(notificationState: state.impl()))
    }
}
