import SwiftUI
import shared

struct ContentView: View {

    @StateObject var bootstrapVM = AuthDiKt.bootstrapVM()
    @State var initialDestination: BootstrapNavigationDestination? = nil
	
	var body: some View {
        initialViewFrom(destination: initialDestination)
            .handleNavigation(state: bootstrapVM.state.navigation) { destination in
                switch destination {
                case is BootstrapNavigationDestination:
                    initialDestination = destination as? BootstrapNavigationDestination
                    break
                default: break
                }
            }
	}
    
    @ViewBuilder
    func initialViewFrom(destination: BootstrapNavigationDestination?) -> some View {
        switch destination {
        case is BootstrapNavigationDestination.ToMain:
            MainStack()
        case is BootstrapNavigationDestination.ToLogin:
            AuthorizationStack()
        default:
            Spacer()
        }
    }
}
