package me.bookk.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import me.bookk.database.entity.LegalDocumentStatusEntity

@Dao
abstract class LegalDocumentStatusDao {
    @Query("SELECT * FROM legal_document_status ORDER BY document")
    abstract fun observeAll(): Flow<List<LegalDocumentStatusEntity>>

    @Upsert
    abstract suspend fun upsertAll(statuses: List<LegalDocumentStatusEntity>)

    @Query("DELETE FROM legal_document_status")
    abstract suspend fun clear()

    @Transaction
    open suspend fun replaceAll(statuses: List<LegalDocumentStatusEntity>) {
        clear()
        upsertAll(statuses)
    }
}
