import SwiftUI
import shared

struct BootstrapView: View {

	@StateObject var bootstrapVM = IOSAuthDiKt.bootstrapVM()
    @State var initialDestination: BootstrapNavigationDestination? = nil
	
	var body: some View {
		ColorSchemeView(state: bootstrapVM.state) {
			StartDestinationView(state: bootstrapVM.state.impl())
		}
	}
}

struct StartDestinationView: View {
	
	@ObservedObject
	var state: IOSBootstrapState
	
	var body: some View {
		switch state.startDestination {
		case is BootstrapNavigationDestination.Main:
			DashboardScreen()
		case is BootstrapNavigationDestination.Login:
			AuthorizationRoot()
		default:
			Spacer()
		}
	}
}

struct ColorSchemeView<Content: View>: View {
	
	@ObservedObject
	var state: IOSBootstrapState
	
	@ViewBuilder
	let content: Content
	
	@Environment(\.colorScheme)
	var colorScheme: ColorScheme
	
	@State
	var animationScheme: ColorScheme?
	
	init(state: BootstrapState, @ViewBuilder content: () -> Content) {
		self.state = state.impl()
		self.content = content()
		self.animationScheme = mapScheme(scheme: state.colorScheme)
	}
	
	var body: some View {
		VStack {
			content
				.preferredColorScheme(animationScheme)
				.onChange(of: state.colorScheme, initial: false) { _, newValue in
					withAnimation {
						animationScheme = mapScheme(scheme: newValue)
					}
				}
		}
	}
	
	private func mapScheme(scheme: BootstrapStateUIColorScheme) -> ColorScheme? {
		return switch(scheme) {
		case BootstrapStateUIColorScheme.dark: ColorScheme.dark
		case BootstrapStateUIColorScheme.light: ColorScheme.light
		default: nil
		}
	}
}
