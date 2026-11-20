//
//  KeychainService.swift
//  iosApp
//
//  Created by Dmytro Akulinin on 15.01.2026.
//  Copyright © 2026 ValthSolutions. All rights reserved.
//


import Foundation
import Security

final class KeychainService {

    private let service: String
    private let accessGroup: String?
    
    init(service: String = Bundle.main.bundleIdentifier ?? "app",
         accessGroup: String? = nil) {
        self.service = service
        self.accessGroup = accessGroup
    }

    // MARK: - String
    @discardableResult
    func set(_ value: String, forKey key: String) -> Bool {
        guard let data = value.data(using: .utf8) else { return false }
        return set(data, forKey: key)
    }

    func getString(_ key: String) -> String? {
        guard let data = getData(key) else { return nil }
        return String(data: data, encoding: .utf8)
    }

    // MARK: - Data
    @discardableResult
    func set(_ value: Data, forKey key: String) -> Bool {
        let query = baseQuery(forKey: key)

        let attributesToUpdate: [String: Any] = [
            kSecValueData as String: value,
            kSecAttrAccessible as String: kSecAttrAccessibleWhenUnlockedThisDeviceOnly
        ]

        let updateStatus = SecItemUpdate(query as CFDictionary, attributesToUpdate as CFDictionary)
        if updateStatus == errSecItemNotFound {
            var addQuery = query
            addQuery[kSecValueData as String] = value
            addQuery[kSecAttrAccessible as String] = kSecAttrAccessibleWhenUnlocked
            return SecItemAdd(addQuery as CFDictionary, nil) == errSecSuccess
        }

        return updateStatus == errSecSuccess
    }

    func getData(_ key: String) -> Data? {
        var query = baseQuery(forKey: key)
        query[kSecReturnData as String] = kCFBooleanTrue!
        query[kSecMatchLimit as String] = kSecMatchLimitOne

        var result: AnyObject?
        let status = SecItemCopyMatching(query as CFDictionary, &result)
        guard status == errSecSuccess else { return nil }
        return result as? Data
    }

    // MARK: - Bool
    @discardableResult
    func set(_ value: Bool, forKey key: String) -> Bool {
        set(Data([value ? 1 : 0]), forKey: key)
    }

    fileprivate func getBool(_ key: String) -> Bool? {
        guard let data = getData(key), let byte = data.first else { return nil }
        return byte == 1
    }

    @discardableResult
    func delete(_ key: String) -> Bool {
        let status = SecItemDelete(baseQuery(forKey: key) as CFDictionary)
        return status == errSecSuccess || status == errSecItemNotFound
    }

    // MARK: - Query builder

    private func baseQuery(forKey key: String) -> [String: Any] {
        var query: [String: Any] = [
            kSecClass as String: kSecClassGenericPassword,
            kSecAttrService as String: service,
            kSecAttrAccount as String: key
        ]

        if let accessGroup {
            query[kSecAttrAccessGroup as String] = accessGroup
        }

        return query
    }
}

@propertyWrapper
public struct KeychainWrapper<T> {

    private let key: String
    private let keychain: KeychainService

    public init(key: String, service: String = Bundle.main.bundleIdentifier ?? "app") {
        self.key = key
        self.keychain = KeychainService(service: service)
    }

    public var wrappedValue: T? {
        get {
            switch T.self {
            case is Bool.Type:
                return keychain.getBool(key) as? T
            case is String.Type:
                return keychain.getString(key) as? T
            case is Data.Type:
                return keychain.getData(key) as? T
            default:
                assertionFailure("Unsupported type \(T.self)")
                return nil
            }
        }
        set {
            switch newValue {
            case let v as String:
                keychain.set(v, forKey: key)
            case let v as Bool:
                keychain.set(v, forKey: key)
            case let v as Data:
                keychain.set(v, forKey: key)
            case nil:
                keychain.delete(key)
            default:
                assertionFailure("Unsupported type \(String(describing: newValue))")
            }
        }
    }
}

enum KeychainKeys {
        static let hardwareKeyPin = "token_credential_key_pin"
        static let hardwareKeyBiometric = "token_credential_key_biometric"
}
