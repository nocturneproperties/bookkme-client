import SwiftUI
import shared

struct QuantityStepper: View {
	let quantity: Int32
	let onIncrease: () -> Void
	let onDecrease: () -> Void

	var body: some View {
		HStack(spacing: 0) {
			CircleSymbolButton(
				symbol: .minus,
				accessibilityLabel: DesignSystem.strings().quantity_decrease.desc().localized(),
				action: onDecrease
			)
			.accessibilityValue("\(quantity)")
			Text("\(quantity)")
				.font(.headline)
				.monospacedDigit()
				.foregroundStyle(AppColors.primary)
				.frame(minWidth: 16)
				.accessibilityHidden(true)
			CircleSymbolButton(
				symbol: .plus,
				accessibilityLabel: DesignSystem.strings().quantity_increase.desc().localized(),
				action: onIncrease
			)
			.accessibilityValue("\(quantity)")
		}
	}
}

#Preview {
	QuantityStepper(quantity: 2, onIncrease: {}, onDecrease: {})
		.padding()
		.background(AppColors.background)
}
