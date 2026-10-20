package cl.duoc.bitacoraescolar.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import cl.duoc.bitacoraescolar.data.model.EvidenciaEntity
import cl.duoc.bitacoraescolar.data.model.RegistroEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RegistroDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRegistro(registro: RegistroEntity): Long

    @Update
    suspend fun updateRegistro(registro: RegistroEntity)

    @Query("SELECT * FROM registros WHERE id = :id")
    suspend fun getRegistroById(id: Long): RegistroEntity?

    @Query("SELECT * FROM registros WHERE bitacoraId = :bitacoraId ORDER BY fechaCreacion ASC")
    fun getRegistrosByBitacora(bitacoraId: Long): Flow<List<RegistroEntity>>

    @Query("SELECT * FROM registros WHERE estudianteId = :estudianteId ORDER BY fechaCreacion DESC")
    fun getRegistrosByEstudiante(estudianteId: Long): Flow<List<RegistroEntity>>

    @Query("SELECT * FROM registros WHERE bitacoraId = :bitacoraId AND estudianteId = :estudianteId LIMIT 1")
    suspend fun getRegistroByBitacoraYEstudiante(bitacoraId: Long, estudianteId: Long): RegistroEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvidencia(evidencia: EvidenciaEntity): Long

    @Query("SELECT * FROM evidencias WHERE registroId = :registroId")
    fun getEvidenciasByRegistro(registroId: Long): Flow<List<EvidenciaEntity>>
}
