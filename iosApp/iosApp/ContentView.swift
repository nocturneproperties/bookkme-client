import SwiftUI
import shared

struct ContentView: View {
    
    @EnvironmentObject var navigationStack: NavigationStackHolder
	
	var body: some View {
        NavigationStack(path: $navigationStack.path) {
            Button("Sign Up") {
                navigationStack.path.append(SignUpDestination())
            }
            .navigationDestination(for: SignUpDestination.self) { value in
                SignUpScreen()
            }
            .navigationDestination(for: SignInDestination.self) { value in
                SignInScreen()
            }
        }
	}
}

struct ContentView_Previews: PreviewProvider {
	static var previews: some View {
		ContentView()
            .environmentObject(NavigationStackHolder())
	}
}
