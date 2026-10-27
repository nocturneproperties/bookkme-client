import SwiftUI
import shared

struct AppointmentCompletionSheet: View {

	let state: IOSAppointmentCompletionState

	init(_ state: AppointmentCompletionState) {
		self.state = IOSAppointmentCompletionState.cast(state)
	}

	var body: some View {
		NavigationStack {
			List {
				Section {
					OptionsMultiPickerField(state.servicePicker) { item, onRemove in
						AppointmentServiceItem(
							service: item as! ServicePickerPresentation,
							onRemove: onRemove
						)
					}
					.listRowInsets(EdgeInsets())
				} header: {
					Text(state.servicePicker.pickerTitle.localized())
				} footer: {
					Text(state.servicesHint.localized())
				}
				Section {
					StateTextField(state.price)
						.textFieldStyle(.inListTrailing)
				} footer: {
					FieldError(field: state.price)
				}
				Section {
					StateTextField(state.note, textEditor: true)
						.lineLimit(3...5)
						.textFieldStyle(.inList)
				} footer: {
					FieldError(field: state.note)
				}
				Section {
					StateButton(state.confirmButton)
						.listRowInsets(EdgeInsets())
						.listRowBackground(Color.clear)
				}
			}
			.listSectionSpacing(.compact)
			.scrollDismissesKeyboard(.immediately)
			.navigationTitle(state.title.localized())
			.navigationBarTitleDisplayMode(.inline)
		}
		.presentationDragIndicator(.visible)
	}
}

private struct FieldError: View {
	let field: any TextFieldState

	var body: some View {
		if let error = field.supportingTextRes {
			Text(error.localized())
				.foregroundStyle(AppColors.error)
		}
	}
}
