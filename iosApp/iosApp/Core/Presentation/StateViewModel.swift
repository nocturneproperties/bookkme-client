//
//  AnyObject+Lifetime.swift
//  iosApp
//

import shared
import SwiftUI

@propertyWrapper
class StateViewModel<Value: ViewModel>: DynamicProperty {
    @State private var value: Value

    init(wrappedValue: Value) {
        self._value = State(initialValue: wrappedValue)
    }

    var wrappedValue: Value {
        get { value }
        set { value = newValue }
    }
    
    deinit {
        value.onCleared()
    }
}
