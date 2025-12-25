package me.bookk.feature.business.domain.api

interface RefreshBusinessInfo {
    suspend operator fun invoke()
}