import SwiftUI
import UIKit

struct SharedFile: Identifiable {
    let url: URL

    var id: URL { url }
}

struct ShareSheet: UIViewControllerRepresentable {
    let file: SharedFile

    func makeUIViewController(context: Context) -> UIActivityViewController {
        UIActivityViewController(activityItems: [file.url], applicationActivities: nil)
    }

    func updateUIViewController(_ uiViewController: UIActivityViewController, context: Context) {}
}
