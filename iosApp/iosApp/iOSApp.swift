import SwiftUI
import Shared

@main
struct iOSApp: App {

    init(){
        KoinInitializerKt.doInitKoin(
            driverFactory: DatabaseDriverFactory()
        )
    }
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}