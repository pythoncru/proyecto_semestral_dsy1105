package cl.duoc.bitacoraescolar.data.model

enum class Rol {
    DOCENTE,
    ESTUDIANTE
}

enum class EstadoBitacora {
    BORRADOR,
    PUBLICADA,
    CERRADA
}

enum class EstadoRegistro {
    BORRADOR,
    ENVIADO,
    REVISADO
}

enum class TipoRecordatorio {
    CIERRE_MENSUAL,
    CREACION_NEXT_BITACORA,
    INICIO_SEMESTRE
}
