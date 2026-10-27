//
//  AppColors+DesignColor.swift
//  iosApp
//

import SwiftUI
import shared

extension ColorToken {
    var color: Color {
		switch self {
		case ColorToken.background:
			AppColors.background
		case ColorToken.primarytext:
			AppColors.primary
		case ColorToken.secondarytext:
			AppColors.secondary
		case ColorToken.hinttext:
			AppColors.hintText
		case ColorToken.elevated:
			AppColors.elevated
		case ColorToken.actiontext:
			AppColors.actionText
		case ColorToken.divider:
			AppColors.divider
		case ColorToken.actiontextdisabled:
			AppColors.actionTextDisabled
		case ColorToken.inactive:
			AppColors.inactive
		case ColorToken.header:
			AppColors.header
		case ColorToken.success:
			AppColors.success
		case ColorToken.error:
			AppColors.error
		case ColorToken.inactivetoggle:
			AppColors.inactiveToggle
		case ColorToken.button:
			AppColors.buttonPrimary
		case ColorToken.buttonactive:
			AppColors.buttonActive
		case ColorToken.buttoninactive:
			AppColors.buttonInactive
		default:
			AppColors.background
		}
    }
}
