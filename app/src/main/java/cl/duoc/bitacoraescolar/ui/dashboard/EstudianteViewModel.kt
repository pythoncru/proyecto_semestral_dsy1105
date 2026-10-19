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
        onComplete: (Boolean, String?) -> Unit
    ) {
        if (contenido.isBlank()) {
            onComplete(false, "El contenido del registro no puede estar vacío")
            return
        }

        viewModelScope.launch {
            try {
                val existente = repository.getRegistroByBitacoraYEstudiante(bitacoraId, estudianteId)
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
}
