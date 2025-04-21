package me.bookk.feature.business.domain.api

import me.bookk.feature.business.domain.api.entity.Business

interface CreateBusiness {
    suspend operator fun invoke(name: String): Business
}