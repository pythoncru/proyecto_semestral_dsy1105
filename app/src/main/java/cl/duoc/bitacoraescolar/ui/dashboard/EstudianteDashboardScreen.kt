package cl.duoc.bitacoraescolar.ui.dashboard

import android.net.Uri
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import java.io.File
import java.io.FileOutputStream
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.bitacoraescolar.data.auth.UserSession
import cl.duoc.bitacoraescolar.data.model.BitacoraEntity
import cl.duoc.bitacoraescolar.data.model.EvidenciaEntity
import cl.duoc.bitacoraescolar.data.model.RecordatorioEntity
import cl.duoc.bitacoraescolar.data.model.RegistroEntity
import androidx.activity.result.PickVisualMediaRequest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EstudianteDashboardScreen(
    onLogout: () -> Unit,
    darkMode: Boolean = false,
    onToggleTheme: () -> Unit = {},
    viewModel: EstudianteViewModel = viewModel()
) {
    val currentUser by UserSession.currentUser.collectAsState()
    val estudianteId = currentUser?.id ?: 3L
    val cursoId = currentUser?.cursoId ?: 1L

    val bitacorasFlow = remember(viewModel, cursoId) {
        viewModel.getBitacorasPublicadasPorCurso(cursoId)
    }
    val bitacorasPublicadas by bitacorasFlow.collectAsState()
    val registrosFlow = remember(viewModel, estudianteId) {
        viewModel.getRegistrosByEstudiante(estudianteId)
    }
    val misRegistros by registrosFlow.collectAsState()
    val recordatorios by viewModel.recordatorios.collectAsState()
    val borradores = misRegistros.filter { it.estado.name == "BORRADOR" }
    val enviados = misRegistros.filter { it.estado.name != "BORRADOR" }

    RecordatorioPopup(usuarioId = estudianteId, recordatorios = recordatorios)

    var selectedTab by remember { mutableIntStateOf(0) }
    var selectedBitacoraForResponder by remember { mutableStateOf<BitacoraEntity?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    val filteredBitacoras = bitacorasPublicadas.filter {
        it.titulo.contains(searchQuery, ignoreCase = true) || it.proposito.contains(searchQuery, ignoreCase = true)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(selectedBitacoraForResponder?.let { "Responder Bitácora" } ?: "Panel Estudiante") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onTertiaryContainer
                ),
                navigationIcon = {
                    if (selectedBitacoraForResponder != null) {
                        IconButton(modifier = Modifier.interactiveFeedback(), onClick = { selectedBitacoraForResponder = null }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                        }
                    }
                },
                actions = {
                    IconButton(modifier = Modifier.interactiveFeedback(), onClick = onToggleTheme) {
                        Text(if (darkMode) "☀" else "☾", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.onSurface)
                    }
                    IconButton(modifier = Modifier.interactiveFeedback(), onClick = {
                        UserSession.logout()
                        onLogout()
                    }) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Cerrar Sesión")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (selectedBitacoraForResponder != null) {
                EstudianteResponderBitacoraView(
                    bitacora = selectedBitacoraForResponder!!,
                    estudianteId = estudianteId,
                    viewModel = viewModel,
                    onBack = { selectedBitacoraForResponder = null }
                )
            } else {
                ScrollableTabRow(selectedTabIndex = selectedTab) {
                    Tab(modifier = Modifier.interactiveFeedback(), 
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("Bitácoras Activas (${filteredBitacoras.size})") }
                    )
                    Tab(modifier = Modifier.interactiveFeedback(), 
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Mis Registros (${enviados.size})") }
                    )
                    Tab(modifier = Modifier.interactiveFeedback(), 
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = { Text("Recordatorios (${recordatorios.size})") }
                    )
                    Tab(modifier = Modifier.interactiveFeedback(), 
                        selected = selectedTab == 3,
                        onClick = { selectedTab = 3 },
                        text = { Text("Borradores (${borradores.size})") }
                    )
                }

                when (selectedTab) {
                    0 -> {
                        Column(modifier = Modifier.fillMaxSize()) {
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                label = { Text("Buscar bitácoras...") },
                                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth().padding(16.dp)
                            )
                            EstudianteBitacorasList(
                                bitacoras = filteredBitacoras,
                                onBitacoraClick = { selectedBitacoraForResponder = it }
                            )
                        }
                    }
                    1 -> EstudianteRegistrosList(registros = enviados, viewModel = viewModel)
                    2 -> EstudianteRecordatoriosList(recordatorios = recordatorios)
                    3 -> EstudianteBorradoresList(
                        borradores = borradores,
                        onEditar = { borrador ->
                            selectedBitacoraForResponder = bitacorasPublicadas.firstOrNull { it.id == borrador.bitacoraId }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun EstudianteBitacorasList(
    bitacoras: List<BitacoraEntity>,
    onBitacoraClick: (BitacoraEntity) -> Unit
) {
    if (bitacoras.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No hay bitácoras publicadas que coincidan con la búsqueda.")
        }
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(bitacoras) { bitacora ->
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 })
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth().interactiveFeedback().clickable { onBitacoraClick(bitacora) },
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = bitacora.titulo, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = "Propósito: ${bitacora.proposito}", style = MaterialTheme.typography.bodyMedium)
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(text = "Mes: ${bitacora.mes}/${bitacora.anio}", style = MaterialTheme.typography.labelSmall)
                                Text("Toca para responder →", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelMedium)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EstudianteResponderBitacoraView(
    bitacora: BitacoraEntity,
    estudianteId: Long,
    viewModel: EstudianteViewModel,
    onBack: () -> Unit
) {
    var contenidoText by remember { mutableStateOf("") }
    var mensajeEstado by remember { mutableStateOf<String?>(null) }
    var existingRegistro by remember { mutableStateOf<RegistroEntity?>(null) }
    var modoEdicion by remember(bitacora.id, estudianteId) { mutableStateOf(false) }
    val yaEnviado = existingRegistro?.estado?.name?.let { it != "BORRADOR" } ?: false
    val puedeEditar = !yaEnviado || modoEdicion
    var evidencias by remember { mutableStateOf<List<EvidenciaEntity>>(emptyList()) }

    LaunchedEffect(bitacora.id, estudianteId) {
        val reg = viewModel.getRegistroByBitacoraYEstudiante(bitacora.id, estudianteId)
        if (reg != null) {
            existingRegistro = reg
            contenidoText = reg.contenido
        }
    }

    LaunchedEffect(existingRegistro?.id) {
        existingRegistro?.id?.let { regId ->
            viewModel.getEvidenciasByRegistro(regId).collect { evs ->
                evidencias = evs
            }
        }
    }

    val context = LocalContext.current
    var mostrarOpcionesFoto by remember { mutableStateOf(false) }
    var adjuntando by remember { mutableStateOf(false) }
    var eliminandoId by remember { mutableStateOf<Long?>(null) }
    var imagenParaEliminar by remember { mutableStateOf<EvidenciaEntity?>(null) }

    if (imagenParaEliminar != null) {
        val seleccionada = imagenParaEliminar!!
        AlertDialog(
            shape = RoundedCornerShape(24.dp),
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            onDismissRequest = { imagenParaEliminar = null },
            title = { Text("Eliminar evidencia") },
            text = { Text("¿Quieres eliminar esta imagen de la bitácora?") },
            confirmButton = {
                TextButton(modifier = Modifier.interactiveFeedback(), 
                    enabled = eliminandoId == null,
                    onClick = {
                        eliminandoId = seleccionada.id
                        viewModel.eliminarImagenEvidencia(seleccionada.id) { ok, error ->
                            eliminandoId = null
                            imagenParaEliminar = null
                            if (ok) {
                                val ruta = Uri.parse(seleccionada.fileUri).path
                                val dir = File(context.filesDir, "evidencias")
                                if (ruta != null) {
                                    val archivo = File(ruta)
                                    if (archivo.parentFile?.canonicalPath == dir.canonicalPath) {
                                        archivo.delete()
                                    }
                                }
                                mensajeEstado = "Imagen eliminada."
                            } else {
                                mensajeEstado = error ?: "No se pudo eliminar la imagen."
                            }
                        }
                    }
                ) { Text("Eliminar") }
            },
            dismissButton = {
                TextButton(modifier = Modifier.interactiveFeedback(), onClick = { imagenParaEliminar = null }) { Text("Cancelar") }
            }
        )
    }

    fun guardarImagenLocal(bitmap: Bitmap): Uri {
        val dir = File(context.filesDir, "evidencias").apply { mkdirs() }
        val archivo = File(dir, "foto_${System.currentTimeMillis()}_${java.util.UUID.randomUUID()}.jpg")
        FileOutputStream(archivo).use { bitmap.compress(Bitmap.CompressFormat.JPEG, 90, it) }
        return Uri.fromFile(archivo)
    }

    fun adjuntarImagen(uri: Uri) {
        if (!puedeEditar) { mensajeEstado = "Selecciona Editar bitácora para cambiar evidencias."; return }
        if (adjuntando || evidencias.size >= 5) {
            mensajeEstado = "Solo se permiten 5 imágenes por bitácora."
            return
        }
        adjuntando = true
        viewModel.guardarImagenEvidencia(
            bitacora.id, estudianteId, contenidoText, uri.toString(),
            permitirEdicion = yaEnviado && modoEdicion
        ) { success, error ->
            adjuntando = false
            if (success) {
                mensajeEstado = "Imagen adjuntada correctamente."
                // La consulta de Room actualiza automáticamente las miniaturas.
                viewModel.obtenerRegistroParaEvidencia(bitacora.id, estudianteId) { reg ->
                    existingRegistro = reg
                }
            } else {
                mensajeEstado = error ?: "No se pudo adjuntar la imagen."
            }
        }
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                // Copiar al almacenamiento interno evita perder permisos temporales del selector.
                val dir = File(context.filesDir, "evidencias").apply { mkdirs() }
                val archivo = File(dir, "foto_${System.currentTimeMillis()}_${java.util.UUID.randomUUID()}.jpg")
                context.contentResolver.openInputStream(uri)?.use { input ->
                    archivo.outputStream().use { output -> input.copyTo(output) }
                } ?: throw IllegalStateException("No se pudo leer la imagen")
                adjuntarImagen(Uri.fromFile(archivo))
            } catch (e: Exception) {
                mensajeEstado = "No se pudo importar la imagen: ${e.message}"
            }
        }
    }
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            try { adjuntarImagen(guardarImagenLocal(bitmap)) }
            catch (e: Exception) { mensajeEstado = "Error al guardar foto: ${e.message}" }
        }
    }

    if (mostrarOpcionesFoto) {
        AlertDialog(
            shape = RoundedCornerShape(24.dp),
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            onDismissRequest = { mostrarOpcionesFoto = false },
            title = { Text("Adjuntar evidencia") },
            text = {
                Column {
                    TextButton(modifier = Modifier.interactiveFeedback(), onClick = {
                        mostrarOpcionesFoto = false
                        photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    }) { Text("Elegir desde la galería") }
                    TextButton(modifier = Modifier.interactiveFeedback(), onClick = {
                        mostrarOpcionesFoto = false
                        cameraLauncher.launch(null)
                    }) { Text("Tomar una foto") }
                }
            },
            confirmButton = { TextButton(modifier = Modifier.interactiveFeedback(), onClick = { mostrarOpcionesFoto = false }) { Text("Cancelar") } }
        )
    }

    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = bitacora.titulo, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Propósito: ${bitacora.proposito}", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Instrucciones: ${bitacora.instrucciones}", style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Preguntas Orientadoras:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
                    Text(text = bitacora.preguntasOrientadoras, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = contenidoText,
                onValueChange = { contenidoText = it },
                readOnly = !puedeEditar,
                label = { Text("Tu Respuesta / Registro de Aprendizaje *") },
                modifier = Modifier.fillMaxWidth().height(160.dp)
            )
        }

        // Miniaturas inmediatamente debajo del campo de respuesta.
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Evidencias adjuntas (${evidencias.size}/5)", fontWeight = FontWeight.Bold)
                    if (evidencias.isEmpty()) Text("Todavía no has adjuntado fotografías.")
                    evidencias.forEach { ev ->
                        val imagen = remember(ev.fileUri) {
                            try {
                                val ruta = Uri.parse(ev.fileUri).path
                                ruta?.let { BitmapFactory.decodeFile(it)?.asImageBitmap() }
                            } catch (_: Exception) { null }
                        }
                        if (imagen != null) {
                            Image(
                                bitmap = imagen,
                                contentDescription = "Evidencia ${ev.nombreArchivo}",
                                contentScale = ContentScale.Fit,
                                modifier = Modifier.fillMaxWidth().height(180.dp)
                            )
                        } else Text("No se pudo mostrar ${ev.nombreArchivo}")
                        TextButton(
                            onClick = { imagenParaEliminar = ev },
                            enabled = puedeEditar && eliminandoId == null && !adjuntando,
                            modifier = Modifier.align(Alignment.End).interactiveFeedback()) {
                            Text("Eliminar imagen")
                        }
                    }
                    OutlinedButton(
                        onClick = { mostrarOpcionesFoto = true },
                        enabled = puedeEditar && evidencias.size < 5 && !adjuntando,
                        modifier = Modifier.fillMaxWidth().interactiveFeedback()) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Text(if (evidencias.size >= 5) "Máximo de 5 imágenes" else "Adjuntar evidencia")
                    }
                }
            }
        }

        if (existingRegistro?.retroalimentacion != null) {
            item {
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(text = "Retroalimentación del Docente:", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSecondaryContainer)
                        Text(text = existingRegistro!!.retroalimentacion!!, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }

        mensajeEstado?.let {
            item {
                Text(text = it, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.bodySmall)
            }
        }

        item {
            if (yaEnviado && !modoEdicion) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Este registro ya fue enviado. Solo puedes modificarlo mediante Editar bitácora.")
                    OutlinedButton(onClick = { modoEdicion = true }, modifier = Modifier.fillMaxWidth().interactiveFeedback()) {
                        Text("Editar bitácora")
                    }
                }
            } else {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    if (!yaEnviado) {
                        Button(onClick = {
                            viewModel.guardarRegistro(bitacora.id, estudianteId, contenidoText, esEnvio = false) { ok, err ->
                                mensajeEstado = if (ok) "Borrador guardado." else err
                                if (ok) viewModel.obtenerRegistroParaEvidencia(bitacora.id, estudianteId) { existingRegistro = it }
                            }
                        }, modifier = Modifier.weight(1f).interactiveFeedback()) { Text("Guardar borrador") }
                    } else {
                        OutlinedButton(onClick = { modoEdicion = false }, modifier = Modifier.weight(1f).interactiveFeedback()) {
                            Text("Cancelar edición")
                        }
                    }
                    Button(onClick = {
                        viewModel.guardarRegistro(bitacora.id, estudianteId, contenidoText,
                            esEnvio = true, permitirEdicion = yaEnviado && modoEdicion) { ok, err ->
                            mensajeEstado = if (ok) "Registro guardado correctamente." else err
                            if (ok) {
                                modoEdicion = false
                                viewModel.obtenerRegistroParaEvidencia(bitacora.id, estudianteId) { existingRegistro = it }
                            }
                        }
                    }, modifier = Modifier.weight(1f).interactiveFeedback()) {
                        Text(if (yaEnviado) "Guardar cambios" else "Enviar al docente")
                    }
                }
            }
        }
    }
}

@Composable
fun EstudianteRegistrosList(
    registros: List<RegistroEntity>,
    viewModel: EstudianteViewModel
) {
    if (registros.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Aún no tienes registros enviados. Revisa Borradores para ver los pendientes.")
        }
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(registros, key = { it.id }) { registro ->
                var titulo by remember(registro.bitacoraId) { mutableStateOf("Bitácora #${registro.bitacoraId}") }
                var docente by remember(registro.bitacoraId) { mutableStateOf("Cargando docente...") }
                var expandido by remember(registro.id) { mutableStateOf(false) }
                LaunchedEffect(registro.bitacoraId) {
                    val datos = viewModel.obtenerDatosBitacora(registro.bitacoraId)
                    titulo = datos.first
                    docente = datos.second
                }
                Card(modifier = Modifier.fillMaxWidth().interactiveFeedback().clickable { expandido = !expandido }) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(titulo, style = MaterialTheme.typography.titleMedium)
                        Text("Docente: $docente", style = MaterialTheme.typography.bodySmall)
                        Text("Fecha: " + java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale("es", "CL")).format(java.util.Date(registro.fechaCreacion)), style = MaterialTheme.typography.labelSmall)
                        Text("Estado: ${registro.estado}", style = MaterialTheme.typography.labelSmall)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(if (expandido) "Ocultar registro ▲" else "Ver registro ▼", color = MaterialTheme.colorScheme.primary)
                        if (expandido) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(registro.contenido)
                            if (!registro.retroalimentacion.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Retroalimentación del docente: ${registro.retroalimentacion}")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EstudianteBorradoresList(
    borradores: List<RegistroEntity>,
    onEditar: (RegistroEntity) -> Unit
) {
    if (borradores.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No tienes borradores guardados.")
        }
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(borradores, key = { it.id }) { borrador ->
                Card(modifier = Modifier.fillMaxWidth().interactiveFeedback().clickable { onEditar(borrador) }) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Borrador de bitácora #${borrador.bitacoraId}", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(borrador.contenido, style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Toca para continuar editando →", color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    }
}

@Composable
fun EstudianteRecordatoriosList(
    recordatorios: List<RecordatorioEntity>
) {
    if (recordatorios.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No hay recordatorios institucionales.")
        }
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(recordatorios) { rec ->
                AnimatedVisibility(visible = true, enter = fadeIn()) {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = rec.titulo, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = rec.mensaje, style = MaterialTheme.typography.bodyMedium)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "Tipo: ${rec.tipo}", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }
        }
    }
}
