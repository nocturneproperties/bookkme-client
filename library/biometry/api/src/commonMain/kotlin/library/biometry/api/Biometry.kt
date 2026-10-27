package library.biometry.api

import dev.icerock.moko.resources.desc.StringDesc

interface Biometry : BiometryOptManager {
    fun isBiometryAvailable(): Boolean

    suspend fun request(
        title: StringDesc,
        reason: StringDesc,
        cancelText: StringDesc
    )

    sealed interface Error {
        class NotEnrolled() : Throwable(), Error
        class AuthenticationFailed(message: String) : Throwable(message), Error
        class UserCancelled : Throwable(), Error
    }
}