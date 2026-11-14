package me.bookk.feature.authorization.domain.datasource.email

import me.bookk.feature.authorization.domain.entity.EmailVerification
import kotlin.uuid.Uuid

interface EmailChangeDataSource {
    suspend fun startEmailChange(email: String): EmailVerification
    suspend fun confirmEmailChange(verificationId: Uuid, code: String)
}
