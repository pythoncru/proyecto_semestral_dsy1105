package cl.duoc.bitacoraescolar.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import cl.duoc.bitacoraescolar.data.model.BitacoraEntity
import cl.duoc.bitacoraescolar.data.model.EstadoBitacora
import kotlinx.coroutines.flow.Flow

@Dao
interface BitacoraDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBitacora(bitacora: BitacoraEntity): Long

    @Update
    suspend fun updateBitacora(bitacora: BitacoraEntity)

    @Query("SELECT * FROM bitacoras WHERE id = :id")
    suspend fun getBitacoraById(id: Long): BitacoraEntity?

    @Query("SELECT * FROM bitacoras WHERE docenteId = :docenteId ORDER BY fechaCreacion DESC")
    fun getBitacorasByDocente(docenteId: Long): Flow<List<BitacoraEntity>>

    @Query("SELECT * FROM bitacoras WHERE cursoId = :cursoId AND estado = 'PUBLICADA' ORDER BY fechaCreacion DESC")
    fun getBitacorasPublicadasPorCurso(cursoId: Long): Flow<List<BitacoraEntity>>

    @Query("SELECT * FROM bitacoras ORDER BY fechaCreacion DESC")
    fun getAllBitacoras(): Flow<List<BitacoraEntity>>

    @Query("UPDATE bitacoras SET estado = :estado, fechaCierre = :fechaCierre WHERE id = :bitacoraId")
    suspend fun cambiarEstadoBitacora(bitacoraId: Long, estado: EstadoBitacora, fechaCierre: Long? = null)
}
