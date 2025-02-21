//
//  ErrorState.swift
//  iosApp
//
//  Created by Antiufieiev Michael on 23.01.2025.
//  Copyright © 2025 BookkMe. All rights reserved.
//

import shared
import Combine

class IOSNotificationState:PresentationNotificationState, ObservableObject {
    
    @Published
    var presentationNotification: [any PresentationNotification] = []
    
    var publisher: AnyPublisher<any PresentationNotification, Never> {
        $presentationNotification.flatMap(\.publisher)
            .eraseToAnyPublisher()
    }
    
    func add(notification: any PresentationNotification) {
        presentationNotification.append(notification)
    }
    
    func removeFirst() {
        presentationNotification.removeFirst()
    }
    
}

extension PresentationNotificationState {
    func impl() -> IOSNotificationState {
		return self as! IOSNotificationState
    }
}
