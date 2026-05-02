//
//  DSPickerField.swift
//  iosApp
//
//  Created by Dmytro Akulinin on 06.02.2026.
//  Copyright © 2026 ValthSolutions. All rights reserved.
//

import SwiftUI
import UIKit
import shared

private enum PickerBottomSheetDetent: CustomPresentationDetent {
    nonisolated(unsafe) static var requestedHeight: CGFloat = .zero

    static func height(in context: Context) -> CGFloat? {
        min(requestedHeight, context.maxDetentValue)
    }
}

struct PickerField: View {
    @Bindable private var state: IOSPickerState
    private let onItemPicked: (PickerPresentation) -> Void

    @State private var isSheetPresented = false
    @State private var isScreenPresented = false

    init(state: PickerFieldState, onItemPicked: ((PickerPresentation) -> Void)? = nil) {
        let impl = state.impl()
        self.state = impl
        self.onItemPicked = onItemPicked ?? { option in
            impl.onItemPicked(option)
        }
    }

    private var pickerDetent: PresentationDetent {
        PickerBottomSheetDetent.requestedHeight = PickerBottomSheet.calculateHeight(items: state.options.count)
        return .custom(PickerBottomSheetDetent.self)
    }

    var body: some View {
        if state.isVisible {
            StateTextField(state: state.textField)
                .contentShape(Rectangle())
                .simultaneousGesture(TapGesture().onEnded {
                    guard state.textField.enabled else { return }
                    dismissKeyboard()
                    switch state.pickerType {
                    case .bottomSheet:
                        guard !state.options.isEmpty else { return }
                        isSheetPresented = true
                    case .screen:
                        isScreenPresented = true
                    default:
                        isScreenPresented = true
                    }
                })
                .sheet(isPresented: $isSheetPresented) {
                    PickerBottomSheet(
                        title: state.pickerTitle.localized(),
                        options: state.options,
                        selectedId: state.selectedItem?.pickerItemId,
                        onPick: { option in
                            onItemPicked(option)
                            isSheetPresented = false
                        }
                    )
                    .presentationDetents([pickerDetent])
                    .presentationDragIndicator(.hidden)
                    .presentationCornerRadius(24)
                    .presentationBackground(AppColors.background)
                }
        }
    }

    private func dismissKeyboard() {
        UIApplication.shared.sendAction(
            #selector(UIResponder.resignFirstResponder),
            to: nil,
            from: nil,
            for: nil
        )
    }
}

private struct PickerBottomSheet: View {
    let title: String
    let options: [PickerPresentation]
    let onPick: (PickerPresentation) -> Void

    @State private var selectedOptionId: String?
    @Environment(\.dismiss) private var dismiss

    private var listHeight: CGFloat {
        Self.listHeight(for: options.count)
    }

    private enum Layout {
        static let rowHeight: CGFloat = 48
        static let buttonHeight: CGFloat = 48
        static let headerHeight: CGFloat = 24

        static let topPadding: CGFloat = 16
        static let horizontalPadding: CGFloat = 16
        static let rowTrailingPadding: CGFloat = 16
        static let headerBottomPadding: CGFloat = 8
        static let buttonTopPadding: CGFloat = 24
        static let bottomPadding: CGFloat = 0

        static let baseHeight: CGFloat =
            topPadding + headerHeight + headerBottomPadding + buttonTopPadding + buttonHeight + bottomPadding
    }

    init(
        title: String,
        options: [PickerPresentation],
        selectedId: String?,
        onPick: @escaping (PickerPresentation) -> Void
    ) {
        self.title = title
        self.options = options
        self.onPick = onPick
        self._selectedOptionId = State(initialValue: selectedId)
    }

    var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            header
            ScrollView {
                LazyVStack(spacing: 8) {
                    ForEach(options, id: \.pickerItemId) { option in
                        Button(action: {
                            selectedOptionId = option.pickerItemId
                        }) {
                            HStack {
                                Text(capitalizedItemTitle(option.displayName.localized()))
									.font(.body)
                                    .multilineTextAlignment(.leading)
                                Spacer(minLength: 0)
								if (selectedOptionId == option.pickerItemId) {
									Image(systemName: "checkmark")
										.foregroundStyle(AppColors.actionText)
								}
                            }
                            .padding(.leading, Layout.horizontalPadding)
                            .padding(.trailing, Layout.rowTrailingPadding)
							.frame(maxWidth: .infinity, minHeight: Layout.rowHeight, maxHeight: .infinity, alignment: .leading)
							.background(AppColors.elevated,
										in: RoundedRectangle(cornerRadius: 12))
							.padding(.horizontal)
                        }
						.buttonStyle(.plain)
                    }
                }
            }
            .scrollIndicators(.hidden)
            .scrollBounceBehavior(.basedOnSize)
            .frame(maxHeight: listHeight, alignment: .top)
            .clipped()
            StateButton(
                state: IOSButtonState(
                    text: DesignSystem.strings.shared.action_continue.desc(),
                    isEnabled: selectedOptionId != nil
                ),
            ) {
                confirmSelection()
            }
            .padding(.top, Layout.buttonTopPadding)
            .padding(.horizontal, Layout.horizontalPadding)
            .padding(.bottom, Layout.bottomPadding)
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .top)
        .background(AppColors.background.ignoresSafeArea())
    }

    private var header: some View {
        HStack(spacing: 12) {
            Text(title)
				.font(.body)
                .foregroundStyle(AppColors.primary)
                .lineLimit(1)

            Spacer(minLength: 0)
        }
        .frame(minHeight: Layout.headerHeight)
        .padding(.top, Layout.topPadding)
        .padding(.horizontal, Layout.horizontalPadding)
        .padding(.bottom, Layout.headerBottomPadding)
    }

    static func calculateHeight(items: Int) -> CGFloat {
        Layout.baseHeight + listHeight(for: items)
    }

    private static func listHeight(for items: Int) -> CGFloat {
        CGFloat(max(items, 1)) * Layout.rowHeight
    }

    private func capitalizedItemTitle(_ title: String) -> String {
        let trimmedTitle = title.trimmingCharacters(in: .whitespacesAndNewlines)
        guard let firstCharacter = trimmedTitle.first else { return trimmedTitle }
        return String(firstCharacter).uppercased() + trimmedTitle.dropFirst()
    }

    private func confirmSelection() {
        guard
            let selectedOptionId,
            let option = options.first(where: { $0.pickerItemId == selectedOptionId })
        else {
            return
        }
        onPick(option)
        dismiss()
    }
}

#Preview("Picker Field - Bottom Sheet") {
    @Previewable
    @State
    var value: IOSPickerState = IOSPickerState(
		pickerTitle: RawStringDesc(string: "Select country"),
        options: [
            MinimalPickerPresentation(pickerItemId: "1", displayName: RawStringDesc(string: "Afghanistan")),
            MinimalPickerPresentation(pickerItemId: "2", displayName: RawStringDesc(string: "Albania")),
            MinimalPickerPresentation(pickerItemId: "3", displayName: RawStringDesc(string: "Algeria")),
            MinimalPickerPresentation(pickerItemId: "4", displayName: RawStringDesc(string: "Algeria")),
            MinimalPickerPresentation(pickerItemId: "5", displayName: RawStringDesc(string: "Algeria")),
            MinimalPickerPresentation(pickerItemId: "6", displayName: RawStringDesc(string: "Algeria")),
            MinimalPickerPresentation(pickerItemId: "7", displayName: RawStringDesc(string: "Algeria")),
            MinimalPickerPresentation(pickerItemId: "8", displayName: RawStringDesc(string: "Algeria")),
            MinimalPickerPresentation(pickerItemId: "9", displayName: RawStringDesc(string: "Algeria")),
            MinimalPickerPresentation(pickerItemId: "10", displayName: RawStringDesc(string: "Algeria")),
            MinimalPickerPresentation(pickerItemId: "11", displayName: RawStringDesc(string: "Algeria")),
            MinimalPickerPresentation(pickerItemId: "12", displayName: RawStringDesc(string: "Algeria")),
            MinimalPickerPresentation(pickerItemId: "13", displayName: RawStringDesc(string: "Algeria")),
            MinimalPickerPresentation(pickerItemId: "14", displayName: RawStringDesc(string: "Algeria")),
        ],
		pickerType: .bottomSheet,
        textField: IOSTextFieldState(placeholder: RawStringDesc(string: "Select country"))
    )

	VStack(spacing: 16) {
        PickerField(state: value) { option in
            value.selectedItem = option
            value.textField.updateText(desc: option.displayName)
        }
    }
    .padding(16)
}

#Preview("Picker Field - Screen") {
    @Previewable
    @State
    var value: IOSPickerState = IOSPickerState(
		pickerTitle: RawStringDesc(string: "Select country"),
        options: [
            MinimalPickerPresentation(pickerItemId: "1", displayName: RawStringDesc(string: "Afghanistan")),
            MinimalPickerPresentation(pickerItemId: "2", displayName: RawStringDesc(string: "Albania")),
            MinimalPickerPresentation(pickerItemId: "5", displayName: RawStringDesc(string: "Albania")),
            MinimalPickerPresentation(pickerItemId: "4", displayName: RawStringDesc(string: "Albania")),
            MinimalPickerPresentation(pickerItemId: "6", displayName: RawStringDesc(string: "Albania")),
            MinimalPickerPresentation(pickerItemId: "7", displayName: RawStringDesc(string: "Albania")),
            MinimalPickerPresentation(pickerItemId: "8", displayName: RawStringDesc(string: "Albania")),
            MinimalPickerPresentation(pickerItemId: "9", displayName: RawStringDesc(string: "Albania")),
            MinimalPickerPresentation(pickerItemId: "3", displayName: RawStringDesc(string: "Algeria"))
        ],
		pickerType: .screen,
        textField: IOSTextFieldState(placeholder: RawStringDesc(string: "Select country"))
    )

	VStack(spacing: 16) {
        PickerField(state: value) { option in
            value.selectedItem = option
            value.textField.updateText(desc: option.displayName)
        }
    }
    .padding(16)
    .background(AppColors.elevated)
}
