import SwiftUI

enum CircleSymbol {
	case minus
	case plus

	var systemImage: String {
		switch self {
		case .minus: "minus"
		case .plus: "plus"
		}
	}
}

struct CircleSymbolButton: View {
	let symbol: CircleSymbol
	var accessibilityLabel: String? = nil
	let action: () -> Void

	var body: some View {
		labelled(
			Button(action: action) {
				ZStack {
					Circle()
						.fill(AppColors.primary.opacity(0.1))
						.frame(width: 28, height: 28)
					Image(systemName: symbol.systemImage)
						.font(.body.bold())
						.foregroundStyle(AppColors.secondary)
				}
				.frame(width: 44, height: 44)
				.contentShape(Rectangle())
			}
			.buttonStyle(.plain)
		)
	}

	@ViewBuilder
	private func labelled(_ button: some View) -> some View {
		if let accessibilityLabel {
			button.accessibilityLabel(accessibilityLabel)
		} else {
			button
		}
	}
}

#Preview {
	HStack {
		CircleSymbolButton(symbol: .minus) {}
		CircleSymbolButton(symbol: .plus) {}
	}
	.padding()
	.background(AppColors.background)
}
