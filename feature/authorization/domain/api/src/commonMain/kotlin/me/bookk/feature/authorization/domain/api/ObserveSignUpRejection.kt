package me.bookk.feature.authorization.domain.api

import kotlinx.coroutines.flow.Flow
import me.bookk.feature.authorization.domain.entity.SignUpRejection

interface ObserveSignUpRejection {
    operator fun invoke(): Flow<SignUpRejection?>
}
