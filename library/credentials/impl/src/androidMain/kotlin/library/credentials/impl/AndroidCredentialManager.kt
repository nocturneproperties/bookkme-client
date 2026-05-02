package library.credentials.impl

import android.content.pm.PackageManager
import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import library.credentials.api.CredentialManager
import me.bookk.core.KResult
import me.bookk.core.android.AndroidActivityAware
import me.bookk.core.asKResult
import java.security.GeneralSecurityException
import java.security.KeyStore
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec
import kotlin.io.encoding.Base64


class AndroidCredentialManager : CredentialManager, AndroidActivityAware() {

    override fun encrypt(data: String): KResult<String> {
        return runCatching {
            val masterKey = generateMasterKey()
            val encryptedData = encrypt(data.encodeToByteArray(), masterKey)
            val keyStore = KeyStore.getInstance(ANDROID_STORE_NAME).apply { load(null) }
            val hardwareKey = obtainKey(keyStore, false)
            val hardwareWrappedKey = encrypt(masterKey.encoded, hardwareKey)
            val encryptionBody = hardwareWrappedKey + encryptedData
            Base64.encode(encryptionBody)
        }.recoverCatching(::processEncryptionException).asKResult()
    }

    override fun decrypt(data: String): KResult<String> {
        return runCatching {
            val base64Unwrapped = Base64.decode(data)
            val hardwareWrapped = base64Unwrapped.sliceArray(0 until MONO_ENCR_KEY_BYTE_LENGTH)
            val encryptedData =
                base64Unwrapped.sliceArray(MONO_ENCR_KEY_BYTE_LENGTH until base64Unwrapped.size)
            val keyStore = KeyStore.getInstance(ANDROID_STORE_NAME).apply { load(null) }
            val hardwareKey = obtainKey(keyStore, false)
            val hardwareUnwrappedKey = decrypt(hardwareWrapped, hardwareKey)
            val masterKey = generateMasterKey(hardwareUnwrappedKey)
            val decrypted = decrypt(encryptedData, masterKey)
            decrypted.decodeToString()
        }.recoverCatching(::processEncryptionException).asKResult()
    }

    override fun encrypt(secret: String, data: String): KResult<String> {
        return runCatching {
            val masterKey = generateMasterKey()
            val encryptedData = encrypt(data.encodeToByteArray(), masterKey)
            val keyStore = KeyStore.getInstance(ANDROID_STORE_NAME).apply { load(null) }
            val hardwareKey = obtainKey(keyStore, false)
            val hardwareWrappedKey = encrypt(masterKey.encoded, hardwareKey)
            val (pinKey, salt) = createPinEncryptionKey(secret.toCharArray())
            val pinWrappedKey = encrypt(hardwareWrappedKey, pinKey)
            val encryptionBody = salt + pinWrappedKey + encryptedData
            Base64.encode(encryptionBody)
        }.recoverCatching(::processEncryptionException).asKResult()
    }

    override fun decrypt(secret: String, data: String): KResult<String> {
        return runCatching {
            val base64Decoded = Base64.decode(data)
            val salt = base64Decoded.sliceArray(0 until SALT_BYTE_LENGTH)
            val pinWrappedKey =
                base64Decoded.sliceArray(SALT_BYTE_LENGTH until (SALT_BYTE_LENGTH + DOUBLE_ENCR_KEY_BYTE_LENGTH))
            val encryptedData =
                base64Decoded.sliceArray((SALT_BYTE_LENGTH + DOUBLE_ENCR_KEY_BYTE_LENGTH) until base64Decoded.size)
            val (pinKey) = createPinEncryptionKey(secret.toCharArray(), salt)
            val pinUnwrappedKey = decrypt(pinWrappedKey, pinKey)
            val keyStore = KeyStore.getInstance(ANDROID_STORE_NAME).apply { load(null) }
            val hardwareKey = obtainKey(keyStore, false)
            val hardwareUnwrappedKey = decrypt(pinUnwrappedKey, hardwareKey)
            val masterKey = generateMasterKey(hardwareUnwrappedKey)
            val decrypted = decrypt(encryptedData, masterKey)
            decrypted.decodeToString()
        }.recoverCatching(::processEncryptionException).asKResult()
    }

    override fun encryptUsingBiometry(data: String): KResult<String> {
        return runCatching {
            val masterKey = generateMasterKey()
            val encryptedData = encrypt(data.encodeToByteArray(), masterKey)
            val keyStore = KeyStore.getInstance(ANDROID_STORE_NAME).apply { load(null) }
            val hardwareKey = obtainKey(keyStore, true)
            val hardwareWrappedKey = encrypt(masterKey.encoded, hardwareKey)
            val encryptionBody = hardwareWrappedKey + encryptedData
            Base64.encode(encryptionBody)
        }.recoverCatching(::processEncryptionException).asKResult()
    }

    override fun decryptUsingBiometry(data: String): KResult<String> {
        return runCatching {
            val base64Unwrapped = Base64.decode(data)
            val hardwareWrapped = base64Unwrapped.sliceArray(0 until MONO_ENCR_KEY_BYTE_LENGTH)
            val encryptedData =
                base64Unwrapped.sliceArray(MONO_ENCR_KEY_BYTE_LENGTH until base64Unwrapped.size)
            val keyStore = KeyStore.getInstance(ANDROID_STORE_NAME).apply { load(null) }
            val hardwareKey = obtainKey(keyStore, true)
            val hardwareUnwrappedKey = decrypt(hardwareWrapped, hardwareKey)
            val masterKey = generateMasterKey(hardwareUnwrappedKey)
            val decrypted = decrypt(encryptedData, masterKey)
            decrypted.decodeToString()
        }.recoverCatching(::processEncryptionException).asKResult()
    }

    override fun generateSecureRandomString(): KResult<String> {
        return KResult.Success(Base64.encode(ByteArray(32).random()))
    }

    private fun processEncryptionException(throwable: Throwable): String {
        throw when (throwable) {
            is GeneralSecurityException -> CredentialManager.Error.OperationFailed()
            else -> throwable
        }
    }

    private fun createPinEncryptionKey(
        pin: CharArray,
        salt: ByteArray = ByteArray(16).random()
    ): Pair<SecretKey, ByteArray> {
        val factory = SecretKeyFactory.getInstance(PIN_TRANSFORMATION_TYPE)
        val spec = PBEKeySpec(pin, salt, 65536, 256)
        val secretKey = factory.generateSecret(spec)
        return SecretKeySpec(secretKey.encoded, "AES") to salt
    }

    private fun encrypt(data: ByteArray, key: SecretKey): ByteArray {
        val cipher = Cipher.getInstance(TRANSFORMATION_TYPE)
        cipher.init(Cipher.ENCRYPT_MODE, key)
        val iv = cipher.iv
        val result = cipher.doFinal(data)
        return iv + result
    }

    private fun decrypt(data: ByteArray, key: SecretKey): ByteArray {
        val cipher = Cipher.getInstance(TRANSFORMATION_TYPE)
        val iv = data.sliceArray(0 until GCM_IV_BYTE_LENGTH)
        val payload = data.sliceArray(GCM_IV_BYTE_LENGTH until data.size)
        val spec = GCMParameterSpec(128, iv)
        cipher.init(Cipher.DECRYPT_MODE, key, spec)
        return cipher.doFinal(payload)
    }

    private fun obtainKey(keyStore: KeyStore, biometric: Boolean): SecretKey {
        val alias = if (biometric) CREDENTIAL_KEY_BIOMETRIC else CREDENTIAL_KEY_PIN
        val privateKey = keyStore.getKey(alias, null)

        if (privateKey == null) {
            return generateKeystorePrivateKey(alias, biometric)
        }
        if (privateKey !is SecretKey) {
            keyStore.deleteEntry(alias)
            return generateKeystorePrivateKey(alias, biometric)
        }
        return privateKey
    }

    private fun generateMasterKey(body: ByteArray? = null): SecretKeySpec {
        val keyBody = body ?: ByteArray(32).random()
        return SecretKeySpec(keyBody, "AES")
    }

    private fun ByteArray.random(): ByteArray {
        SecureRandom.getInstanceStrong().nextBytes(this)
        return this
    }

    private fun generateKeystorePrivateKey(alias: String, biometric: Boolean): SecretKey {
        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            ANDROID_STORE_NAME
        )

        val builder = KeyGenParameterSpec.Builder(
            alias,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setKeySize(256)
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setIsStrongBoxBacked(hasStrongBox())
            .apply {
                if (biometric) {
                    setInvalidatedByBiometricEnrollment(true)
                    setUserAuthenticationRequired(true)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        setUserAuthenticationParameters(
                            30,
                            KeyProperties.AUTH_BIOMETRIC_STRONG
                        )
                    } else {
                        @Suppress("DEPRECATION")
                        setUserAuthenticationValidityDurationSeconds(-1)
                    }
                }
            }
        keyGenerator.init(builder.build())
        return keyGenerator.generateKey()
    }

    private fun hasStrongBox(): Boolean {
        return requireActivity().packageManager
            .hasSystemFeature(PackageManager.FEATURE_STRONGBOX_KEYSTORE)
    }

    companion object {
        private const val CREDENTIAL_KEY_BIOMETRIC = "token_credential_key_biometric"
        private const val CREDENTIAL_KEY_PIN = "token_credential_key_pin"
        private const val ANDROID_STORE_NAME = "AndroidKeyStore"
        private const val TRANSFORMATION_TYPE = "AES/GCM/NoPadding"
        private const val PIN_TRANSFORMATION_TYPE = "PBKDF2WithHmacSHA256"
        private const val GCM_IV_BYTE_LENGTH = 12
        private const val SALT_BYTE_LENGTH = 16
        private const val DOUBLE_ENCR_KEY_BYTE_LENGTH = 88
        private const val MONO_ENCR_KEY_BYTE_LENGTH = 60
    }

}