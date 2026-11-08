import shared
import SwiftUI

struct ChangeEmailScreen: View {
    @EnvironmentObject var navigationStack: NavigationStackHolder
    @StateViewModel var viewModel: ChangeEmailViewModel = IOSAuthDiKt.changeEmailVM()

    var body: some View {
        let uiState = IOSChangeEmailState.cast(viewModel.uiState)
        List {
            Section {
                Text(uiState.explanation.localized())
                    .foregroundStyle(AppColors.secondary)
            }
            SectionTextField(uiState.email, textContentType: .emailAddress)
                .textFieldStyle(.inList)
        }
        .toolbar {
            TextButton(uiState.continueButton)
        }
        .listSectionSpacing(.compact)
        .scrollDismissesKeyboard(.immediately)
        .withNavigationBar(uiState.appBar)
        .sendLifecycleEventsTo(viewModel)
        .handleNotifications(uiState.notifications)
        .handleNavigation(uiState.navigation) { dest in
            switch dest {
            case is ChangeEmailDestinations.Back:
                navigationStack.popLast()

            case let verifyEmail as ChangeEmailDestinations.VerifyEmail:
                navigationStack.push(verifyEmail)

            default:
                break
            }
        }
    }
}
