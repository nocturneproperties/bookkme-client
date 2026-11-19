package me.bookk.database.relation

import androidx.room3.Embedded
import androidx.room3.Relation
import me.bookk.database.entity.ServiceEntity
import me.bookk.database.entity.ServiceGroupEntity

class ServiceLocal(
    @Embedded
    val entity: ServiceEntity,
    @Relation(
        parentColumns = ["groupId"],
        entityColumns = ["id"],
    )
    val group: ServiceGroupEntity,
)
