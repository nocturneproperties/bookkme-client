package me.bookk.feature.clients.domain.datasource

object ClientErrorCodes {
    private const val BASE = 200000

    const val CLIENT_EXISTS = BASE + 4
    const val STEP_UP_INVALID = BASE + 40
    const val CLIENT_VALIDATION_ERROR = BASE + 5
    const val CLIENT_MISSING_CONTACT_INFO = BASE + 25
}
