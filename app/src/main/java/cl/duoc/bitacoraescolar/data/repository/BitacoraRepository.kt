package cl.duoc.bitacoraescolar.data.repository

import android.content.Context
import cl.duoc.bitacoraescolar.data.database.AppDatabase
import cl.duoc.bitacoraescolar.data.model.BitacoraEntity
import cl.duoc.bitacoraescolar.data.model.EstadoBitacora
import cl.duoc.bitacoraescolar.data.model.EvidenciaEntity
import cl.duoc.bitacoraescolar.data.model.RecordatorioEntity
import cl.duoc.bitacoraescolar.data.model.RegistroEntity
import cl.duoc.bitacoraescolar.data.model.UsuarioEntity
import kotlinx.coroutines.flow.Flow

class BitacoraRepository(context: Context) {
    private val db = AppDatabase.getDatabase(context)
    private val usuarioDao = db.usuarioDao()
    private val cursoDao = db.cursoDao()
    private val bitacoraDao = db.bitacoraDao()
    private val registroDao = db.registroDao()
    private val recordatorioDao = db.recordatorioDao()

    suspend fun login(email: String, password: String): Result<UsuarioEntity> {
        val user = usuarioDao.getUsuarioByEmail(email.trim())
        return if (user != null && user.passwordHash == password) {
            Result.success(user)
        } else {
            Result.failure(Exception("Correo o contraseña incorrectos"))
        }
    }

    suspend fun getUsuarioById(id: Long): UsuarioEntity? {
        return usuarioDao.getUsuarioById(id)
    }

    fun getEstudiantesByCurso(cursoId: Long) = usuarioDao.getEstudiantesByCurso(cursoId)

    fun getCursos() = cursoDao.getAllCursos()
    fun getAsignaturas() = cursoDao.getAllAsignaturas()
    suspend fun getCursoById(id: Long) = cursoDao.getCursoById(id)
    suspend fun getAsignaturaById(id: Long) = cursoDao.getAsignaturaById(id)

    fun getBitacorasByDocente(docenteId: Long) = bitacoraDao.getBitacorasByDocente(docenteId)
    fun getBitacorasPublicadasPorCurso(cursoId: Long) = bitacoraDao.getBitacorasPublicadasPorCurso(cursoId)
    fun getAllBitacoras() = bitacoraDao.getAllBitacoras()
    suspend fun getBitacoraById(id: Long) = bitacoraDao.getBitacoraById(id)

    suspend fun crearBitacora(bitacora: BitacoraEntity): Long {
        return bitacoraDao.insertBitacora(bitacora)
    }

    suspend fun actualizarBitacora(bitacora: BitacoraEntity) {
        bitacoraDao.updateBitacora(bitacora)
    }

    suspend fun cambiarEstadoBitacora(bitacoraId: Long, estado: EstadoBitacora, fechaCierre: Long? = null) {
        bitacoraDao.cambiarEstadoBitacora(bitacoraId, estado, fechaCierre)
    }

    fun getRegistrosByBitacora(bitacoraId: Long) = registroDao.getRegistrosByBitacora(bitacoraId)
    fun getRegistrosByEstudiante(estudianteId: Long) = registroDao.getRegistrosByEstudiante(estudianteId)
    suspend fun getRegistroById(id: Long) = registroDao.getRegistroById(id)
    suspend fun getRegistroByBitacoraYEstudiante(bitacoraId: Long, estudianteId: Long) =
        registroDao.getRegistroByBitacoraYEstudiante(bitacoraId, estudianteId)

    suspend fun guardarRegistro(registro: RegistroEntity): Long {
        return if (registro.id == 0L) {
            registroDao.insertRegistro(registro)
        } else {
            registroDao.updateRegistro(registro)
            registro.id
        }
    }

    suspend fun contarEvidencias(registroId: Long) = registroDao.contarEvidencias(registroId)

    suspend fun agregarEvidencia(evidencia: EvidenciaEntity): Long {
        return registroDao.insertEvidencia(evidencia)
    }

    suspend fun eliminarEvidencia(evidenciaId: Long): Boolean =
        registroDao.eliminarEvidencia(evidenciaId) > 0

    fun getEvidenciasByRegistro(registroId: Long) = registroDao.getEvidenciasByRegistro(registroId)

    fun getRecordatorios(): Flow<List<RecordatorioEntity>> = recordatorioDao.getAllRecordatorios()
    suspend fun marcarRecordatorioCompletado(id: Long, completado: Boolean) {
        recordatorioDao.setCompletado(id, completado)
    }
}
