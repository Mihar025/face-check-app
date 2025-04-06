import UIKit
import Flutter
import GoogleMaps

@main
@objc class AppDelegate: FlutterAppDelegate {
  override func application(
    _ application: UIApplication,
    didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?
  ) -> Bool {
    GMSServices.provideAPIKey("AIzaSyCNiHRbGMLzsrWfGiloB-z_gIFjcWNu-d8")
    GeneratedPluginRegistrant.register(with: self)

    if #available(iOS 10.0, *) {
          UNUserNotificationCenter.current().delegate = self
        }

    return super.application(application, didFinishLaunchingWithOptions: launchOptions)
  }
}