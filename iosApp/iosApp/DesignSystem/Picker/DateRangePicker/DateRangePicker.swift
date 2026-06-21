import SwiftUI
import shared

struct DateRangePicker: View {
    @Bindable private var state: IOSDateRangePickerState
    let onDismiss: () -> Void

    init(state: any DateRangePickerState, onDismiss: @escaping () -> Void) {
        self._state = Bindable(wrappedValue: IOSDateRangePickerState.cast(state))
        self.onDismiss = onDismiss
    }

    var body: some View {
        VStack(spacing: 16) {
            Text(state.title.localized())
				.padding(.top)
                .font(.headline)
                .frame(maxWidth: .infinity, alignment: .leading)

			Spacer()
            HStack(spacing: 8) {
                DatePickerField(state: state.startDate)
                DatePickerField(state: state.endDate)
            }
			Spacer()
			Spacer()

            Button(action: {
                state.onDateRangeSelected()
                onDismiss()
            }) {
                Text(DesignSystem.strings.shared.action_select.desc().localized())
                    .frame(maxWidth: .infinity, minHeight: 36)
            }
            .buttonStyle(.borderedProminent)
            .tint(AppColors.buttonActive)
        }
        .padding(16)
        .presentationBackground(AppColors.background)
        .presentationDetents([.medium])
    }
}
