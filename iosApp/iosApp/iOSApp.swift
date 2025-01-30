import SwiftUI
import shared

@main
struct iOSApp: App {
    
    init() {
        DISetupKt.doInitDI(creator: IOSStateFactoryCreator())
    }
    
	var body: some Scene {
		WindowGroup {
            ContentView()
		}
	}
}
