import AuthenticationServices
import shared

class IOSPasskeyCredentialUpdater: PasskeyCredentialUpdater {
    private let updater = ASCredentialUpdater()

    func reportUnknownCredential(relyingParty: String, credentialId: String) async throws {
        try await updater.reportUnknownPublicKeyCredential(
            relyingPartyIdentifier: relyingParty,
            credentialID: try Data(base64URLEncoded: credentialId)
        )
    }

    func reportNoAcceptedCredentials(relyingParty: String, userHandle: String) async throws {
        try await updater.reportAllAcceptedPublicKeyCredentials(
            relyingPartyIdentifier: relyingParty,
            userHandle: try Data(base64URLEncoded: userHandle),
            acceptedCredentialIDs: []
        )
    }
}

private struct InvalidBase64URLError: Swift.Error {}

private extension Data {
    init(base64URLEncoded value: String) throws {
        let base64 = value
            .replacingOccurrences(of: "-", with: "+")
            .replacingOccurrences(of: "_", with: "/")
        let padded = base64.padding(
            toLength: (base64.count + 3) / 4 * 4,
            withPad: "=",
            startingAt: 0
        )
        guard let data = Data(base64Encoded: padded) else { throw InvalidBase64URLError() }
        self = data
    }
}
