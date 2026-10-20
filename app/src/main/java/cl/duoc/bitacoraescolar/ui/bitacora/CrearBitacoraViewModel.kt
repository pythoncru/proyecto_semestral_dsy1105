package cl.duoc.bitacoraescolar.ui.bitacora

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.bitacoraescolar.data.model.BitacoraEntity
import cl.duoc.bitacoraescolar.data.model.EstadoBitacora
import cl.duoc.bitacoraescolar.data.repository.BitacoraRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CrearBitacoraUiState(
    val titulo: String = "",
    val proposito: String = "",
    val instrucciones: String = "",
    val preguntasOrientadoras: String = "",
    val mes: String = "10",
    val anio: String = "2026",
    val cursoId: Long = 1L,
    val asignaturaId: Long = 1L,
    val tituloError: String? = null,
    val propositoError: String? = null,
    val instruccionesError: String? = null,
    val preguntasError: String? = null,
    val esValido: Boolean = false,
    val guardadoExitoso: Boolean = false
)

class CrearBitacoraViewModel(
    private val repository: BitacoraRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CrearBitacoraUiState())
    val uiState: StateFlow<CrearBitacoraUiState> = _uiState.asStateFlow()

    fun onTituloChanged(valor: String) {
        _uiState.update { it.copy(
            titulo = valor,
            tituloError = if (valor.isBlank()) "El título es obligatorio" else null
        ) }
        validarFormulario()
    }

    fun onPropositoChanged(valor: String) {
        _uiState.update { it.copy(
            proposito = valor,
            propositoError = if (valor.isBlank()) "El propósito es obligatorio" else null
        ) }
        validarFormulario()
    }

    fun onInstruccionesChanged(valor: String) {
        _uiState.update { it.copy(
            instrucciones = valor,
            instruccionesError = if (valor.isBlank()) "Las instrucciones son obligatorias" else null
        ) }
        validarFormulario()
    }

    fun onPreguntasChanged(valor: String) {
        _uiState.update { it.copy(
            preguntasOrientadoras = valor,
            preguntasError = if (valor.isBlank()) "Debe ingresar al menos una pregunta" else null
        ) }
        validarFormulario()
    }

    private fun validarFormulario() {
        val s = _uiState.value
        val esValido = s.titulo.isNotBlank() &&
                s.proposito.isNotBlank() &&
                s.instrucciones.isNotBlank() &&
                s.preguntasOrientadoras.isNotBlank()
        _uiState.update { it.copy(esValido = esValido) }
    }

    fun guardarBitacora(docenteId: Long, publicar: Boolean) {
        if (!_uiState.value.esValido) return

        viewModelScope.launch {
            val estado = if (publicar) EstadoBitacora.PUBLICADA else EstadoBitacora.BORRADOR
            val nuevaBitacora = BitacoraEntity(
                id = 0L,
                titulo = _uiState.value.titulo,
                proposito = _uiState.value.proposito,
                instrucciones = _uiState.value.instrucciones,
                preguntasOrientadoras = _uiState.value.preguntasOrientadoras,
                mes = _uiState.value.mes.toIntOrNull() ?: 10,
                anio = _uiState.value.anio.toIntOrNull() ?: 2026,
                cursoId = _uiState.value.cursoId,
                asignaturaId = _uiState.value.asignaturaId,
                docenteId = docenteId,
                estado = estado
            )
            repository.crearBitacora(nuevaBitacora)
            _uiState.update { it.copy(guardadoExitoso = true) }
        }
    }
}