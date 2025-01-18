//
//  Colors.swift
//  iosApp
//
//  Created by Antiufieiev Michael on 29.12.2024.
//  Copyright © 2024 BookkMe. All rights reserved.
//

import shared
import SwiftUI


class AppColors {
    
    static var background: Color {
        get {
            createColor(
                dark: DesignSystem.colors().darkBackground,
                light: DesignSystem.colors().lightBackground
            )
        }
    }
    static var primary: Color {
        get {
            createColor(
                dark: DesignSystem.colors().darkPrimaryText,
                light: DesignSystem.colors().lightPrimaryText
            )
        }
    }
    static var hintText: Color {
        get {
            createColor(
                dark: DesignSystem.colors().darkHintText,
                light: DesignSystem.colors().lightHintText
            )
        }
    }
    static var elevated: Color {
        get {
            createColor(
                dark: DesignSystem.colors().darkElevated,
                light: DesignSystem.colors().lightElevated
            )
        }
    }
    static var actionText: Color {
        get {
            createColor(
                dark: DesignSystem.colors().darkActionText,
                light: DesignSystem.colors().lightActionText
            )
        }
    }
    static var actionTextDisabled: Color {
        get {
            createColor(
                dark: DesignSystem.colors().darkActionTextDisabled,
                light: DesignSystem.colors().lightActionTextDisabled
            )
        }
    }
    static var inactive: Color {
        get {
            createColor(
                dark: DesignSystem.colors().darkInactive,
                light: DesignSystem.colors().lightInactive
            )
        }
    }
    static var header: Color {
        get {
            createColor(
                dark: DesignSystem.colors().darkHeader,
                light: DesignSystem.colors().lightHeader
            )
        }
    }
    static var success: Color {
        get {
            createColor(
                dark: DesignSystem.colors().darkSuccess,
                light: DesignSystem.colors().lightSuccess
            )
        }
    }
    static var inactiveToggle: Color {
        get {
            createColor(
                dark: DesignSystem.colors().darkInactiveToggle,
                light: DesignSystem.colors().lightInactiveToggle
            )
        }
    }
    static var buttonPrimary: Color {
        get {
            createColor(
                dark: DesignSystem.colors().darkButton,
                light: DesignSystem.colors().lightButton
            )
        }
    }
    static var buttonActive: Color {
        get {
            createColor(
                dark: DesignSystem.colors().darkButtonActive,
                light: DesignSystem.colors().lightButtonActive
            )
        }
    }
    static var buttonInactive: Color {
        get {
            createColor(
                dark: DesignSystem.colors().darkButtonInactive,
                light: DesignSystem.colors().lightButtonInactive
            )
        }
    }
    static var error: Color {
        get {
            createColor(
                dark: DesignSystem.colors().error,
                light: DesignSystem.colors().error
            )
        }
    }
    
    static func createColor(dark:shared.ColorResource, light:shared.ColorResource) -> Color {
        return Color(
            UIColor { traits in
                traits.userInterfaceStyle == .dark ? dark.getUIColor() : light.getUIColor()
            }
        )
    }
}
