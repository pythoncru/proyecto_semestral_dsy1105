package cl.duoc.bitacoraescolar.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "bitacoras",
    foreignKeys = [
        ForeignKey(
            entity = CursoEntity::class,
            parentColumns = ["id"],
            childColumns = ["cursoId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = AsignaturaEntity::class,
            parentColumns = ["id"],
            childColumns = ["asignaturaId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = UsuarioEntity::class,
            parentColumns = ["id"],
            childColumns = ["docenteId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("cursoId"), Index("asignaturaId"), Index("docenteId")]
)
data class BitacoraEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val titulo: String,
    val proposito: String,
    val instrucciones: String,
    val preguntasOrientadoras: String,
    val mes: Int,
    val anio: Int,
    val cursoId: Long,
    val asignaturaId: Long,
    val docenteId: Long,
    val estado: EstadoBitacora = EstadoBitacora.BORRADOR,
    val fechaCreacion: Long = System.currentTimeMillis(),
    val fechaCierre: Long? = null
)
