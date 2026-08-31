import SwiftUI
import shared

struct InviteEmployeeScreen: View {
	@EnvironmentObject var navigationStack: NavigationStackHolder
	@StateViewModel var viewModel: InviteEmployeeViewModel

	init(businessId: KotlinUuid) {
		_viewModel = StateViewModel(wrappedValue: IosEmployeesPresentationDiKt.inviteEmployeeVM(businessId: businessId))
	}

	var body: some View {
		let uiState = IOSInviteEmployeeState.cast(viewModel.uiState)
		let invitationsList = IOSListState<InvitationItem>.cast(uiState.invitationsList)

		ListGroup(listState: invitationsList, listStyle: .insetGrouped, content: { item in
			InvitationRow(item: item)
		}, header: {
			
			SectionTextField(uiState.emailField, header: uiState.descriptionText.localized())
				.textFieldStyle(.inList)
				.textInputAutocapitalization(.never)
				.disableAutocorrection(true)
			
			Section {
				StateButton(uiState.sendButton)
					.listRowInsets(EdgeInsets(top: 0, leading: 0, bottom: 32, trailing: 0))
					.listRowBackground(Color.clear)
			}
			.listSectionSpacing(.compact)
			
		})
		.refreshable { await uiState.refreshState.impl().awaitRefresh() }
		.withNavigationBar(uiState.appBar)
		.sendLifecycleEventsTo(viewModel)
		.handleNotifications(uiState.notifications)
		.handleNavigation(uiState.navigation) { destination in
			switch destination {
			case is InviteEmployeeDestinations.Back:
				navigationStack.popLast()
			default:
				break
			}
		}
	}
}

private struct InvitationRow: View {
	let item: InvitationItem

	@State private var longPressCount = 0

	var body: some View {
		let statusColor = item.status.color.color
		HStack(spacing: 12) {
			Text(item.initials)
				.font(.footnote.weight(.medium))
				.foregroundStyle(AppColors.actionText)
				.frame(width: 40, height: 40)
				.background(AppColors.actionText.opacity(0.1))
				.clipShape(Circle())

			VStack(alignment: .leading, spacing: 2) {
				Text(item.email)
					.font(.body)
					.foregroundStyle(AppColors.primary)
					.lineLimit(1)
				Text(item.sentOn.localized())
					.font(.caption)
					.foregroundStyle(AppColors.secondary)
			}

			Spacer()

			Text(item.status.label.localized())
				.font(.caption2)
				.padding(.horizontal, 8)
				.padding(.vertical, 4)
				.background(statusColor.opacity(0.15))
				.foregroundStyle(statusColor)
				.clipShape(Capsule())
				.lineLimit(1)
		}
		.contentShape(Rectangle())
		.onLongPressGesture {
			guard let onLongPress = item.onLongPress else { return }
			longPressCount += 1
			onLongPress()
		}
		.sensoryFeedback(.impact(weight: .medium), trigger: longPressCount)
	}
}
