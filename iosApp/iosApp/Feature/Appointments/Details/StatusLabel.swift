//
//  StatusLabel.swift
//  iosApp
//
//  Created by BookkMe on 24.06.2026.
//  Copyright © 2026 BookkMe. All rights reserved.
//

import SwiftUI
import shared

struct StatusLabel: View {
    let status: UIAppointmentStatus

    var body: some View {
        let color = status.color.color
        Text(status.label.localized())
            .font(.subheadline.weight(.semibold))
            .foregroundStyle(color)
            .padding(.horizontal, 12)
            .padding(.vertical, 4)
            .background(color.opacity(0.12))
            .clipShape(RoundedRectangle(cornerRadius: 8))
    }
}
