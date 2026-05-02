import SwiftUI
import shared

struct BootstrapView: View {

	@StateViewModel var bootstrapVM = IOSAuthDiKt.bootstrapVM()
    @State var initialDestination: BootstrapNavigationDestination? = nil
	
	var body: some View {
		ColorSchemeView(state: bootstrapVM.state) {
			StartDestinationView(state: bootstrapVM.state.impl())
		}
	}
}

struct StartDestinationView: View {
	
	let state: IOSBootstrapState
	
	var body: some View {
		ZStack {
			switch state.startDestination {
			case is BootstrapNavigationDestination.Main:
				DashboardScreen()
			case is BootstrapNavigationDestination.Login:
				AuthorizationRoot()
			default:
				Spacer()
			}
		}.animation(.default, value: state.startDestination)
	}
}

struct ColorSchemeView<Content: View>: View {
	
	let state: IOSBootstrapState
	
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
