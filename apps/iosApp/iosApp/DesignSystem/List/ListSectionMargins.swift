import SwiftUI

extension View {
	@ViewBuilder
	func zeroHorizontalListSectionMargins() -> some View {
		if #available(iOS 26.0, *) {
			listSectionMargins(.horizontal, 0)
		} else {
			self
		}
	}
}
