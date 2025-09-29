package me.bookk.feature.business.domain.api

import me.bookk.feature.business.domain.api.entity.Business

interface UpdateBusiness {
    suspend operator fun invoke(model:Business.Update)
}