//
//  StateCheckBox.swift
//  iosApp
//
//  Created by BookkMe on 29.05.2026.
//  Copyright © 2026 BookkMe. All rights reserved.
//

import SwiftUI
import shared

struct StateCheckBox: View {
	
	@Bindable var state: IOSBooleanState
	let label: Text?
	let textColor: Color?
	let textSize: CGFloat?
	
	init(_ state: IOSBooleanState, label: Text? = nil, textColor: Color? = nil, textSize: CGFloat? = nil) {
		self.label = label
		self.textColor = textColor
		self.textSize = textSize
		self._state = Bindable(wrappedValue: state)
	}
	
	init(_ state: BooleanState, label: Text? = nil, textColor: Color? = nil, textSize: CGFloat? = nil) {
		self.init(IOSBooleanState.cast(state), label: label, textColor: textColor, textSize: textSize)
	}
	
	var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            Toggle(
                isOn: Binding(
                    get: { state.isChecked },
                    set: { value in
                        withAnimation {
                            state.onCheckedChange?(KotlinBoolean(bool: value))
                        }
                    }
                )
            ) {
                styledCheckBoxLabel(label ?? Text(state.text.localized()), color: textColor, size: textSize)
            }
            .disabled(!state.isEnabled)
            if let supportingText = state.supportingTextRes {
                Text(supportingText.localized())
                    .font(.footnote)
                    .foregroundStyle(state.validationState == .error ? AppColors.error : AppColors.secondary)
            }
        }
    }
}


struct SectionStateCheckBox: View {
	
	@Bindable var state: IOSBooleanState
	let label: Text?
	let textColor: Color?
	let textSize: CGFloat?
	
	init(_ state: IOSBooleanState, label: Text? = nil, textColor: Color? = nil, textSize: CGFloat? = nil) {
		self.label = label
		self.textColor = textColor
		self.textSize = textSize
		self._state = Bindable(wrappedValue: state)
	}
	
	init(_ state: BooleanState, label: Text? = nil, textColor: Color? = nil, textSize: CGFloat? = nil) {
		self.init(IOSBooleanState.cast(state), label: label, textColor: textColor, textSize: textSize)
	}
	
	var body: some View {
		Section {
			VStack(alignment: .leading, spacing: 8) {
				Toggle(
					isOn: Binding(
						get: { state.isChecked },
						set: { value in
							withAnimation {
								state.onCheckedChange?(KotlinBoolean(bool: value))
							}
						}
					)
				) {
					styledCheckBoxLabel(label ?? Text(state.text.localized()), color: textColor, size: textSize)
				}
				.disabled(!state.isEnabled)
			}
		} footer: {
			if let supportingText = state.supportingTextRes {
				Text(supportingText.localized())
					.font(.footnote)
					.foregroundStyle(state.validationState == .error ? AppColors.error : AppColors.secondary)
			}
		}
	}
}

private func styledCheckBoxLabel(_ label: Text, color: Color?, size: CGFloat?) -> Text {
    var result = label
    if let color {
        result = result.foregroundColor(color)
    }
    if let size {
        result = result.font(.system(size: size))
    }
    return result
}

#Preview {
	let state = IOSBooleanState(text: RawStringDesc(string: "Text"))
	StateCheckBox(state)
}
