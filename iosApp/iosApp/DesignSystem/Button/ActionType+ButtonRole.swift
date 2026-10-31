import SwiftUI
import shared

extension ActionType {
	var buttonRole: ButtonRole? {
		switch self {
		case .confirm:
			guard #available(iOS 26.0, *) else { return nil }
			return .confirm
		case .negative:
			return .destructive
		case .cancel:
			return .cancel
		default:
			return nil
		}
	}
}
