package me.bookk.domain.environment.impl

import me.bookk.domain.environment.api.Environment

class DefaultEnvironment : Environment {
    override val privacyPolicy: String = ""
    override val termsOfUse: String = ""
    override val subscriptionPolicy: String = ""
    override val feedbackEmail: String = ""
}