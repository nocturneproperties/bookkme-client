import SwiftUI
import shared

struct AppointmentCreateScreen: View {

    @EnvironmentObject var navigationStack: NavigationStackHolder
    @StateViewModel var viewModel: AppointmentCreateViewModel

    init(businessId: KotlinUuid) {
        _viewModel = StateViewModel(
            wrappedValue: IosAppointmentsPresentationDiKt.appointmentCreateVM(businessId: businessId)
        )
    }

    var body: some View {
        let state = IOSAppointmentCreateState.cast(viewModel.uiState)
        List {
			Section {
				PickerField(state.clientPicker)
					.textFieldStyle(.inListTrailing)
			} header: {
				Text("")
			}
			Section {
				OptionsMultiPickerField(state.servicePicker) { item, onRemove in
					ServiceItem(
						service: item as! ServicePickerPresentation,
						onRemove: onRemove
					)
				}
				.listRowInsets(EdgeInsets())
			} header: {
				Text(state.servicePicker.pickerTitle.localized())
			} footer: {
				if !state.subtotalPrice.isEmpty {
					SubtotalRow(label: state.subtotalLabel, price: state.subtotalPrice)
						.listRowInsets(EdgeInsets())
						.listRowBackground(Color.clear)
				}
			}
			Section {
				DatePickerField(state: state.datePicker)
					.textFieldStyle(.inList)
				TimePickerField(state: state.timePicker)
					.textFieldStyle(.inList)
			}
			Section {
				StateTextField(state.note, textEditor: true)
					.lineLimit(3...5)
					.textFieldStyle(.inList)
			}
        }
		.toolbar {
			TextButton(state.create)
		}
		.listSectionSpacing(.compact)
        .withNavigationBar(state.appBar)
        .sendLifecycleEventsTo(viewModel)
        .handleNotifications(state.notifications)
        .handleNavigation(state.navigation) { destination in
            switch destination {
            case is AppointmentCreateDestination.Back:
                navigationStack.popLast()
            default:
                break
            }
        }
    }
}

private struct SubtotalRow: View {
    let label: any StringDesc
    let price: String

    var body: some View {
        HStack {
            Text(label.localized())
                .font(.subheadline)
                .foregroundStyle(AppColors.secondary)
            Spacer()
            Text(price)
                .font(.headline)
                .foregroundStyle(AppColors.primary)
        }
        .padding(.horizontal, 16)
		.padding(.top, 8)
    }
}

private struct ServiceItem: View {
    let service: ServicePickerPresentation
    let onRemove: () -> Void

    var body: some View {
		HStack(spacing: 0) {
			VStack(alignment: .leading, spacing: 2) {
				Text(service.displayName.localized())
					.font(.headline)
					.foregroundStyle(AppColors.primary)
				Text(service.duration.localized())
					.font(.caption)
					.foregroundStyle(AppColors.secondary)
			}
			.padding(.leading, 16)
			Spacer(minLength: 8)
			Text(service.price)
				.font(.headline)
				.foregroundStyle(AppColors.primary)
			Button(action: onRemove) {
				ZStack {
					Circle()
						.fill(AppColors.primary.opacity(0.1))
						.frame(width: 28, height: 28)
					Text("−")
						.font(.body)
						.fontWeight(.bold)
						.foregroundStyle(AppColors.secondary)
				}
			}
			.buttonStyle(.plain)
			.padding(.leading, 8)
			.padding(.trailing, 16)
		}
		.padding(.vertical, 16)
    }
}
