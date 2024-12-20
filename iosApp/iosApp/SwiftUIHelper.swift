import ComposeApp
import SwiftUI

class TheFactory: SwiftUIFactory {
    let controller = MapViewWrapperViewController()
    
    func updateCurrentLatLng(currentLatLng: MapLatLng) { controller.updateCurrentLatLng(currentLatLng) }
    func updateSelectedLatLng(selectedLatLng: MapLatLng) { controller.updateSelectedLatLng(selectedLatLng) }
    func updateMarkers(markers: [KotlinTriple<KotlinInt, KotlinDouble, KotlinDouble>]) { controller.updateMarkers(markers) }
    func updateRoutes(routes: [MapLatLng]) { controller.updateRoutes(routes) }
    func updateCandidates(candidates: [Candidate]) { controller.updateCandidates(candidates) }
    func updateSelectedIndex(selectedIndex: Int32) { controller.updateSelectedIndex(selectedIndex) }
    
    func makeController(
        onSelectLatLng: @escaping (MapLatLng) -> Void,
        onMapClick: @escaping (MapPointF, MapLatLng) -> Void
    ) -> UIViewController {
        controller.mapEnv.onSelectLatLng = onSelectLatLng
        controller.mapEnv.onMapClick = onMapClick
        
        return controller
    }
}
