//
//  CredentialManagerTemporaryStub.swift
//  iosApp
//

import Foundation
import Security
import CryptoKit
import LocalAuthentication
import shared
import CommonCrypto

final class IOSCredentialManager: CredentialManager {

    private let hardwareKeyStore = HardwareKeyStore(key: KeychainKeys.hardwareKeyPin) // non-bio
    private let biometricKeyStore = BiometricHardwareKeyStore(account: KeychainKeys.hardwareKeyBiometric) // bio

    // MARK: - Mono (device-protected)

    func encrypt(data: String) -> any KResult {
        do {
            let masterKey = try Self.randomBytes(count: 32)
            let plaintext = Data(data.utf8)
            let encryptedData = try Self.aesGcmEncrypt(plaintext, keyData: masterKey)

            let hardwareKey = try obtainOrCreateHardwareKey()
            let wrappedMasterKey = try Self.aesGcmEncrypt(masterKey, keyData: hardwareKey)
            guard wrappedMasterKey.count == Self.MONO_ENCR_KEY_BYTE_LENGTH else { throw CryptoError.cryptoFailure }

            var body = Data()
            body.append(wrappedMasterKey)
            body.append(encryptedData)

            return kSuccess(body.base64EncodedString())
        } catch {
            return kFailure()
        }
    }

    func decrypt(data: String) -> any KResult {
        do {
            let decoded = try Self.base64DecodeOrThrow(data)
            guard decoded.count > Self.MONO_ENCR_KEY_BYTE_LENGTH else { throw CryptoError.cryptoFailure }

            let wrappedMasterKey = decoded.subdata(in: 0..<Self.MONO_ENCR_KEY_BYTE_LENGTH)
            let encryptedData = decoded.subdata(in: Self.MONO_ENCR_KEY_BYTE_LENGTH..<decoded.count)

            let hardwareKey = try readHardwareKeyOrFail()
            let masterKey = try Self.aesGcmDecrypt(wrappedMasterKey, keyData: hardwareKey)
            guard masterKey.count == 32 else { throw CryptoError.cryptoFailure }

            let plaintext = try Self.aesGcmDecrypt(encryptedData, keyData: masterKey)
            guard let string = String(data: plaintext, encoding: .utf8) else { throw CryptoError.cryptoFailure }

            return kSuccess(string)
        } catch {
            return kFailure()
        }
    }
    
    func generateSecureRandomString() -> any KResult {
        do {
            return kSuccess(try Self.randomBytes(count: 32).base64EncodedString())
        } catch {
           return kFailure()
       }
    }

    // MARK: - Double (pin + device-protected)

    func encrypt(secret pin: String, data: String) -> any KResult {
        do {
            // 1) masterKey
            let masterKey = try Self.randomBytes(count: 32)

            // 2) encrypt payload with masterKey
            let plaintext = Data(data.utf8)
            let encryptedData = try Self.aesGcmEncrypt(plaintext, keyData: masterKey)

            // 3) wrap masterKey with persistent hardwareKey
            let hardwareKey = try obtainOrCreateHardwareKey()
            let hardwareWrappedKey = try Self.aesGcmEncrypt(masterKey, keyData: hardwareKey)
            guard hardwareWrappedKey.count == Self.MONO_ENCR_KEY_BYTE_LENGTH else { throw CryptoError.cryptoFailure }

            // 4) derive pinKey + salt, wrap hardwareWrappedKey with pinKey
            let salt = try Self.randomBytes(count: Self.SALT_BYTE_LENGTH)
            let pinKey = try Self.derivePinKey(pin: pin, salt: salt)
            let pinWrappedKey = try Self.aesGcmEncrypt(hardwareWrappedKey, keyData: pinKey)
            guard pinWrappedKey.count == Self.DOUBLE_ENCR_KEY_BYTE_LENGTH else { throw CryptoError.cryptoFailure }

            // 5) body = salt + pinWrappedKey + encryptedData
            var body = Data()
            body.append(salt)
            body.append(pinWrappedKey)
            body.append(encryptedData)

            return kSuccess(body.base64EncodedString())
        } catch {
            return kFailure()
        }
    }

    func decrypt(secret pin: String, data: String) -> any KResult {
        do {
            let decoded = try Self.base64DecodeOrThrow(data)
            let headerSize = Self.SALT_BYTE_LENGTH + Self.DOUBLE_ENCR_KEY_BYTE_LENGTH
            guard decoded.count > headerSize else { throw CryptoError.cryptoFailure }

            let salt = decoded.subdata(in: 0..<Self.SALT_BYTE_LENGTH)
            let pinWrappedKey = decoded.subdata(in: Self.SALT_BYTE_LENGTH..<headerSize)
            let encryptedData = decoded.subdata(in: headerSize..<decoded.count)

            // 1) derive pinKey (same salt)
            let pinKey = try Self.derivePinKey(pin: pin, salt: salt)

            // 2) unwrap hardwareWrappedKey (should become 60 bytes)
            let hardwareWrappedKey = try Self.aesGcmDecrypt(pinWrappedKey, keyData: pinKey)
            guard hardwareWrappedKey.count == Self.MONO_ENCR_KEY_BYTE_LENGTH else { throw CryptoError.cryptoFailure }

            // 3) unwrap masterKey using hardwareKey (should become 32 bytes)
            let hardwareKey = try readHardwareKeyOrFail()
            let masterKey = try Self.aesGcmDecrypt(hardwareWrappedKey, keyData: hardwareKey)
            guard masterKey.count == 32 else { throw CryptoError.cryptoFailure }

            // 4) decrypt data
            let plaintext = try Self.aesGcmDecrypt(encryptedData, keyData: masterKey)
            guard let string = String(data: plaintext, encoding: .utf8) else { throw CryptoError.cryptoFailure }

            return kSuccess(string)
        } catch {
            return kFailure()
        }
    }

    // MARK: - Mono (biometry-protected)

    func encryptUsingBiometry(data: String) -> any KResult {
        do {
            let masterKey = try Self.randomBytes(count: 32)
            let plaintext = Data(data.utf8)
            let encryptedData = try Self.aesGcmEncrypt(plaintext, keyData: masterKey)

            // hardware key protected by biometry (Keychain access control)
            let hardwareKey = try obtainOrCreateBiometricHardwareKey()
            let wrappedMasterKey = try Self.aesGcmEncrypt(masterKey, keyData: hardwareKey)
            guard wrappedMasterKey.count == Self.MONO_ENCR_KEY_BYTE_LENGTH else { throw CryptoError.cryptoFailure }

            var body = Data()
            body.append(wrappedMasterKey)
            body.append(encryptedData)

            return kSuccess(body.base64EncodedString())
        } catch {
            return kFailure()
        }
    }

    func decryptUsingBiometry(data: String) -> any KResult {
        do {
            let decoded = try Self.base64DecodeOrThrow(data)
            guard decoded.count > Self.MONO_ENCR_KEY_BYTE_LENGTH else { throw CryptoError.cryptoFailure }

            let wrappedMasterKey = decoded.subdata(in: 0..<Self.MONO_ENCR_KEY_BYTE_LENGTH)
            let encryptedData = decoded.subdata(in: Self.MONO_ENCR_KEY_BYTE_LENGTH..<decoded.count)

            // Reading this key should trigger biometry if needed.
            let hardwareKey = try readBiometricHardwareKeyOrFail()

            let masterKey = try Self.aesGcmDecrypt(wrappedMasterKey, keyData: hardwareKey)
            guard masterKey.count == 32 else { throw CryptoError.cryptoFailure }

            let plaintext = try Self.aesGcmDecrypt(encryptedData, keyData: masterKey)
            guard let string = String(data: plaintext, encoding: .utf8) else { throw CryptoError.cryptoFailure }

            return kSuccess(string)
        } catch {
            return kFailure()
        }
    }

}

// MARK: - Helpers
private extension IOSCredentialManager {

    static let GCM_IV_BYTE_LENGTH = 12
    static let SALT_BYTE_LENGTH = 16
    static let MONO_ENCR_KEY_BYTE_LENGTH = 60
    static let DOUBLE_ENCR_KEY_BYTE_LENGTH = 88

    static let PBKDF2_ITERATIONS: UInt32 = 65_536
    static let PBKDF2_KEY_LENGTH = 32 // 256-bit

    func obtainOrCreateHardwareKey() throws -> Data {
        if let existing = hardwareKeyStore.get(), existing.count == 32 {
            return existing
        }
        let created = try Self.randomBytes(count: 32)
        hardwareKeyStore.set(created)
        return created
    }

    func readHardwareKeyOrFail() throws -> Data {
        guard let key = hardwareKeyStore.get(), key.count == 32 else {
            throw CryptoError.cryptoFailure
        }
        return key
    }

    func obtainOrCreateBiometricHardwareKey() throws -> Data {
        if let existing = try biometricKeyStore.get(prompt: Self.biometryPrompt), existing.count == 32 {
            return existing
        }
        let created = try Self.randomBytes(count: 32)
        try biometricKeyStore.set(created)
        return created
    }

    func readBiometricHardwareKeyOrFail() throws -> Data {
        guard let key = try biometricKeyStore.get(prompt: Self.biometryPrompt), key.count == 32 else {
            throw CryptoError.cryptoFailure
        }
        return key
    }

    static var biometryPrompt: String {
        "Authenticate to access secure data"
    }

    static func base64DecodeOrThrow(_ str: String) throws -> Data {
        guard let decoded = Data(base64Encoded: str) else { throw CryptoError.cryptoFailure }
        return decoded
    }

    // AES-GCM combined: nonce(12) + ciphertext + tag(16)
    static func aesGcmEncrypt(_ data: Data, keyData: Data) throws -> Data {
        do {
            let key = SymmetricKey(data: keyData)
            let sealed = try AES.GCM.seal(data, using: key)
            guard let combined = sealed.combined else { throw CryptoError.cryptoFailure }
            return combined
        } catch {
            throw CryptoError.cryptoFailure
        }
    }

    static func aesGcmDecrypt(_ combined: Data, keyData: Data) throws -> Data {
        do {
            let key = SymmetricKey(data: keyData)
            let box = try AES.GCM.SealedBox(combined: combined)
            return try AES.GCM.open(box, using: key)
        } catch {
            throw CryptoError.cryptoFailure
        }
    }

    static func randomBytes(count: Int) throws -> Data {
        var data = Data(count: count)
        let status = data.withUnsafeMutableBytes { buf in
            SecRandomCopyBytes(kSecRandomDefault, count, buf.baseAddress!)
        }
        guard status == errSecSuccess else {
            throw CryptoError.cryptoFailure
        }
        return data
    }

    /// PBKDF2-HMAC-SHA256 -> 32 bytes key (AES-256)
    static func derivePinKey(pin: String, salt: Data) throws -> Data {
        let passwordData = Data(pin.utf8)

        var derived = Data(count: PBKDF2_KEY_LENGTH)
        let result = derived.withUnsafeMutableBytes { derivedBuf in
            salt.withUnsafeBytes { saltBuf in
                passwordData.withUnsafeBytes { passBuf in
                    CCKeyDerivationPBKDF(
                        CCPBKDFAlgorithm(kCCPBKDF2),
                        passBuf.bindMemory(to: Int8.self).baseAddress!,
                        passwordData.count,
                        saltBuf.bindMemory(to: UInt8.self).baseAddress!,
                        salt.count,
                        CCPseudoRandomAlgorithm(kCCPRFHmacAlgSHA256),
                        PBKDF2_ITERATIONS,
                        derivedBuf.bindMemory(to: UInt8.self).baseAddress!,
                        PBKDF2_KEY_LENGTH
                    )
                }
            }
        }

        guard result == kCCSuccess else { throw CryptoError.cryptoFailure }
        return derived
    }
}

// MARK: - Keychain stores

/// Existing wrapper-based store (no biometry)
struct HardwareKeyStore {
    private let keychain: KeychainService = KeychainService()
    private let key: String

    init(key: String) { self.key = key }

    func get() -> Data? {
        keychain.getData(key)
    }

    func set(_ data: Data?) {
        if let data {
            _ = keychain.set(data, forKey: key)
        } else {
            _ = keychain.delete(key)
        }
    }
}

/// Biometry-protected store using SecAccessControl
final class BiometricHardwareKeyStore {

    private let service: String
    private let account: String

    init(service: String = Bundle.main.bundleIdentifier ?? "app", account: String) {
        self.service = service
        self.account = account
    }

    func set(_ data: Data) throws {
        // delete old
        _ = SecItemDelete(baseQuery() as CFDictionary)

        var error: Unmanaged<CFError>?
        guard let access = SecAccessControlCreateWithFlags(
            nil,
            kSecAttrAccessibleWhenUnlockedThisDeviceOnly,
            [.biometryCurrentSet],
            &error
        ) else {
            throw CryptoError.cryptoFailure
        }

        var query = baseQuery()
        query[kSecValueData as String] = data
        query[kSecAttrAccessControl as String] = access

        // No prompt needed for add; access control is enforced on read/use.
        let status = SecItemAdd(query as CFDictionary, nil)
        guard status == errSecSuccess else { throw CryptoError.cryptoFailure }
    }

    func get(prompt: String) throws -> Data? {
        var query = baseQuery()
        query[kSecReturnData as String] = true
        query[kSecMatchLimit as String] = kSecMatchLimitOne
        query[kSecUseAuthenticationContext as String] = BiometryContext.shared.current

        var item: CFTypeRef?
        let status = SecItemCopyMatching(query as CFDictionary, &item)

        if status == errSecItemNotFound { return nil }
        guard status == errSecSuccess else { throw CryptoError.cryptoFailure }
        guard let data = item as? Data else { throw CryptoError.cryptoFailure }
        return data
    }

    private func baseQuery() -> [String: Any] {
        [
            kSecClass as String: kSecClassGenericPassword,
            kSecAttrService as String: service,
            kSecAttrAccount as String: account
        ]
    }
}

// MARK: - KResult mapping

private func kSuccess(_ value: String) -> any KResult {
    KResultSuccess(value: value as NSString)
}

private func kFailure() -> any KResult {
    KResultFailure(error: CredentialManagerErrorOperationFailed())
}

// MARK: - Error

enum CryptoError: Swift.Error {
    case cryptoFailure
}
