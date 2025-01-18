//
//  IOSStateFactoryCreator.swift
//  iosApp
//
//  Created by Antiufieiev Michael on 26.12.2024.
//  Copyright © 2024 BookkMe. All rights reserved.
//
import shared

class IOSStateFactoryCreator:StateFactoryCreator {
    func createAuthFactory() -> any AuthStateFactory {
        return IOSAuthStateFactory()
    }
    
}
