import SwiftUI
import UIKit
import NMapsMap
import ComposeApp

class MapEnvironment: ObservableObject {
    @Published var currentLatLng: MapLatLng = MapLatLng(latitude: Pref.Default.shared.CURRENT_LATITUDE, longitude: Pref.Default.shared.CURRENT_LONGITUDE)
    @Published var selectedLatLng: MapLatLng = MapLatLng(latitude: Pref.Default.shared.CURRENT_LATITUDE, longitude: Pref.Default.shared.CURRENT_LONGITUDE)
    @Published var onSelectLatLng: ((MapLatLng) -> Void) = { _ in }
    @Published var markers: [KotlinTriple<KotlinInt, KotlinDouble, KotlinDouble>] = []
    @Published var routes: [MapLatLng] = []
    @Published var candidates: [Candidate] = []
    @Published var selectedIndex: Int32 = -1
    @Published var onMapClick: (MapPointF, MapLatLng) -> Void = { _, _ in }
}

struct MapUIView: UIViewRepresentable {
    @EnvironmentObject private var mapEnv: MapEnvironment
    
    func makeUIView(context: Context) -> NMFNaverMapView {
        // View를 원하는대로 생성하는 곳
        let map = NMFNaverMapView(frame: UIScreen.main.bounds)
        map.showZoomControls = true
        map.mapView.positionMode = .direction
        map.mapView.zoomLevel = 17
        map.mapView.touchDelegate = context.coordinator
        map.mapView.addOptionDelegate(delegate: context.coordinator)

        
        return map
    }
  
    func updateUIView(_ view: NMFNaverMapView, context: Context) {
        // View를 원하는대로 수정하는 곳
        
        let marker = NMFMarker()
        marker.position = NMGLatLng(lat: mapEnv.selectedLatLng.latitude, lng: mapEnv.selectedLatLng.longitude)
        marker.mapView = view.mapView

    }
    
    class Coordinator: NSObject, NMFMapViewTouchDelegate, NMFMapViewOptionDelegate {
        var parent: MapUIView
                
        init(parent: MapUIView) {
            self.parent = parent
        }
        
        func mapView(_ mapView: NMFMapView, didTapMap latlng: NMGLatLng, point: CGPoint) {
            parent.mapEnv.onMapClick(MapPointF(x: Float(point.x), y: Float(point.y)), MapLatLng(latitude: latlng.lat, longitude: latlng.lng))
        }
    }
    
    func makeCoordinator() -> Coordinator {
        return Coordinator(parent: self)
    }
}

struct MapSwiftView: View {
//    var selectedLatLng: any RuntimeMutableState
//    var onMapClick: (MapPointF, MapLatLng) -> Void
    
    @EnvironmentObject private var mapEnv: MapEnvironment
    
    var body: some View {
        VStack {
            Text("\(mapEnv.selectedLatLng)")
            MapUIView().environmentObject(mapEnv) // 바인딩을 사용하여 클릭 핸들러를 전달
        }
    }
}

class MapViewWrapperViewController: UIHostingController<AnyView> {
    let mapEnv = MapEnvironment()
    
    init(selectedLatLng: MapLatLng, onMapClick: @escaping (MapPointF, MapLatLng) -> Void) {
        super.init(rootView: AnyView(MapSwiftView().environmentObject(mapEnv))) // 초기화
    }
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

//    required dynamic init?(coder aDecoder: NSCoder) {
//        super.init(coder: aDecoder, rootView: MapSwiftView(selectedLatLng: MapLatLng(latitude: 0.0, longitude: 0.0), onMapClick: { _, _ in })) // 초기화
//    }
//    
//    @MainActor @preconcurrency required dynamic init?(coder aDecoder: NSCoder, selectedLatLng: MapLatLng, onMapClick: @escaping (MapPointF, MapLatLng) -> Void) {
//        super.init(rootView: MapSwiftView(selectedLatLng: selectedLatLng, onMapClick: onMapClick)) // 초기화
//    }
    
//    func updateSelectedLatLng(_ selectedLatLng: MapLatLng) {
//        mapEnv.selectedLatLng = selectedLatLng
//    }
}
