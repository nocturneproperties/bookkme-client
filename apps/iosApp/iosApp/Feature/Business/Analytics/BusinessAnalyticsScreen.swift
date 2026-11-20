import SwiftUI
import shared

struct BusinessAnalyticsScreen: View {

	@EnvironmentObject var navigationStack: NavigationStackHolder
	@StateViewModel var viewModel: BusinessAnalyticsViewModel

	init() {
		self._viewModel = StateViewModel(wrappedValue: IOSBusinessDiKt.businessAnalyticsVM())
	}

	var body: some View {
		BusinessAnalyticsContent(state: viewModel.uiState)
			.withNavigationBar(viewModel.uiState.appBar)
			.sendLifecycleEventsTo(viewModel)
			.handleNotifications(viewModel.uiState.notifications)
			.handleNavigation(viewModel.uiState.navigation) { dest in
				switch dest {
				case is BusinessAnalyticsDestinations.Back:
					navigationStack.popLast()
				default:
					break
				}
			}
	}
}

struct BusinessAnalyticsContent: View {

	let state: IOSBusinessAnalyticsState

	init(state: BusinessAnalyticsState) {
		self.state = IOSBusinessAnalyticsState.cast(state)
	}

	var body: some View {
		ContentUnavailableView {
			Label(state.title.localized(), systemImage: "chart.bar.xaxis")
		} description: {
			Text(state.message.localized())
		}
	}
}

#Preview {
	NavigationStack {
		BusinessAnalyticsScreen()
	}
}
