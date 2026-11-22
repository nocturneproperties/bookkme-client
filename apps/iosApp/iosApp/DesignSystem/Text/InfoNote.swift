import SwiftUI

struct InfoNote: View {
    let text: String
    var title: String?

    var body: some View {
        HStack(alignment: .top, spacing: 12) {
            Image(systemName: "info.circle")
                .font(.title3)
                .foregroundStyle(AppColors.actionText)
            VStack(alignment: .leading, spacing: 2) {
                if let title {
                    Text(title)
                        .fontWeight(.semibold)
                }
                Text(text)
            }
            .foregroundStyle(AppColors.primary)
            Spacer(minLength: 0)
        }
        .padding(16)
        .background(
            AppColors.actionText.opacity(0.2),
            in: RoundedRectangle(cornerRadius: 16, style: .continuous)
        )
    }
}
