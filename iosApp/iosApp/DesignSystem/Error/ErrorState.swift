//
//  ErrorState.swift
//  iosApp
//
//  Created by Antiufieiev Michael on 23.01.2025.
//  Copyright © 2025 BookkMe. All rights reserved.
//

import shared
import Combine

class IOSErrorState:ErrorState, ObservableObject {
    
    @Published
    var presentationError: [any PresentationError] = []
    
    var publisher: AnyPublisher<any PresentationError, Never> {
        $presentationError.flatMap(\.publisher)
            .eraseToAnyPublisher()
    }
    
    func add(error: any PresentationError) {
        presentationError.append(error)
    }
    
    func removeFirst() {
        presentationError.removeFirst()
    }
    
}

extension shared.ErrorState {
    func impl() -> IOSErrorState {
        return self as! IOSErrorState
    }
}
