package cl.duoc.bitacoraescolar.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recordatorios")
data class RecordatorioEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val titulo: String,
    val mensaje: String,
    val tipo: TipoRecordatorio,
    val fechaProgramada: Long,
    val completado: Boolean = false,
    val usuarioId: Long? = null
)
