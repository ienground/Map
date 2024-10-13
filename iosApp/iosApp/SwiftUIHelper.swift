import ComposeApp
import SwiftUI

class TheFactory: SwiftUIFactory {
    func makeController() -> UIViewController {
        MapViewWrapperViewController()
    }
}
