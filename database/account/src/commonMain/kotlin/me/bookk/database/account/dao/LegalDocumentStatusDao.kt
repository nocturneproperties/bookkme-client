package me.bookk.database.account.dao

import androidx.room3.Dao
import androidx.room3.Query
import androidx.room3.Transaction
import androidx.room3.Upsert
import kotlinx.coroutines.flow.Flow
import me.bookk.database.account.entity.LegalDocumentStatusEntity

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
