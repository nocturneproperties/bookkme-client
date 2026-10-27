package me.bookk.feature.services.domain.datasource

object ServiceErrorCodes {
    private const val BASE = 200000

    const val BUSINESS_SERVICE_EXISTS = BASE + 7
    const val BUSINESS_SERVICE_NAME_VALIDATION_ERROR = BASE + 8
    const val BUSINESS_SERVICE_NOT_EXISTS = BASE + 9

    const val BUSINESS_SERVICE_GROUP_EXISTS = BASE + 10
    const val BUSINESS_SERVICE_GROUP_VALIDATION_ERROR = BASE + 11
    const val BUSINESS_SERVICE_GROUP_NOT_EXISTS = BASE + 10

    const val BUSINESS_QUOTE_SERVICE_NOT_FOUND = BASE + 13
}