//
//  KMMPreview.swift
//  iosApp
//
//  Created by Antiufieiev Michael on 29.12.2024.
//  Copyright © 2024 BookkMe. All rights reserved.
//

import shared
import SwiftUI

struct KMMPreviewView<Content:View>:View {
    
    let viewBuilder: () -> Content
    
    init(@ViewBuilder builder: @escaping () -> Content) {
        DISetupKt.doInitDI(creator: IOSStateFactoryCreator())
        self.viewBuilder = builder
    }
    
    var body: some View {
        viewBuilder()
    }
}
