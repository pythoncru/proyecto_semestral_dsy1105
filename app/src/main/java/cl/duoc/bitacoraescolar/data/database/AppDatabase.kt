package cl.duoc.bitacoraescolar.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import cl.duoc.bitacoraescolar.data.dao.BitacoraDao
import cl.duoc.bitacoraescolar.data.dao.CursoDao
import cl.duoc.bitacoraescolar.data.dao.RecordatorioDao
import cl.duoc.bitacoraescolar.data.dao.RegistroDao
import cl.duoc.bitacoraescolar.data.dao.UsuarioDao
import cl.duoc.bitacoraescolar.data.model.AsignaturaEntity
import cl.duoc.bitacoraescolar.data.model.BitacoraEntity
import cl.duoc.bitacoraescolar.data.model.CursoEntity
import cl.duoc.bitacoraescolar.data.model.EstadoBitacora
import cl.duoc.bitacoraescolar.data.model.EvidenciaEntity
import cl.duoc.bitacoraescolar.data.model.RecordatorioEntity
import cl.duoc.bitacoraescolar.data.model.RegistroEntity
import cl.duoc.bitacoraescolar.data.model.Rol
import cl.duoc.bitacoraescolar.data.model.TipoRecordatorio
import cl.duoc.bitacoraescolar.data.model.UsuarioEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UsuarioEntity::class,
        CursoEntity::class,
        AsignaturaEntity::class,
        BitacoraEntity::class,
        RegistroEntity::class,
        EvidenciaEntity::class,
        RecordatorioEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun usuarioDao(): UsuarioDao
    abstract fun cursoDao(): CursoDao
    abstract fun bitacoraDao(): BitacoraDao
    abstract fun registroDao(): RegistroDao
    abstract fun recordatorioDao(): RecordatorioDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "bitacora_escolar_db"
                )
                    .addCallback(DatabaseCallback())
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback : Callback() {
            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                // Restore the original published sample for existing installations too.
                // Never replace a teacher's existing bitacora or student submissions.
                db.execSQL("""
                    INSERT INTO bitacoras
                    (titulo, proposito, instrucciones, preguntasOrientadoras, mes, anio,
                     cursoId, asignaturaId, docenteId, estado, fechaCreacion, fechaCierre)
                    SELECT 'Bitácora de Lenguaje - Octubre',
                           'Registrar avances en lectura comprensiva y expresión escrita.',
                           'Responda las preguntas al finalizar cada clase.',
                           '¿Qué aprendiste hoy? ¿Qué dudas te quedaron?',
                           10, 2026, 1, 1, 1, 'PUBLICADA',
                           CAST(strftime('%s','now') AS INTEGER) * 1000, NULL
                    WHERE EXISTS (SELECT 1 FROM cursos WHERE id = 1)
                      AND EXISTS (SELECT 1 FROM asignaturas WHERE id = 1)
                      AND EXISTS (SELECT 1 FROM usuarios WHERE id = 1)
                      AND NOT EXISTS (
                          SELECT 1 FROM bitacoras
                          WHERE titulo = 'Bitácora de Lenguaje - Octubre'
                            AND cursoId = 1 AND anio = 2026 AND mes = 10
                      )
                """.trimIndent())
            }
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateDatabase(database)
                    }
                }
            }
        }

        private suspend fun populateDatabase(db: AppDatabase) {
            // 1. Cursos
            val curso1 = CursoEntity(id = 1, nombre = "5° Básico A", nivel = "5° Básico")
            val curso2 = CursoEntity(id = 2, nombre = "6° Básico A", nivel = "6° Básico")
            val curso3 = CursoEntity(id = 3, nombre = "7° Básico A", nivel = "7° Básico")
            val curso4 = CursoEntity(id = 4, nombre = "8° Básico A", nivel = "8° Básico")
            db.cursoDao().insertCursos(listOf(curso1, curso2, curso3, curso4))

            // 2. Asignaturas
            val asig1 = AsignaturaEntity(id = 1, nombre = "Lenguaje y Comunicación")
            val asig2 = AsignaturaEntity(id = 2, nombre = "Matemáticas")
            val asig3 = AsignaturaEntity(id = 3, nombre = "Ciencias Naturales")
            val asig4 = AsignaturaEntity(id = 4, nombre = "Historia y Geografía")
            db.cursoDao().insertAsignaturas(listOf(asig1, asig2, asig3, asig4))

            // 3. Usuarios de prueba (4 cuentas obligatorias)
            val docente1 = UsuarioEntity(
                id = 1,
                email = "docente1@duoc.cl",
                passwordHash = "123456",
                nombreCompleto = "Prof. Carlos Mendoza",
                rol = Rol.DOCENTE
            )
            val docente2 = UsuarioEntity(
                id = 2,
                email = "docente2@duoc.cl",
                passwordHash = "123456",
                nombreCompleto = "Prof. María Silva",
                rol = Rol.DOCENTE
            )
            val estudiante1 = UsuarioEntity(
                id = 3,
                email = "estudiante1@duoc.cl",
                passwordHash = "123456",
                nombreCompleto = "Juan Pérez",
                rol = Rol.ESTUDIANTE,
                cursoId = 1
            )
            val estudiante2 = UsuarioEntity(
                id = 4,
                email = "estudiante2@duoc.cl",
                passwordHash = "123456",
                nombreCompleto = "Ana Gómez",
                rol = Rol.ESTUDIANTE,
                cursoId = 1
            )
            db.usuarioDao().insertUsuarios(listOf(docente1, docente2, estudiante1, estudiante2))

            // 4. Recordatorios iniciales
            val rec1 = RecordatorioEntity(
                id = 1,
                titulo = "Cierre Mensual de Bitácoras",
                mensaje = "Recuerde revisar y cerrar las bitácoras correspondientes al presente mes.",
                tipo = TipoRecordatorio.CIERRE_MENSUAL,
                fechaProgramada = System.currentTimeMillis() + 864000000L
            )
            val rec2 = RecordatorioEntity(
                id = 2,
                titulo = "Creación de Siguiente Bitácora",
                mensaje = "Prepare las preguntas orientadoras para la bitácora del próximo mes.",
                tipo = TipoRecordatorio.CREACION_NEXT_BITACORA,
                fechaProgramada = System.currentTimeMillis() + 1728000000L
            )
            db.recordatorioDao().insertRecordatorios(listOf(rec1, rec2))

            // 5. Bitácora de muestra publicada
            val bitacoraMuestra = BitacoraEntity(
                id = 1,
                titulo = "Bitácora de Lenguaje - Octubre",
                proposito = "Registrar avances en lectura comprensiva y expresión escrita.",
                instrucciones = "Responda las preguntas al finalizar cada clase.",
                preguntasOrientadoras = "¿Qué aprendiste hoy? ¿Qué dudas te quedaron?",
                mes = 10,
                anio = 2026,
                cursoId = 1,
                asignaturaId = 1,
                docenteId = 1,
                estado = EstadoBitacora.PUBLICADA
            )
            db.bitacoraDao().insertBitacora(bitacoraMuestra)
        }
    }
}
