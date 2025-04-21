package me.bookk.feature.business.domain.api

import me.bookk.feature.business.domain.api.entity.Business

interface RefreshBusinessInfo {
    suspend operator fun invoke(): Business?
}