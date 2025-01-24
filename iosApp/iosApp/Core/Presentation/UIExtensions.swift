//
//  UIExtensions.swift
//  iosApp
//
//  Created by Antiufieiev Michael on 26.12.2024.
//  Copyright © 2024 BookkMe. All rights reserved.
//
import shared
import SwiftUI

extension ViewModel:@retroactive ObservableObject {
}

extension Image {
    init(resource: KeyPath<AuthRes.images, shared.ImageResource>) {
        self.init(uiImage: AuthRes.images()[keyPath: resource].toUIImage()!)
    }
}


