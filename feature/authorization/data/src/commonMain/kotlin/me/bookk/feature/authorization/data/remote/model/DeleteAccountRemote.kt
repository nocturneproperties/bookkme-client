package me.bookk.feature.authorization.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
class DeleteAccountRemote(
    val requestId: String,
    val publicKeyCredentialJson: String,
)