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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import cl.duoc.bitacoraescolar.data.model.EstadoRegistro
import cl.duoc.bitacoraescolar.data.model.RecordatorioEntity
import cl.duoc.bitacoraescolar.data.model.RegistroEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EstudianteDashboardScreen(
    onLogout: () -> Unit,
    viewModel: EstudianteViewModel = viewModel()
) {
    val currentUser by UserSession.currentUser.collectAsState()
    val estudianteId = currentUser?.id ?: 3L
    val cursoId = currentUser?.cursoId ?: 1L

    val bitacorasPublicadas by viewModel.getBitacorasPublicadasPorCurso(cursoId).collectAsState(initial = emptyList())
    val misRegistros by viewModel.getRegistrosByEstudiante(estudianteId).collectAsState(initial = emptyList())
    val recordatorios by viewModel.recordatorios.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) }
    var selectedBitacoraForResponder by remember { mutableStateOf<BitacoraEntity?>(null) }

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
                        IconButton(onClick = { selectedBitacoraForResponder = null }) {
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
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("Bitácoras Activas (${bitacorasPublicadas.size})") }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Mis Registros (${misRegistros.size})") }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = { Text("Recordatorios (${recordatorios.size})") }
                    )
                }

                when (selectedTab) {
                    0 -> EstudianteBitacorasList(
                        bitacoras = bitacorasPublicadas,
                        onBitacoraClick = { selectedBitacoraForResponder = it }
                    )
                    1 -> EstudianteRegistrosList(
                        registros = misRegistros
                    )
                    2 -> EstudianteRecordatoriosList(
                        recordatorios = recordatorios
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
            Text("No hay bitácoras publicadas para tu curso actualmente.")
        }
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(bitacoras) { bitacora ->
                Card(
                    modifier = Modifier.fillMaxWidth().clickable { onBitacoraClick(bitacora) },
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

    LaunchedEffect(bitacora.id, estudianteId) {
        val reg = viewModel.getRegistroByBitacoraYEstudiante(bitacora.id, estudianteId)
        if (reg != null) {
            existingRegistro = reg
            contenidoText = reg.contenido
        }
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
                label = { Text("Tu Respuesta / Registro de Aprendizaje *") },
                modifier = Modifier.fillMaxWidth().height(180.dp)
            )
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
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = {
                        viewModel.guardarRegistro(bitacora.id, estudianteId, contenidoText, esEnvio = false) { success, err ->
                            mensajeEstado = if (success) "Borrador guardado exitosamente." else err
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Guardar Borrador")
                }

                Button(
                    onClick = {
                        viewModel.guardarRegistro(bitacora.id, estudianteId, contenidoText, esEnvio = true) { success, err ->
                            if (success) {
                                mensajeEstado = "¡Registro enviado correctamente al docente!"
                            } else {
                                mensajeEstado = err
                            }
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Enviar al Docente")
                }
            }
        }
    }
}

@Composable
fun EstudianteRegistrosList(
    registros: List<RegistroEntity>
) {
    if (registros.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Aún no tienes registros guardados o enviados.")
        }
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(registros) { registro ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "Bitácora ID: #${registro.bitacoraId}", style = MaterialTheme.typography.labelMedium)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = registro.contenido, style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "Estado: ${registro.estado}", style = MaterialTheme.typography.labelSmall)
                            if (!registro.retroalimentacion.isNullOrBlank()) {
                                Text(text = "Con Retroalimentación ✓", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelSmall)
                            }
                        }
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
