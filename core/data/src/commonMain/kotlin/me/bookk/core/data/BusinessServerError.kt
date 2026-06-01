package me.bookk.core.data

import kotlinx.serialization.Serializable

@Serializable
class BusinessServerError(
    val errorCode: Int,
    val message: String
)