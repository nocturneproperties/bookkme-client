package library.credentials.api

import me.bookk.core.KResult

interface CredentialManager {
    /**
     * Encrypts provided data with platform-protected private key
     *
     * This method **MUST** return different outputs for the same input parameters
     *
     * @param data - Raw string data that needs to be encrypted
     *
     * @return [kotlin.String] - Base64 encoded encrypted data
     */
    fun encrypt(data: String): KResult<String>

    /**
     * Decrypts provided data with platform-protected private key
     *
     * @param data - Base64 encoded encrypted data
     *
     * @return [kotlin.String] - Decrypted UTF-8 string
     */
    fun decrypt(data: String): KResult<String>

    /**
     * Encrypts provided data with PIN-dependent encryption key and platform-protected private key
     *
     * This method **MUST** return different outputs for the same input parameters
     *
     * @param secret - secret that will protect data, different secrets codes must emit different encrypted data
     * @param data - Raw string data that needs to be encrypted
     *
     * @return [kotlin.String] - Base64 encoded encrypted data
     */
    fun encrypt(secret: String, data: String): KResult<String>

    /**
     * Decrypts provided data with PIN-dependent encryption key and platform-protected private key
     *
     * @param secret - secret that will protect data, different secret codes must emit different encrypted data
     * @param data - Base64 encoded encrypted data
     *
     * @return [kotlin.String] - Decrypted UTF-8 string
     */
    fun decrypt(secret: String, data: String): KResult<String>

    /**
     * Encrypts provided data with biometric-protected platform private key.
     * Using this method without prior biometric identification will result in error.
     *
     * This method **MUST** return different outputs for the same input parameters
     *
     * @param data - Raw string data that needs to be encrypted
     *
     * @return [kotlin.String] - Base64 encoded encrypted data
     */
    fun encryptUsingBiometry(data: String): KResult<String>

    /**
     * Decrypts provided data with biometric-protected platform private key.
     * Using this method without prior biometric identification will result in error.
     *
     * @param data - Base64 encoded encrypted data
     *
     * @return [kotlin.String] - Decrypted UTF-8 string
     */
    fun decryptUsingBiometry(data: String): KResult<String>

    /**
     * Generates random 32-byte base64-encoded string
     *
     * @return [kotlin.String] - 32-byte random string
     */
    fun generateSecureRandomString(): KResult<String>

    sealed interface Error {
        class OperationFailed : Error, Throwable()
    }
}