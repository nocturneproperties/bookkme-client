package me.bookk.feature.business.domain.datasource

object BusinessErrorCodes {
    private const val BASE = 200000

    const val BUSINESS_ALREADY_EXIST = BASE + 1
    const val BUSINESS_NAME_VALIDATION_ERROR = BASE + 2
    const val BUSINESS_NOT_FOUND = BASE + 3
    const val BUSINESS_ACTIVE_DAY_WITHOUT_WORK_HOURS = BASE + 19
    const val BUSINESS_INVALID_DAY_OFF_RANGE = BASE + 20
}