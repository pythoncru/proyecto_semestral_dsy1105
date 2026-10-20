package cl.duoc.bitacoraescolar.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import cl.duoc.bitacoraescolar.data.model.RecordatorioEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecordatorioDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecordatorio(recordatorio: RecordatorioEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecordatorios(recordatorios: List<RecordatorioEntity>)

    @Query("SELECT * FROM recordatorios ORDER BY fechaProgramada ASC")
    fun getAllRecordatorios(): Flow<List<RecordatorioEntity>>

    @Query("UPDATE recordatorios SET completado = :completado WHERE id = :id")
    suspend fun setCompletado(id: Long, completado: Boolean)
}
