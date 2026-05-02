//
//  AnyObject+Lifetime.swift
//  iosApp
//

import shared
import SwiftUI

@propertyWrapper
struct StateViewModel<Value: ViewModel>: DynamicProperty {
    @StateObject private var value: DeinitWrapper<Value>

    init(wrappedValue: @autoclosure @escaping () -> Value) {
        self._value = StateObject(wrappedValue: DeinitWrapper(wrappedValue()))
    }

    var wrappedValue: Value {
		get { value.viewModel }
		nonmutating set { value.viewModel = newValue }
    }
}



private class DeinitWrapper<Value: ViewModel>: ObservableObject {
	var viewModel: Value
	
	init(_ value: Value) {
		self.viewModel = value
	}
	
	deinit {
		viewModel.onCleared()
	}
}
