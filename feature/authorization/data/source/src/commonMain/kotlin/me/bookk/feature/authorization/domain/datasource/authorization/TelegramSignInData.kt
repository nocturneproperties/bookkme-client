package me.bookk.feature.authorization.domain.datasource.authorization

import me.bookk.feature.authorization.domain.entity.AcceptedLegalDocuments

class TelegramSignInData(
    val initData: String,
    val deviceInfo: SignInData.DeviceInfo,
    val acceptedDocuments: AcceptedLegalDocuments?,
)
