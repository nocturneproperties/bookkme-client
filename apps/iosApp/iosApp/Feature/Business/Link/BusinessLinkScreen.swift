import shared
import SwiftUI

struct BusinessLinkScreen: View {
    @EnvironmentObject var navigationStack: NavigationStackHolder
    @StateViewModel var viewModel: BusinessLinkViewModel

    init(businessId: KotlinUuid) {
        self._viewModel = StateViewModel(wrappedValue: IOSBusinessDiKt.businessLinkVM(id: businessId))
    }

    var body: some View {
        let state = IOSBusinessLinkState.cast(viewModel.uiState)
        BusinessLinkContent(state: state)
            .scrollDismissesKeyboard(.immediately)
            .withNavigationBar(state.appBar)
            .sendLifecycleEventsTo(viewModel)
            .handleNotifications(state.notifications)
            .handleNavigation(state.navigation) { destination in
                switch destination {
                case is BusinessLinkDestinations.Back:
                    navigationStack.popLast()
                default:
                    break
                }
            }
    }
}

struct BusinessLinkContent: View {
    let state: IOSBusinessLinkState

    var body: some View {
        List {
            description
            nameSection
            actionsSection
        }
        .listStyle(.automatic)
        .refreshable { await state.refresh.impl().awaitRefresh() }
        .toolbar {
            TextButton(state.save)
        }
    }

    private var description: some View {
        Section {
            InfoNote(text: BusinessRes.strings().business_link_description.desc().localized())
                .listRowInsets(EdgeInsets())
                .listRowBackground(Color.clear)
        }
    }

    private var nameSection: some View {
        Section {
            StateTextField(state.name)
                .textInputAutocapitalization(.never)
                .autocorrectionDisabled()
                .textFieldStyle(.inList)
        } header: {
            Text(BusinessRes.strings().business_link_name_label.desc().localized())
                .textCase(nil)
        } footer: {
            VStack(alignment: .leading, spacing: 8) {
                if let loadError = state.loadError {
                    Text(loadError.localized())
                        .foregroundStyle(AppColors.error)
                }
                Text(state.preview.localized())
                    .font(.body.weight(.semibold))
                    .foregroundStyle(AppColors.actionText)
            }
        }
    }

    private var actionsSection: some View {
        Section {
            if state.share.isVisible {
                TextButton(state.share)
                    .listRowInsets(EdgeInsets())
                    .listRowBackground(Color.clear)
            }
        } footer: {
            Text(BusinessRes.strings().business_link_change_note.desc().localized())
        }
    }
}
