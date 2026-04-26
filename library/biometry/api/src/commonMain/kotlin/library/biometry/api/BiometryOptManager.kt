package library.biometry.api

interface BiometryOptManager {
    suspend fun optIn()
    suspend fun optOut()
    suspend fun isOptedIn(): Boolean?
}