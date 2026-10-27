package me.bookk.database.relation

import androidx.room.Embedded
import androidx.room.Relation
import me.bookk.database.entity.ServiceEntity
import me.bookk.database.entity.ServiceGroupEntity


class ServiceLocal(
    @Embedded
    val entity: ServiceEntity,
    @Relation(
        parentColumn = "groupId",
        entityColumn = "id"
    )
    val group: ServiceGroupEntity
)