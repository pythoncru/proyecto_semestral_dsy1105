package cl.duoc.bitacoraescolar.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "asignaturas")
data class AsignaturaEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nombre: String
)
