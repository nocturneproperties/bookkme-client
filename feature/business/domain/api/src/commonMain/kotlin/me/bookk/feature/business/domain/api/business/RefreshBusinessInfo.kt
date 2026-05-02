package me.bookk.feature.business.domain.api.business

interface RefreshBusinessInfo {
    suspend operator fun invoke()
}