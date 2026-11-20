import shared
import SwiftUI

struct BusinessDataExportScreen: View {
    @EnvironmentObject var navigationStack: NavigationStackHolder
    @StateViewModel var viewModel: BusinessDataExportViewModel
    @State private var sharedFile: SharedFile?

    init(businessId: KotlinUuid) {
        self._viewModel = StateViewModel(wrappedValue: IOSBusinessDiKt.businessDataExportVM(id: businessId))
    }

    var body: some View {
        let state = IOSBusinessDataExportState.cast(viewModel.uiState)
        BusinessDataExportContent(state: state)
            .withNavigationBar(state.appBar)
            .sendLifecycleEventsTo(viewModel)
            .handleNotifications(state.notifications)
            .handleNavigation(state.navigation) { destination in
                switch destination {
                case is BusinessDataExportDestinations.Back:
                    navigationStack.popLast()
                case let share as BusinessDataExportDestinations.Share:
                    sharedFile = SharedFile(url: URL(fileURLWithPath: share.filePath))
                default:
                    break
                }
            }
            .sheet(item: $sharedFile) { file in
                ShareSheet(file: file)
            }
    }
}

struct BusinessDataExportContent: View {
    let state: IOSBusinessDataExportState

    var body: some View {
        List {
            Section {
                summary
                if state.status.showsFile {
                    fileRow
                }
                if let expiry = state.expiry {
                    HStack(alignment: .firstTextBaseline) {
                        Text(BusinessRes.strings().business_export_expiry_label.desc().localized())
                        Spacer()
                        Text(expiry.localized())
                            .foregroundStyle(AppColors.secondary)
                            .multilineTextAlignment(.trailing)
                    }
                }
            }
            if state.request.isVisible {
                Section {
                    passkeyConfirmation
                }
            }
            Section {
                if state.request.isVisible {
                    StateButton(state.request)
						.listRowInsets(EdgeInsets())
						.listRowBackground(Color.clear)
                }
                if state.download.isVisible {
                    StateButton(state.download)
						.listRowInsets(EdgeInsets())
						.listRowBackground(Color.clear)
                }
                if state.retry.isVisible {
					StateButton(state.retry)
						.listRowInsets(EdgeInsets())
						.listRowBackground(Color.clear)
                }
			} footer: {
                footer
			}
		}.listStyle(.automatic)
    }

    private var summary: some View {
        VStack(alignment: .leading, spacing: 12) {
            statusIndicator
            Text(state.status.title.localized())
                .font(.headline)
                .foregroundStyle(AppColors.primary)
            if let message = state.status.message {
                Text(message.localized())
                    .foregroundStyle(AppColors.secondary)
            }
        }
        .padding(.vertical, 8)
    }

    @ViewBuilder
    private var statusIndicator: some View {
        if state.status.isInProgress {
            ProgressView()
                .controlSize(.large)
                .frame(width: 40, height: 40)
                .tint(state.status.color.color)
        } else if let icon = state.status.icon {
            Image(resource: icon)
                .renderingMode(.template)
                .resizable()
                .scaledToFit()
                .frame(width: 40, height: 40)
                .foregroundStyle(state.status.color.color)
                .accessibilityHidden(true)
        }
    }

    private var fileRow: some View {
        HStack(spacing: 16) {
            Image(resource: DesignSystem.images().document)
                .renderingMode(.template)
                .resizable()
                .scaledToFit()
                .frame(width: 28, height: 32)
                .foregroundStyle(AppColors.secondary)
                .accessibilityHidden(true)
            VStack(alignment: .leading, spacing: 4) {
                Text(BusinessRes.strings().business_export_file_title.desc().localized())
                Text(BusinessRes.strings().business_export_file_type.desc().localized())
                    .font(.subheadline)
                    .foregroundStyle(AppColors.secondary)
            }
        }
    }

    private var passkeyConfirmation: some View {
        HStack(spacing: 12) {
            Image(resource: DesignSystem.images().key)
                .renderingMode(.template)
                .resizable()
                .scaledToFit()
                .frame(width: 20, height: 20)
                .foregroundStyle(AppColors.secondary)
                .accessibilityHidden(true)
            Text(BusinessRes.strings().business_export_confirmation.desc().localized())
                .foregroundStyle(AppColors.primary)
        }
    }

    private var footer: some View {
        VStack(alignment: .leading, spacing: 8) {
            if let requestAvailability = state.requestAvailability {
                Text(requestAvailability.localized())
            }
            if let error = state.error {
                Text(error.localized())
                    .foregroundStyle(AppColors.error)
            }
            if let footer = state.status.footer {
                Text(footer.localized())
            }
        }
        .textCase(nil)
    }
}
