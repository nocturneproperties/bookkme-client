import SwiftUI
import shared

struct LegalDocumentsView: View {
    let state: IOSLegalDocumentsSection

    init(state: any LegalDocumentsSection) {
        self.state = IOSLegalDocumentsSection.cast(state)
    }

    var body: some View {
        Section {
            ForEach(state.documents, id: \.id) { document in
                LegalDocumentRow(document: document)
            }
            if state.retry.isVisible {
                TextButton(state.retry, textAlignment: .leading)
            }
        } header: {
            HStack {
                Text(state.title.localized())
                Spacer()
                if state.isLoading {
                    ProgressView()
                        .controlSize(.small)
                        .accessibilityLabel(
                            SettingsRes.strings().settings_legal_loading.desc().localized()
                        )
                }
            }
        } footer: {
            VStack(alignment: .leading, spacing: 12) {
                if let message = state.message {
                    Text(message.localized())
                        .font(.footnote)
                        .foregroundStyle(AppColors.secondary)
                        .fixedSize(horizontal: false, vertical: true)
                }
                if state.accept.isVisible {
                    StateButton(state.accept)
                        .font(.body)
                }
            }
            .padding(.top, 4)
            .textCase(nil)
        }
        .listRowBackground(AppColors.elevated)
    }
}

private struct LegalDocumentRow: View {
    let document: LegalDocumentItem

    var body: some View {
        Button {
            document.onClick?()
        } label: {
            HStack(spacing: 16) {
                VStack(alignment: .leading, spacing: 4) {
                    Text(document.title.localized())
                        .font(.body)
                        .foregroundStyle(AppColors.primary)
                    Text(document.status.localized())
                        .font(.caption)
                        .foregroundStyle(document.statusColor.color)
                }
                .frame(maxWidth: .infinity, alignment: .leading)
                if document.onClick != nil {
                    Image(systemName: "arrow.up.right")
                        .font(.footnote.weight(.semibold))
                        .foregroundStyle(.secondary)
                        .accessibilityHidden(true)
                }
            }
            .frame(minHeight: 32)
            .padding(.vertical, 4)
            .contentShape(Rectangle())
        }
        .disabled(document.onClick == nil)
        .buttonStyle(.plain)
    }
}
