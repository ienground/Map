import SwiftUI
import ComposeApp

import Foundation
import CoreLocation
import Combine

@main
struct iOSApp: App {
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

class LocationManager: NSObject, CLLocationManagerDelegate, ObservableObject {
    private let locationManager = CLLocationManager()
    
    @Published var location: CLLocation?
    @Published var authorizationStatus: CLAuthorizationStatus = .notDetermined

    override init() {
        super.init()
        locationManager.delegate = self
        requestLocationPermission()
    }

    private func requestLocationPermission() {
        locationManager.requestWhenInUseAuthorization()
    }

    func startUpdatingLocation() {
        if CLLocationManager.locationServicesEnabled() {
            locationManager.startUpdatingLocation()
        }
    }

    func locationManager(_ manager: CLLocationManager, didChangeAuthorization status: CLAuthorizationStatus) {
        authorizationStatus = status
        if status == .authorizedWhenInUse || status == .authorizedAlways {
            startUpdatingLocation()
        }
    }

    func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        location = locations.last
    }
}
