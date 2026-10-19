package cl.duoc.bitacoraescolar.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usuarios")
data class UsuarioEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val email: String,
    val passwordHash: String,
    val nombreCompleto: String,
    val rol: Rol,
    val cursoId: Long? = null
)
