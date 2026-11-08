import shared
import SwiftUI

struct VerifyEmailScreen: View {
    @EnvironmentObject var navigationStack: NavigationStackHolder
    @StateViewModel var viewModel: VerifyEmailViewModel

    init(pendingSignUp: ApiStartSignUpPendingSignUp) {
        self._viewModel = StateViewModel(wrappedValue: IOSAuthDiKt.verifyEmailVM(pendingSignUp: pendingSignUp))
    }

    var body: some View {
        let uiState = IOSVerifyEmailState.cast(viewModel.uiState)
        List {
            Section {
                Text(uiState.codeSentMessage.localized())
                    .foregroundStyle(AppColors.secondary)
            }
            SectionTextField(uiState.code, textContentType: .oneTimeCode)
                .textFieldStyle(.inList)
            Section {
                TextButton(uiState.resendButton)
					.listRowBackground(Color.clear)
					.listRowInsets(EdgeInsets())
                TextButton(uiState.changeEmailButton)
					.listRowBackground(Color.clear)
					.listRowInsets(EdgeInsets())
			}.listRowSeparator(.hidden)
        }
        .toolbar {
            TextButton(uiState.confirmButton)
        }
        .listSectionSpacing(.compact)
        .scrollDismissesKeyboard(.immediately)
        .withNavigationBar(uiState.appBar)
        .sendLifecycleEventsTo(viewModel)
        .handleNotifications(uiState.notifications)
        .handleNavigation(uiState.navigation) { dest in
            switch dest {
            case is VerifyEmailDestinations.Back:
                navigationStack.popLast()

            default:
                break
            }
        }
    }
}
