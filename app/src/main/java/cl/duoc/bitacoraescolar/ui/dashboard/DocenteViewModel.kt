package cl.duoc.bitacoraescolar.ui.dashboard

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.bitacoraescolar.data.model.AsignaturaEntity
import cl.duoc.bitacoraescolar.data.model.BitacoraEntity
import cl.duoc.bitacoraescolar.data.model.CursoEntity
import cl.duoc.bitacoraescolar.data.model.EstadoBitacora
import cl.duoc.bitacoraescolar.data.model.EstadoRegistro
import cl.duoc.bitacoraescolar.data.model.RecordatorioEntity
import cl.duoc.bitacoraescolar.data.model.RegistroEntity
import cl.duoc.bitacoraescolar.data.repository.BitacoraRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DocenteViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = BitacoraRepository(application)

    val cursos: StateFlow<List<CursoEntity>> = repository.getCursos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val asignaturas: StateFlow<List<AsignaturaEntity>> = repository.getAsignaturas()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recordatorios: StateFlow<List<RecordatorioEntity>> = repository.getRecordatorios()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun getBitacorasByDocente(docenteId: Long): StateFlow<List<BitacoraEntity>> {
        return repository.getBitacorasByDocente(docenteId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    }

    fun getRegistrosByBitacora(bitacoraId: Long): StateFlow<List<RegistroEntity>> {
        return repository.getRegistrosByBitacora(bitacoraId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    }

    fun getEstudiantesByCurso(cursoId: Long) = repository.getEstudiantesByCurso(cursoId)

    fun crearBitacora(
        titulo: String,
        proposito: String,
        instrucciones: String,
        preguntasOrientadoras: String,
        mes: Int,
        anio: Int,
        cursoId: Long,
        asignaturaId: Long,
        docenteId: Long,
        fechaBitacora: Long,
        onComplete: (Boolean, String?) -> Unit
    ) {
        if (titulo.isBlank() || proposito.isBlank() || instrucciones.isBlank()) {
            onComplete(false, "Todos los campos obligatorios deben ser completados")
            return
        }

        viewModelScope.launch {
            try {
                val nuevaBitacora = BitacoraEntity(
                    titulo = titulo,
                    proposito = proposito,
                    instrucciones = instrucciones,
                    preguntasOrientadoras = preguntasOrientadoras,
                    mes = mes,
                    anio = anio,
                    cursoId = cursoId,
                    asignaturaId = asignaturaId,
                    docenteId = docenteId,
                    estado = EstadoBitacora.BORRADOR,
                    fechaCreacion = fechaBitacora
                )
                repository.crearBitacora(nuevaBitacora)
                onComplete(true, null)
            } catch (e: Exception) {
                onComplete(false, e.message ?: "Error al crear la bitácora")
            }
        }
    }

    fun cambiarEstadoBitacora(bitacoraId: Long, estado: EstadoBitacora) {
        viewModelScope.launch {
            val fechaCierre = if (estado == EstadoBitacora.CERRADA) System.currentTimeMillis() else null
            repository.cambiarEstadoBitacora(bitacoraId, estado, fechaCierre)
        }
    }

    fun retroalimentarRegistro(registroId: Long, retroalimentacion: String) {
        viewModelScope.launch {
            val registro = repository.getRegistroById(registroId)
            if (registro != null) {
                val actualizado = registro.copy(
                    retroalimentacion = retroalimentacion,
                    estado = EstadoRegistro.REVISADO,
                    fechaRevision = System.currentTimeMillis()
                )
                repository.guardarRegistro(actualizado)
            }
        }
    }

    fun marcarRecordatorioCompletado(id: Long, completado: Boolean) {
        viewModelScope.launch {
            repository.marcarRecordatorioCompletado(id, completado)
        }
    }
}
