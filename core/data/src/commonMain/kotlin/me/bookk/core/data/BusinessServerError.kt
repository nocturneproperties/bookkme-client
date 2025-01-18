package me.bookk.core.data

import kotlinx.serialization.Serializable

@Serializable
class BusinessServerError(
    val message: String,
    val errorCode: Int
)