import SwiftUI
import shared

@MainActor
@Observable
final class GlobalMessagePresenter {

	static let shared = GlobalMessagePresenter()

	private(set) var current: PresentationNotificationGlobalMessage?
	@ObservationIgnored var currentFrame: CGRect = .zero

	@ObservationIgnored private var queue: [PresentationNotificationGlobalMessage] = []
	@ObservationIgnored private var window: GlobalMessageWindow?
	@ObservationIgnored private var dismissTask: Task<Void, Never>?

	private init() {}

	func show(_ message: PresentationNotificationGlobalMessage) {
		installWindowIfNeeded()
		queue.append(message)
		if current == nil {
			showNext()
		}
	}

	func dismiss() {
		guard current != nil else { return }
		dismissTask?.cancel()
		currentFrame = .zero
		withAnimation(.easeIn(duration: 0.25)) {
			current = nil
		}
		dismissTask = Task { [weak self] in
			try? await Task.sleep(for: .milliseconds(300))
			guard !Task.isCancelled else { return }
			self?.showNext()
		}
	}

	func performAction() {
		current?.onActionClick?()
		dismiss()
	}

	private func showNext() {
		guard !queue.isEmpty else { return }
		let next = queue.removeFirst()
		syncInterfaceStyle()
		withAnimation(.spring(duration: 0.35)) {
			current = next
		}
		scheduleDismiss(of: next)
	}

	private func scheduleDismiss(of message: PresentationNotificationGlobalMessage) {
		dismissTask?.cancel()
		guard let seconds = message.duration.seconds else { return }
		dismissTask = Task { [weak self] in
			try? await Task.sleep(for: .seconds(seconds))
			guard !Task.isCancelled else { return }
			self?.dismiss()
		}
	}

	private func installWindowIfNeeded() {
		guard window == nil, let scene = activeScene else { return }
		let window = GlobalMessageWindow(windowScene: scene)
		window.windowLevel = .statusBar
		let controller = UIHostingController(rootView: GlobalMessageOverlay(presenter: self))
		controller.view.backgroundColor = .clear
		window.rootViewController = controller
		window.isHidden = false
		self.window = window
	}

	private func syncInterfaceStyle() {
		window?.overrideUserInterfaceStyle = activeScene?.keyWindow?.overrideUserInterfaceStyle ?? .unspecified
	}

	private var activeScene: UIWindowScene? {
		let scenes = UIApplication.shared.connectedScenes.compactMap { $0 as? UIWindowScene }
		return scenes.first { $0.activationState == .foregroundActive } ?? scenes.first
	}
}

final class GlobalMessageWindow: UIWindow {
	override func hitTest(_ point: CGPoint, with event: UIEvent?) -> UIView? {
		guard GlobalMessagePresenter.shared.currentFrame.contains(point) else { return nil }
		return super.hitTest(point, with: event)
	}
}

struct GlobalMessageOverlay: View {

	let presenter: GlobalMessagePresenter

	var body: some View {
		VStack {
			if let message = presenter.current {
				GlobalMessageView(
					message: message,
					onTap: { presenter.dismiss() },
					onAction: { presenter.performAction() }
				)
				.id(message.id)
				.onGeometryChange(for: CGRect.self) { proxy in
					proxy.frame(in: .global)
				} action: { frame in
					presenter.currentFrame = frame
				}
				.onDisappear {
					presenter.currentFrame = .zero
				}
				.transition(.move(edge: .top).combined(with: .opacity))
			}
			Spacer()
		}
		.frame(maxWidth: .infinity)
		.padding(.top, 8)
	}
}

struct GlobalMessageView: View {

	let message: PresentationNotificationGlobalMessage
	let onTap: @MainActor () -> Void
	let onAction: @MainActor () -> Void

	var body: some View {
		HStack(spacing: 8) {
			Image(systemName: message.state.systemImage)
				.font(.system(size: 16, weight: .semibold))
			Text(message.text.localized())
				.font(.subheadline.weight(.medium))
			if let actionText = message.actionText {
				Button {
					onAction()
				} label: {
					Text(actionText.localized())
						.font(.subheadline.weight(.bold))
				}
			}
		}
		.foregroundStyle(AppColors.onAction)
		.padding(.horizontal, 16)
		.padding(.vertical, 10)
		.background(message.state.colorToken().color, in: Capsule())
		.shadow(color: .black.opacity(0.15), radius: 8, y: 4)
		.onTapGesture {
			onTap()
		}
		.padding(.horizontal, 16)
	}
}

private extension PresentationNotificationGlobalMessage.State {
	var systemImage: String {
		switch self {
		case PresentationNotificationGlobalMessage.State.success:
			"checkmark.circle"
		case PresentationNotificationGlobalMessage.State.error:
			"exclamationmark.circle"
		default:
			"info.circle"
		}
	}
}

private extension PresentationNotificationGlobalMessage.Duration {
	var seconds: Double? {
		switch self {
		case PresentationNotificationGlobalMessage.Duration.short_:
			4
		case PresentationNotificationGlobalMessage.Duration.long_:
			10
		default:
			nil
		}
	}
}
