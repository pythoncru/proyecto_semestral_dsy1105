package cl.duoc.bitacoraescolar.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "evidencias",
    foreignKeys = [
        ForeignKey(
            entity = RegistroEntity::class,
            parentColumns = ["id"],
            childColumns = ["registroId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("registroId")]
)
data class EvidenciaEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val registroId: Long,
    val fileUri: String,
    val nombreArchivo: String,
    val tipo: String = "IMAGEN"
)
