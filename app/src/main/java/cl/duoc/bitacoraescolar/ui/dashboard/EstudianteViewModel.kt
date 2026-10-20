package cl.duoc.bitacoraescolar.ui.dashboard

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.bitacoraescolar.data.model.BitacoraEntity
import cl.duoc.bitacoraescolar.data.model.EstadoRegistro
import cl.duoc.bitacoraescolar.data.model.EvidenciaEntity
import cl.duoc.bitacoraescolar.data.model.RecordatorioEntity
import cl.duoc.bitacoraescolar.data.model.RegistroEntity
import cl.duoc.bitacoraescolar.data.repository.BitacoraRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class EstudianteViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = BitacoraRepository(application)

    val recordatorios: StateFlow<List<RecordatorioEntity>> = repository.getRecordatorios()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun getBitacorasPublicadasPorCurso(cursoId: Long): StateFlow<List<BitacoraEntity>> {
        return repository.getBitacorasPublicadasPorCurso(cursoId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    }

    fun getRegistrosByEstudiante(estudianteId: Long): StateFlow<List<RegistroEntity>> {
        return repository.getRegistrosByEstudiante(estudianteId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    }

    suspend fun getRegistroByBitacoraYEstudiante(bitacoraId: Long, estudianteId: Long): RegistroEntity? {
        return repository.getRegistroByBitacoraYEstudiante(bitacoraId, estudianteId)
    }

    fun guardarRegistro(
        bitacoraId: Long,
        estudianteId: Long,
        contenido: String,
        esEnvio: Boolean,
        permitirEdicion: Boolean = false,
        onComplete: (Boolean, String?) -> Unit
    ) {
        if (contenido.isBlank()) {
            onComplete(false, "El contenido del registro no puede estar vacío")
            return
        }

        viewModelScope.launch {
            try {
                val existente = repository.getRegistroByBitacoraYEstudiante(bitacoraId, estudianteId)
                if (existente != null && existente.estado != EstadoRegistro.BORRADOR && (!permitirEdicion || !esEnvio)) {
                    onComplete(false, "Registro ya enviado. Selecciona Editar bitácora.")
                    return@launch
                }
                val nuevoEstado = if (esEnvio) EstadoRegistro.ENVIADO else EstadoRegistro.BORRADOR

                val registro = if (existente != null) {
                    existente.copy(
                        contenido = contenido,
                        estado = nuevoEstado
                    )
                } else {
                    RegistroEntity(
                        bitacoraId = bitacoraId,
                        estudianteId = estudianteId,
                        contenido = contenido,
                        estado = nuevoEstado
                    )
                }

                repository.guardarRegistro(registro)
                onComplete(true, null)
            } catch (e: Exception) {
                onComplete(false, e.message ?: "Error al guardar el registro")
            }
        }
    }

    fun agregarEvidencia(registroId: Long, fileUri: String, nombreArchivo: String, tipo: String = "IMAGEN", onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val evidencia = EvidenciaEntity(
                    registroId = registroId,
                    fileUri = fileUri,
                    nombreArchivo = nombreArchivo,
                    tipo = tipo
                )
                repository.agregarEvidencia(evidencia)
                onComplete(true)
            } catch (_: Exception) {
                onComplete(false)
            }
        }
    }

    fun getEvidenciasByRegistro(registroId: Long) = repository.getEvidenciasByRegistro(registroId)
    suspend fun obtenerDatosBitacora(bitacoraId: Long): Pair<String, String> {
        val bitacora = repository.getBitacoraById(bitacoraId)
            ?: return "Bitácora #$bitacoraId" to "Desconocido"
        val docente = repository.getUsuarioById(bitacora.docenteId)
        return bitacora.titulo to (docente?.nombreCompleto ?: "Desconocido")
    }

    fun obtenerRegistroParaEvidencia(bitacoraId: Long, estudianteId: Long, onComplete: (RegistroEntity?) -> Unit) {
        viewModelScope.launch { onComplete(repository.getRegistroByBitacoraYEstudiante(bitacoraId, estudianteId)) }
    }

    fun guardarImagenEvidencia(
        bitacoraId: Long, estudianteId: Long, contenido: String, fileUri: String,
        permitirEdicion: Boolean = false,
        onComplete: (Boolean, String?) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val actual = repository.getRegistroByBitacoraYEstudiante(bitacoraId, estudianteId)
                if (actual != null && actual.estado != EstadoRegistro.BORRADOR && !permitirEdicion) {
                    onComplete(false, "Selecciona Editar bitácora antes de adjuntar imágenes.")
                    return@launch
                }
                val id = if (actual != null) actual.id else repository.guardarRegistro(
                    RegistroEntity(bitacoraId = bitacoraId, estudianteId = estudianteId, contenido = contenido)
                )
                // Comprobar el límite en la base de datos, no solo en pantalla.
                val cantidad = repository.contarEvidencias(id)
                if (cantidad >= 5) {
                    onComplete(false, "Solo se permiten 5 imágenes por bitácora.")
                } else {
                    repository.agregarEvidencia(EvidenciaEntity(
                        registroId = id, fileUri = fileUri,
                        nombreArchivo = "Foto ${cantidad + 1}", tipo = "FOTO"
                    ))
                    onComplete(true, null)
                }
            } catch (e: Exception) { onComplete(false, e.message) }
        }
    }

    fun eliminarImagenEvidencia(evidenciaId: Long, onComplete: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            try {
                val eliminada = repository.eliminarEvidencia(evidenciaId)
                onComplete(eliminada, if (eliminada) null else "No se encontró la imagen.")
            } catch (e: Exception) {
                onComplete(false, e.message ?: "No se pudo eliminar la imagen.")
            }
        }
    }

}
