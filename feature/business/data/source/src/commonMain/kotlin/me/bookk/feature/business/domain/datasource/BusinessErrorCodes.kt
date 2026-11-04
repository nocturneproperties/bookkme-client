package me.bookk.feature.business.domain.datasource

object BusinessErrorCodes {
    const val BUSINESS_STEP_UP_INVALID = 200040
    const val DATA_EXPORT_TOO_FREQUENT = 900002
    private const val BASE = 200000

    const val BUSINESS_ALREADY_EXIST = BASE + 1
    const val BUSINESS_NAME_VALIDATION_ERROR = BASE + 2
    const val BUSINESS_NOT_FOUND = BASE + 3
    const val BUSINESS_ACTIVE_DAY_WITHOUT_WORK_HOURS = BASE + 19
    const val BUSINESS_INVALID_DAY_OFF_RANGE = BASE + 20
    const val BUSINESS_INVALID_CURRENCY = BASE + 35
    const val BUSINESS_CURRENCY_LOCKED = BASE + 36
    const val BUSINESS_CLIENT_DATA_RETENTION_OUT_OF_RANGE = BASE + 41
}