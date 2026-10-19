package cl.duoc.bitacoraescolar.ui.bitacora

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearBitacoraScreen(
    docenteId: Long,
    viewModel: CrearBitacoraViewModel,
    onVolver: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(state.guardadoExitoso) {
        if (state.guardadoExitoso) {
            onVolver()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nueva Bitácora Mensual") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = state.titulo,
                onValueChange = { viewModel.onTituloChanged(it) },
                label = { Text("Título de la bitácora") },
                isError = state.tituloError != null,
                supportingText = {
                    state.tituloError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                },
                trailingIcon = {
                    if (state.tituloError != null) {
                        Icon(
                            Icons.Default.Warning,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = state.proposito,
                onValueChange = { viewModel.onPropositoChanged(it) },
                label = { Text("Propósito general") },
                isError = state.propositoError != null,
                supportingText = {
                    state.propositoError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )

            OutlinedTextField(
                value = state.instrucciones,
                onValueChange = { viewModel.onInstruccionesChanged(it) },
                label = { Text("Instrucciones generales") },
                isError = state.instruccionesError != null,
                supportingText = {
                    state.instruccionesError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )

            OutlinedTextField(
                value = state.preguntasOrientadoras,
                onValueChange = { viewModel.onPreguntasChanged(it) },
                label = { Text("Preguntas orientadoras") },
                isError = state.preguntasError != null,
                supportingText = {
                    state.preguntasError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = { viewModel.guardarBitacora(docenteId, publicar = false) },
                    enabled = state.esValido,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Guardar Borrador")
                }

                Button(
                    onClick = { viewModel.guardarBitacora(docenteId, publicar = true) },
                    enabled = state.esValido,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Publicar")
                }
            }
        }
    }
}