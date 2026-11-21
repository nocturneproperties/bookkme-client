import SwiftUI
import shared

struct AppointmentSubtotalRow: View {
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

struct AppointmentServiceItem: View {
    let service: ServicePickerPresentation
    var onRemove: (() -> Void)? = nil

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
				.padding(.trailing, onRemove == nil ? 16 : 0)
			if let onRemove {
				CircleSymbolButton(symbol: .minus, action: onRemove)
					.padding(.trailing, 8)
			}
		}
		.padding(.vertical, 16)
    }
}
