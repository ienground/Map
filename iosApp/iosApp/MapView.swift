import SwiftUI
import UIKit
import NMapsMap

struct MapUIView: UIViewRepresentable {
  func makeUIView(context: Context) -> UIView {
    // View를 원하는대로 생성하는 곳
      let map = NMFNaverMapView(frame: UIScreen.main.bounds)
      map.showZoomControls = false
      map.mapView.positionMode = .direction
      map.mapView.zoomLevel = 17
      return map
  }
  
  func updateUIView(_ view: UIView, context: Context) {
      // View를 원하는대로 수정하는 곳
      
  }
}

struct MapSwiftView: View {
    var body: some View {
        MapUIView()
    }
}

class MapViewWrapperViewController: UIHostingController<MapSwiftView> {
    init() {
        super.init(rootView: MapSwiftView())
        setup()
    }
    
    @MainActor @preconcurrency required dynamic init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder, rootView: MapSwiftView())
        setup()
    }
    
    func setup() {
        
    }
    
//    override func viewDidLoad() {
//        super.viewDidLoad()
//        let map = UILabel()
//        map.text = "Hello World"
        
//        
//        view.addSubview(map)
//    }
}
