package cl.duoc.bitacoraescolar.ui.dashboard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.bitacoraescolar.data.auth.UserSession
import cl.duoc.bitacoraescolar.data.model.AsignaturaEntity
import cl.duoc.bitacoraescolar.data.model.BitacoraEntity
import cl.duoc.bitacoraescolar.data.model.CursoEntity
import cl.duoc.bitacoraescolar.data.model.EstadoBitacora
import cl.duoc.bitacoraescolar.data.model.EstadoRegistro
import cl.duoc.bitacoraescolar.data.model.RecordatorioEntity
import cl.duoc.bitacoraescolar.data.model.RegistroEntity
import cl.duoc.bitacoraescolar.data.model.UsuarioEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocenteDashboardScreen(
    onLogout: () -> Unit,
    viewModel: DocenteViewModel = viewModel()
) {
    val currentUser by UserSession.currentUser.collectAsState()
    val docenteId = currentUser?.id ?: 1L

    val bitacoras by viewModel.getBitacorasByDocente(docenteId).collectAsState(initial = emptyList())
    val cursos by viewModel.cursos.collectAsState()
    val asignaturas by viewModel.asignaturas.collectAsState()
    val recordatorios by viewModel.recordatorios.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) }
    var showCreateDialog by remember { mutableStateOf(false) }
    var selectedBitacoraForDetail by remember { mutableStateOf<BitacoraEntity?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(selectedBitacoraForDetail?.let { "Detalle Bitácora" } ?: "Panel Docente") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                navigationIcon = {
                    if (selectedBitacoraForDetail != null) {
                        IconButton(onClick = { selectedBitacoraForDetail = null }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                        }
                    }
                },
                actions = {
                    IconButton(onClick = {
                        UserSession.logout()
                        onLogout()
                    }) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Cerrar Sesión")
                    }
                }
            )
        },
        floatingActionButton = {
            if (selectedBitacoraForDetail == null && selectedTab == 0) {
                FloatingActionButton(onClick = { showCreateDialog = true }) {
                    Icon(Icons.Default.Add, contentDescription = "Crear Bitácora")
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (selectedBitacoraForDetail != null) {
                DocenteBitacoraDetailView(
                    bitacora = selectedBitacoraForDetail!!,
                    viewModel = viewModel,
                    onBack = { selectedBitacoraForDetail = null }
                )
            } else {
                ScrollableTabRow(selectedTabIndex = selectedTab) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("Mis Bitácoras (${bitacoras.size})") }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Recordatorios (${recordatorios.size})") }
                    )
                }

                when (selectedTab) {
                    0 -> DocenteBitacorasList(
                        bitacoras = bitacoras,
                        onBitacoraClick = { selectedBitacoraForDetail = it },
                        onCambiarEstado = { bitacora, nuevoEstado ->
                            viewModel.cambiarEstadoBitacora(bitacora.id, nuevoEstado)
                        }
                    )
                    1 -> DocenteRecordatoriosList(
                        recordatorios = recordatorios,
                        onToggleCompletado = { id, comp ->
                            viewModel.marcarRecordatorioCompletado(id, comp)
                        }
                    )
                }
            }
        }
    }

    if (showCreateDialog) {
        CrearBitacoraDialog(
            cursos = cursos,
            asignaturas = asignaturas,
            docenteId = docenteId,
            viewModel = viewModel,
            onDismiss = { showCreateDialog = false }
        )
    }
}

@Composable
fun DocenteBitacorasList(
    bitacoras: List<BitacoraEntity>,
    onBitacoraClick: (BitacoraEntity) -> Unit,
    onCambiarEstado: (BitacoraEntity, EstadoBitacora) -> Unit
) {
    if (bitacoras.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No tienes bitácoras creadas. Presiona '+' para crear una.")
        }
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(bitacoras) { bitacora ->
                Card(
                    modifier = Modifier.fillMaxWidth().clickable { onBitacoraClick(bitacora) },
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = bitacora.titulo, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text(
                                text = bitacora.estado.name,
                                style = MaterialTheme.typography.labelMedium,
                                color = when (bitacora.estado) {
                                    EstadoBitacora.PUBLICADA -> MaterialTheme.colorScheme.primary
                                    EstadoBitacora.CERRADA -> MaterialTheme.colorScheme.error
                                    else -> MaterialTheme.colorScheme.secondary
                                }
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = bitacora.proposito, style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                            if (bitacora.estado == EstadoBitacora.BORRADOR) {
                                TextButton(onClick = { onCambiarEstado(bitacora, EstadoBitacora.PUBLICADA) }) {
                                    Icon(Icons.Default.Send, contentDescription = null)
                                    Spacer(modifier = Modifier.padding(2.dp))
                                    Text("Publicar")
                                }
                            } else if (bitacora.estado == EstadoBitacora.PUBLICADA) {
                                TextButton(onClick = { onCambiarEstado(bitacora, EstadoBitacora.CERRADA) }) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null)
                                    Spacer(modifier = Modifier.padding(2.dp))
                                    Text("Cerrar")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DocenteBitacoraDetailView(
    bitacora: BitacoraEntity,
    viewModel: DocenteViewModel,
    onBack: () -> Unit
) {
    val registros by viewModel.getRegistrosByBitacora(bitacora.id).collectAsState(initial = emptyList())
    var selectedRegistroForReview by remember { mutableStateOf<RegistroEntity?>(null) }
    var retroText by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = bitacora.titulo, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Propósito: ${bitacora.proposito}", style = MaterialTheme.typography.bodyMedium)
                Text(text = "Instrucciones: ${bitacora.instrucciones}", style = MaterialTheme.typography.bodySmall)
                Text(text = "Preguntas: ${bitacora.preguntasOrientadoras}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                Text(text = "Estado: ${bitacora.estado}", style = MaterialTheme.typography.labelMedium)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Aportes de Estudiantes (${registros.size})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))

        if (registros.isEmpty()) {
            Text("Aún no hay registros enviados por los estudiantes para esta bitácora.")
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(registros) { registro ->
                    Card(
                        modifier = Modifier.fillMaxWidth().clickable {
                            selectedRegistroForReview = registro
                            retroText = registro.retroalimentacion ?: ""
                        }
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(text = "Estudiante ID: ${registro.estudianteId}", fontWeight = FontWeight.Bold)
                            Text(text = "Contenido: ${registro.contenido}", style = MaterialTheme.typography.bodyMedium)
                            Text(text = "Estado: ${registro.estado}", style = MaterialTheme.typography.labelSmall)
                            if (!registro.retroalimentacion.isNullOrBlank()) {
                                Text(text = "Feedback: ${registro.retroalimentacion}", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }
        }
    }

    if (selectedRegistroForReview != null) {
        AlertDialog(
            onDismissRequest = { selectedRegistroForReview = null },
            title = { Text("Revisar y Retroalimentar") },
            text = {
                Column {
                    Text("Aporte del Estudiante:")
                    Text(selectedRegistroForReview!!.contenido, fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 4.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = retroText,
                        onValueChange = { retroText = it },
                        label = { Text("Comentarios / Retroalimentación") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    viewModel.retroalimentarRegistro(selectedRegistroForReview!!.id, retroText)
                    selectedRegistroForReview = null
                }) {
                    Text("Guardar y Enviar Feedback")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedRegistroForReview = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearBitacoraDialog(
    cursos: List<CursoEntity>,
    asignaturas: List<AsignaturaEntity>,
    docenteId: Long,
    viewModel: DocenteViewModel,
    onDismiss: () -> Unit
) {
    var titulo by remember { mutableStateOf("") }
    var proposito by remember { mutableStateOf("") }
    var instrucciones by remember { mutableStateOf("") }
    var preguntas by remember { mutableStateOf("") }
    var cursoSelectedId by remember { mutableLongStateOf(cursos.firstOrNull()?.id ?: 1L) }
    var asignaturaSelectedId by remember { mutableLongStateOf(asignaturas.firstOrNull()?.id ?: 1L) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Crear Nueva Bitácora") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = titulo, onValueChange = { titulo = it }, label = { Text("Título *") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = proposito, onValueChange = { proposito = it }, label = { Text("Propósito *") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = instrucciones, onValueChange = { instrucciones = it }, label = { Text("Instrucciones *") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = preguntas, onValueChange = { preguntas = it }, label = { Text("Preguntas Orientadoras") }, modifier = Modifier.fillMaxWidth())

                Text("Curso: ${cursos.find { it.id == cursoSelectedId }?.nombre ?: ""}", style = MaterialTheme.typography.bodySmall)
                Text("Asignatura: ${asignaturas.find { it.id == asignaturaSelectedId }?.nombre ?: ""}", style = MaterialTheme.typography.bodySmall)

                errorMessage?.let {
                    Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                viewModel.crearBitacora(
                    titulo = titulo,
                    proposito = proposito,
                    instrucciones = instrucciones,
                    preguntasOrientadoras = preguntas,
                    mes = 10,
                    anio = 2026,
                    cursoId = cursoSelectedId,
                    asignaturaId = asignaturaSelectedId,
                    docenteId = docenteId,
                    onComplete = { success, error ->
                        if (success) {
                            onDismiss()
                        } else {
                            errorMessage = error
                        }
                    }
                )
            }) {
                Text("Crear")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun DocenteRecordatoriosList(
    recordatorios: List<RecordatorioEntity>,
    onToggleCompletado: (Long, Boolean) -> Unit
) {
    if (recordatorios.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No hay recordatorios institucionales.")
        }
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(recordatorios) { rec ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = rec.titulo, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = rec.mensaje, style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "Tipo: ${rec.tipo}", style = MaterialTheme.typography.labelSmall)
                            TextButton(onClick = { onToggleCompletado(rec.id, !rec.completado) }) {
                                Text(if (rec.completado) "Marcar pendiente" else "Marcar completado")
                            }
                        }
                    }
                }
            }
        }
    }
}
