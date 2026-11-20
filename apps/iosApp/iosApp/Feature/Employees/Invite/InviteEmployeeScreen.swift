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
			Section {
				Text(uiState.descriptionText.localized())
					.font(.footnote)
					.foregroundStyle(AppColors.secondary)
				StateButton(uiState.generateCodeButton)
			}
			.listRowSeparator(.hidden)
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

	var body: some View {
		let statusColor = item.status.color.color
		HStack(spacing: 12) {
			VStack(alignment: .leading, spacing: 2) {
				Text(item.code.localized())
					.font(.body)
					.foregroundStyle(AppColors.primary)
					.lineLimit(1)
				Text(item.createdOn.localized())
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
		.onTapGesture {
			item.onClick?()
		}
	}
}
