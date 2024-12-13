package me.bookk.domain.environment.api

interface Environment {
    val termsOfUse: String
    val privacyPolicy: String
    val subscriptionPolicy: String
    val feedbackEmail: String
}