//
//  ErrorState.swift
//  iosApp
//
//  Created by Antiufieiev Michael on 23.01.2025.
//  Copyright © 2025 BookkMe. All rights reserved.
//

import shared
import Combine

class IOSNotificationState: PresentationNotificationState, ObservableObject {
    
    @Published
    var presentationNotification: [any PresentationNotification] = []
    
    var publisher: AnyPublisher<any PresentationNotification, Never> {
        $presentationNotification.flatMap(\.publisher)
            .eraseToAnyPublisher()
    }
    
    func add(notification: any PresentationNotification) {
		self.presentationNotification.append(notification)
    }
    
	//DispatchQueue.main.async is used to allow changes to published value from within the onReceive. It moves execution to next ui loop pass.
	//Looks like value inside published property does not updated until all observers have been notified.
    func removeFirst() {
		DispatchQueue.main.async {
			self.presentationNotification.removeFirst()
		}
    }
    
}

extension PresentationNotificationState {
    func impl() -> IOSNotificationState {
		return self as! IOSNotificationState
    }
}
