//
//  StateTextField.swift
//  iosApp
//
//  Created by Antiufieiev Michael on 26.12.2024.
//  Copyright © 2024 BookkMe. All rights reserved.
//

import SwiftUI
import shared

struct StateTextField: View {
    
    @Bindable
    var state: IOSTextFieldState
	let isEditor: Bool
    let onTextChanged: ((String) -> Void)? //TODO: Backward compatibility, remove when deprecated screens will be refactored
	
	private var keyboardType: UIKeyboardType {
		switch state.inputType {
		case .digit:
			return .numberPad
		case .decimal:
			return .decimalPad
		case .ascii:
			return .asciiCapable
		case .email:
			return .emailAddress
		case .phone:
			return .phonePad
		case .password:
			return .asciiCapable
		default:
			return .default
		}
	}
	
	private var contentType: UITextContentType? {
		switch state.inputType {
		case .email:
			return .emailAddress
		case .phone:
			return .telephoneNumber
		case .password:
			return .password
		default:
			return nil
		}
	}
	
	private var textBinding: Binding<String> {
		Binding<String>(
			get: { state.text },
			set: { text in
				let newValue = String(text.prefix(Int(state.maxLength)))
				if newValue != state.text {
					withAnimation {
						state.onTextChanged?(newValue)
						onTextChanged?(newValue)
					}
				}
			}
		)
	}

	private var contentColor: Color {
		(state.enabled && !state.readOnly) ? AppColors.primary : AppColors.secondary
	}

	init(
		_ state: TextFieldState,
		textEditor: Bool = false,
		onTextChanged: ((String) -> Void)? = nil
	) {
		self._state = Bindable(wrappedValue: IOSTextFieldState.cast(state))
		self.isEditor = textEditor
		self.onTextChanged = onTextChanged
	}

	var body: some View {
		HStack(spacing: 8) {
			if let icon = state.startIcon?.toUIImage() {
				Image(uiImage: icon)
					.frame(width: 24, height: 24)
			}

			LabeledContent {
				TextField(
					state.placeholder.localized(),
					text: textBinding,
					axis: isEditor ? .vertical : .horizontal
				)
				.keyboardType(keyboardType)
				.textContentType(contentType)
				.font(.body)
				.foregroundStyle(contentColor)
			} label: {
				let label = state.label.localized()
				if !label.isEmpty {
					Text(label)
						.frame(minWidth: 100, alignment: .leading)
						.lineLimit(1)
						.foregroundStyle(contentColor)
				}
			}

			if let suffix = state.suffix {
				Text(suffix.localized())
					.font(.footnote)
					.foregroundStyle(AppColors.secondary)
			}
		}
		.contentShape(Rectangle())
		.disabled(!state.enabled || state.readOnly)
		.id(state.id)
	}
}

struct SectionTextField: View {
	
	@Bindable
	var state: IOSTextFieldState
	let header: String?
	let isEditor: Bool
	let onTextChanged: ((String) -> Void)? //TODO: Backward compatibility, remove when deprecated screens will be refactored
	
	private var keyboardType: UIKeyboardType {
		switch state.inputType {
		case .digit:
			return .numberPad
		case .decimal:
			return .decimalPad
		case .ascii:
			return .asciiCapable
		case .email:
			return .emailAddress
		case .phone:
			return .phonePad
		case .password:
			return .asciiCapable
		default:
			return .default
		}
	}
	
	private var contentType: UITextContentType? {
		switch state.inputType {
		case .email:
			return .emailAddress
		case .phone:
			return .telephoneNumber
		case .password:
			return .password
		default:
			return nil
		}
	}
	
	private var textBinding: Binding<String> {
		Binding<String>(
			get: { state.text },
			set: { text in
				let newValue = String(text.prefix(Int(state.maxLength)))
				if newValue != state.text {
					withAnimation {
						state.onTextChanged?(newValue)
						onTextChanged?(newValue)
					}
				}
			}
		)
	}

	private var contentColor: Color {
		(state.enabled && !state.readOnly) ? AppColors.primary : AppColors.secondary
	}

	init(
		_ state: TextFieldState,
		header: String? = nil,
		textEditor: Bool = false,
		onTextChanged: ((String) -> Void)? = nil
	) {
		self._state = Bindable(wrappedValue: IOSTextFieldState.cast(state))
		self.isEditor = textEditor
		self.onTextChanged = onTextChanged
		self.header = header
	}

	var body: some View {
		Section {
			HStack(spacing: 8) {
				if let icon = state.startIcon?.toUIImage() {
					Image(uiImage: icon)
						.frame(width: 24, height: 24)
				}

				LabeledContent {
					TextField(
						state.placeholder.localized(),
						text: textBinding,
						axis: isEditor ? .vertical : .horizontal
					)
					.keyboardType(keyboardType)
					.textContentType(contentType)
					.font(.body)
					.foregroundStyle(contentColor)
					.disabled(!state.enabled || state.readOnly)
				} label: {
					let label = state.label.localized()
					if !label.isEmpty {
						Text(label)
							.frame(minWidth: 100, alignment: .leading)
							.lineLimit(1)
							.foregroundStyle(contentColor)
					}
				}
				
				if let suffix = state.suffix {
					Text(suffix.localized())
						.font(.footnote)
						.foregroundStyle(AppColors.secondary)
				}
			}
			.contentShape(Rectangle())
			.id(state.id)
		} header : {
			if let headerStr = header {
				Text(headerStr)
			}
		} footer : {
			if let supporting = state.supportingTextRes {
				Text(supporting.localized())
					.font(.footnote)
					.foregroundStyle(state.validationState == .error ? AppColors.error : AppColors.secondary)
					.fixedSize(horizontal: false, vertical: true)
					.frame(maxWidth: .infinity, alignment: .leading)
			}
		}
	}
}

struct StandaloneTextFieldStyle: TextFieldStyle {
	func _body(configuration: TextField<Self._Label>) -> some View {
		configuration
			.padding(.horizontal, 8)
			.padding(.vertical, 12)
			.background(AppColors.elevated)
			.cornerRadius(10)
	}
}

struct OnElevatedTextFieldStyle: TextFieldStyle {
	func _body(configuration: TextField<Self._Label>) -> some View {
		configuration
			.padding(.horizontal, 8)
			.padding(.vertical, 12)
			.background(AppColors.background)
			.cornerRadius(10)
	}
}

struct InListTrailingTextFieldStyle: TextFieldStyle {
	func _body(configuration: TextField<Self._Label>) -> some View {
		configuration
			.padding(.horizontal, 0)
			.padding(.vertical, 0)
			.background(Color.clear)
			.multilineTextAlignment(.trailing)
	}
}

struct InListTextFieldStyle: TextFieldStyle {
	func _body(configuration: TextField<Self._Label>) -> some View {
		configuration
			.padding(.horizontal, 0)
			.padding(.vertical, 0)
			.background(Color.clear)
	}
}

extension TextFieldStyle where Self == InListTextFieldStyle {
	static var inList: InListTextFieldStyle { InListTextFieldStyle() }
}

extension TextFieldStyle where Self == InListTrailingTextFieldStyle {
	static var inListTrailing: InListTrailingTextFieldStyle { InListTrailingTextFieldStyle() }
}

extension TextFieldStyle where Self == OnElevatedTextFieldStyle {
	static var onElevated: OnElevatedTextFieldStyle { OnElevatedTextFieldStyle() }
}

extension TextFieldStyle where Self == StandaloneTextFieldStyle {
	static var standalone: StandaloneTextFieldStyle { StandaloneTextFieldStyle() }
}

#Preview {
    
    @Previewable
    @State
	var value: IOSTextFieldState = IOSTextFieldState(enabled: true, supportingTextRes: RawStringDesc(string: "Error"), placeholder: RawStringDesc(string: "Hint"), isValid: true, maxLength: 20, readOnly: false, text: "Text",)
    
	VStack {
		StateTextField(value) { _ in
			
		}.padding()
	}.background(AppColors.background)
}
