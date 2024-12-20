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
    
    var coordinator: MapUIView.Coordinator?
}

struct MapUIView: UIViewRepresentable {
    @EnvironmentObject private var mapEnv: MapEnvironment
    @State private var selectedMarker: NMFMarker? = nil
    @State private var currentOverlay: NMFPath? = nil
    @State private var waypointMarker: NMFMarker? = nil
    
    func makeUIView(context: Context) -> NMFNaverMapView {
        // View를 원하는대로 생성하는 곳
        let map = NMFNaverMapView(frame: UIScreen.main.bounds)
        map.showZoomControls = true
        map.mapView.positionMode = .direction
        map.mapView.zoomLevel = 17
        map.mapView.touchDelegate = context.coordinator
        map.mapView.addOptionDelegate(delegate: context.coordinator)
        
        context.coordinator.mapView = map.mapView
        mapEnv.coordinator = context.coordinator
        return map
    }
  
    func updateUIView(_ view: NMFNaverMapView, context: Context) {
        // View를 원하는대로 수정하는 곳
        
    }
    
    class Coordinator: NSObject, NMFMapViewTouchDelegate, NMFMapViewOptionDelegate {
        var parent: MapUIView
        var mapView: NMFMapView?
                
        init(parent: MapUIView) {
            self.parent = parent
        }
        
        func mapView(_ mapView: NMFMapView, didTapMap latlng: NMGLatLng, point: CGPoint) {
            parent.mapEnv.onMapClick(MapPointF(x: Float(point.x), y: Float(point.y)), MapLatLng(latitude: latlng.lat, longitude: latlng.lng))
        }
        
        func updateCameraPosition(latLng: MapLatLng) {
            parent.selectedMarker?.mapView = nil
            guard let mapView = self.mapView else {
                print("mapView is nil, cannot perform action.")
                return
            }
            let cameraUpdate = NMFCameraUpdate(scrollTo: NMGLatLng(lat: latLng.latitude, lng: latLng.longitude)) // 예시 좌표
            cameraUpdate.animation = .easeIn
            mapView.moveCamera(cameraUpdate)
        }
        
        func updateBound(selectedIndex: Int, candidates: [Candidate]) {
            parent.currentOverlay?.mapView = nil
            parent.waypointMarker?.mapView = nil
            
            if (selectedIndex != -1 && selectedIndex < candidates.count) {
                let candidate = candidates[selectedIndex]
                var minLatitude = 99999.9
                var minLongitude = 99999.9
                var maxLatitude = -99999.9
                var maxLongitude = -99999.9
            
                candidate.routes?.forEach({ latLng in
                    if (latLng.latitude < minLatitude) { minLatitude = latLng.latitude }
                    if (latLng.latitude > maxLatitude) { maxLatitude = latLng.latitude }
                    if (latLng.longitude < minLongitude) { minLongitude = latLng.longitude }
                    if (latLng.longitude > maxLongitude) { maxLongitude = latLng.longitude }
                })
                
                let cameraUpdate = NMFCameraUpdate(fit: NMGLatLngBounds(southWest: NMGLatLng(lat: minLatitude, lng: minLongitude), northEast: NMGLatLng(lat: maxLatitude, lng: maxLongitude)), padding: 100)
                cameraUpdate.animation = .easeIn
                mapView?.moveCamera(cameraUpdate)

                
                
                let pathOverlay = NMFPath()
                var routes: [NMGLatLng] = []
                candidate.routes?.forEach({ latLng in
                    routes.append(NMGLatLng(lat: Double(latLng.latitude), lng: Double(latLng.longitude)))
                })
                pathOverlay.path = NMGLineString(points: routes)
                pathOverlay.mapView = mapView
                parent.currentOverlay = pathOverlay
                
                let waypointMarker = NMFMarker()
                waypointMarker.position = NMGLatLng(lat: Double(candidate.routeResult.latLng.latitude), lng: Double(candidate.routeResult.latLng.longitude))
                waypointMarker.iconTintColor = .red
                waypointMarker.mapView = mapView
                parent.waypointMarker = waypointMarker
            }
            
        }
        
        func updateMarkers(markers: [KotlinTriple<KotlinInt, KotlinDouble, KotlinDouble>]) {
            parent.selectedMarker?.mapView = nil
            
            guard let mapView = self.mapView else {
                print("mapView is nil, cannot perform action.")
                return
            }
            
            if (!markers.isEmpty) {
                parent.selectedMarker = NMFMarker()
                parent.selectedMarker?.position = NMGLatLng(lat: Double(truncating: markers.first!.second!), lng: Double(truncating: markers.first!.third!))
                parent.selectedMarker?.mapView = mapView
            }
            
        }
    }
    
    func makeCoordinator() -> Coordinator {
        let coordinator = Coordinator(parent: self)
        return coordinator
    }
}

struct MapSwiftView: View {
//    var selectedLatLng: any RuntimeMutableState
//    var onMapClick: (MapPointF, MapLatLng) -> Void
    
    @EnvironmentObject private var mapEnv: MapEnvironment
    @State private var mapView: MapUIView?
    
    var body: some View {
        VStack {
            MapUIView()
                .environmentObject(mapEnv)
                .onAppear {
                    self.mapView = MapUIView()
                }
                .task {
                    for await _ in mapEnv.$selectedLatLng.values {
                        mapEnv.coordinator?.updateCameraPosition(latLng: mapEnv.selectedLatLng)
                        mapEnv.coordinator?.updateMarkers(markers: mapEnv.markers)
                    }
                }
                .task {
                    for await _ in mapEnv.$markers.values {
                        mapEnv.coordinator?.updateMarkers(markers: mapEnv.markers)
                    }
                }
                .task {
                    for await _ in mapEnv.$candidates.values {
//                        mapEnv.coordinator?.update
                    }
                }
                .task {
                    for await _ in mapEnv.$selectedIndex.values {
                        mapEnv.coordinator?.updateBound(selectedIndex: Int(mapEnv.selectedIndex), candidates: mapEnv.candidates)
                    }
                }
        }
    }
}

class MapViewWrapperViewController: UIHostingController<AnyView> {
    let mapEnv = MapEnvironment()
    
    init() {
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
    
    func updateCurrentLatLng(_ currentLatLng: MapLatLng) { mapEnv.currentLatLng = currentLatLng }
    func updateSelectedLatLng(_ selectedLatLng: MapLatLng) { mapEnv.selectedLatLng = selectedLatLng }
    func updateMarkers(_ markers: [KotlinTriple<KotlinInt, KotlinDouble, KotlinDouble>]) { mapEnv.markers = markers }
    func updateRoutes(_ routes: [MapLatLng]) { mapEnv.routes = routes }
    func updateCandidates(_ candidates: [Candidate]) { mapEnv.candidates = candidates }
    func updateSelectedIndex(_ selectedIndex: Int32) { mapEnv.selectedIndex = selectedIndex }
}
