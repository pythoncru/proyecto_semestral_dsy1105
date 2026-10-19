package cl.duoc.bitacoraescolar.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import cl.duoc.bitacoraescolar.data.model.AsignaturaEntity
import cl.duoc.bitacoraescolar.data.model.CursoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CursoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCursos(cursos: List<CursoEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAsignaturas(asignaturas: List<AsignaturaEntity>)

    @Query("SELECT * FROM cursos")
    fun getAllCursos(): Flow<List<CursoEntity>>

    @Query("SELECT * FROM asignaturas")
    fun getAllAsignaturas(): Flow<List<AsignaturaEntity>>

    @Query("SELECT * FROM cursos WHERE id = :id LIMIT 1")
    suspend fun getCursoById(id: Long): CursoEntity?

    @Query("SELECT * FROM asignaturas WHERE id = :id LIMIT 1")
    suspend fun getAsignaturaById(id: Long): AsignaturaEntity?
}
