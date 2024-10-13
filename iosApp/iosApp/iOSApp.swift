import SwiftUI
import ComposeApp

@main
struct iOSApp: App {
//    @UIApplicationDelegateAdaptor(AppDelegate.self) var delegate
    init() {
        SwiftUIFactoryCompanion().shared = TheFactory()
        KoinInitializer_iosKt.doInitKoinIos(
            appComponent: IosApplicationComponent(
//                mapScreen: MapViewWrapperViewController()   
//                networkHelper: IosNetworkHelper(),
//                appTerminator: IosAppTerminator()
            )
        )
    }
    
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
