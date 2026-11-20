import SwiftUI
import shared

@main
struct iOSApp: App {
	
	@UIApplicationDelegateAdaptor(FirebaseDelegate.self) var delegate
    
    init() {
        DISetupKt.doInitDI(creator: IOSStateFactoryCreator())
		if CommandLine.arguments.contains("--disable-animations") {
			UIView.setAnimationsEnabled(false)
		}
    }
    
	var body: some Scene {
		WindowGroup {
            BootstrapView()
		}
	}
}
