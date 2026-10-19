package cl.duoc.bitacoraescolar.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "registros",
    foreignKeys = [
        ForeignKey(
            entity = BitacoraEntity::class,
            parentColumns = ["id"],
            childColumns = ["bitacoraId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = UsuarioEntity::class,
            parentColumns = ["id"],
            childColumns = ["estudianteId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("bitacoraId"), Index("estudianteId")]
)
data class RegistroEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val bitacoraId: Long,
    val estudianteId: Long,
    val contenido: String,
    val estado: EstadoRegistro = EstadoRegistro.BORRADOR,
    val retroalimentacion: String? = null,
    val fechaCreacion: Long = System.currentTimeMillis(),
    val fechaRevision: Long? = null
)
