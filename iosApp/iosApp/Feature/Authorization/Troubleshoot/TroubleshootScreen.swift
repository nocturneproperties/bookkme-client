//
//  TroubleshootScreen.swift
//  iosApp
//
//  Created by BookkMe on 04.02.2025.
//  Copyright © 2025 BookkMe. All rights reserved.
//

import shared
import SwiftUI

struct TroubleshootScreen: View {
    @EnvironmentObject var navigationStack: NavigationStackHolder
    @StateObject var troubleshootVM: TroubleshootViewModel = AuthDiKt.troubleshootVM()
    
    
    var body: some View {
        let uiState = troubleshootVM.uiState
        VStack {
            TroubleshootCard(troubleshootInfo: uiState.troubleshootCardStaticData)
            Spacer()
            StateButton(state: uiState.contactSupportButton.impl()) {
                troubleshootVM.onContactSupportClick()
            }
        }
        .padding()
        .navigationTitle(troubleshootVM.uiState.appBar.title.localized())
        .navigationBarTitleDisplayMode(.large)
    }
}



struct TroubleshootCard: View {
    
    @State
    var troubleshootInfo: TroubleshootCardData
    
    var body: some View {
        VStack {
            HStack {
                Text(troubleshootInfo.title.localized())
                    .font(.body)
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .fixedSize(horizontal: false, vertical: true)
                Image(resource: \.passkey)
                    .resizable()
                    .aspectRatio(contentMode: .fit)
                    .frame(width: 48, height: 48)
            }
            Divider()
                .background(AppColors.divider)
                .padding(.vertical, 8)
            
            ForEach(troubleshootInfo.reasons, id: \.id) { reason in
                TroubleshootReason(reason: reason)
            }
        }
        .padding(24)
        .background(AppColors.elevated)
        .cornerRadius(10)
    }
}

struct TroubleshootReason: View {
    
    let reason: TroubleshootCardData.Reason
    @State
    var isExpanded: Bool = false
    
    var body: some View {
        VStack {
            HStack {
                Text(reason.title.localized())
                    .font(.subheadline)
                    .fontWeight(.semibold)
                    .frame(maxWidth: .infinity, alignment: .leading)
                let degrees = isExpanded ? 90.0 : 0.0
                Image(systemName: "chevron.forward")
                    .rotationEffect(.degrees(degrees))
            }
            
            if (isExpanded) {
                Text(reason.description_.localized())
                    .font(.caption)
                    .foregroundStyle(AppColors.secondary)
                    .padding(.top, 8)
                    .padding(.trailing, 8)
            }
            
        }
        .contentShape(Rectangle())
        .onTapGesture {
            withAnimation {
                isExpanded.toggle()
            }
        }
        .padding(.vertical, 8)
    }
}
