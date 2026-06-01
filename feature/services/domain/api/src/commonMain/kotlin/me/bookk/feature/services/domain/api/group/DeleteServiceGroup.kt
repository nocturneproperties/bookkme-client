package me.bookk.feature.services.domain.api.group

import me.bookk.feature.services.domain.api.group.entity.ServiceGroup

interface DeleteServiceGroup {
    suspend operator fun invoke(group: ServiceGroup)
}