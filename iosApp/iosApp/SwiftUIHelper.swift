import ComposeApp
import SwiftUI

class TheFactory: SwiftUIFactory {
    var controller: MapViewWrapperViewController? = nil
    
//    func makeController(
//        selectedLatLng: MapLatLng,
//        onMapClick: @escaping (MapPointF, MapLatLng) -> Void
//    ) -> UIViewController {
//        controller = MapViewWrapperViewController(selectedLatLng: selectedLatLng, onMapClick: onMapClick)
//        return controller!
//    }
    
    func makeController(
        currentLatLng: MapLatLng,
        selectedLatLng: MapLatLng,
        onSelectLatLng: @escaping (MapLatLng) -> Void,
        markers: [KotlinTriple<KotlinInt, KotlinDouble, KotlinDouble>],
        routes: [MapLatLng],
        candidates: [Candidate],
        selectedIndex:Int32,
        onMapClick: @escaping (MapPointF, MapLatLng) -> Void
    ) -> UIViewController {
        controller = MapViewWrapperViewController(selectedLatLng: selectedLatLng, onMapClick: { _, _ in })
        return controller!
    }
}
