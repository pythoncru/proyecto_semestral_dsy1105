package cl.duoc.bitacoraescolar.data.database

import androidx.room.TypeConverter
import cl.duoc.bitacoraescolar.data.model.EstadoBitacora
import cl.duoc.bitacoraescolar.data.model.EstadoRegistro
import cl.duoc.bitacoraescolar.data.model.Rol
import cl.duoc.bitacoraescolar.data.model.TipoRecordatorio

class Converters {
    @TypeConverter
    fun fromRol(rol: Rol): String = rol.name

    @TypeConverter
    fun toRol(value: String): Rol = enumValueOf(value)

    @TypeConverter
    fun fromEstadoBitacora(estado: EstadoBitacora): String = estado.name

    @TypeConverter
    fun toEstadoBitacora(value: String): EstadoBitacora = enumValueOf(value)

    @TypeConverter
    fun fromEstadoRegistro(estado: EstadoRegistro): String = estado.name

    @TypeConverter
    fun toEstadoRegistro(value: String): EstadoRegistro = enumValueOf(value)

    @TypeConverter
    fun fromTipoRecordatorio(tipo: TipoRecordatorio): String = tipo.name

    @TypeConverter
    fun toTipoRecordatorio(value: String): TipoRecordatorio = enumValueOf(value)
}
