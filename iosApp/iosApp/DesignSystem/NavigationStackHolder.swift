//
//  NavigationStack.swift
//  iosApp
//
//  Created by Antiufieiev Michael on 29.12.2024.
//  Copyright © 2024 BookkMe. All rights reserved.
//

import SwiftUI

public class NavigationStackHolder: ObservableObject {
    
    @Published
    public var path = NavigationPath()
    
}
