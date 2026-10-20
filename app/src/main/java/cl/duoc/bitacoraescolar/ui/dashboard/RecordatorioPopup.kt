package cl.duoc.bitacoraescolar.ui.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cl.duoc.bitacoraescolar.data.model.RecordatorioEntity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** Aviso breve y legible, compartido por los paneles docente y estudiante. */
@Composable
fun RecordatorioPopup(usuarioId: Long, recordatorios: List<RecordatorioEntity>) {
    var visible by remember(usuarioId) { mutableStateOf(true) }
    if (!visible) return

    val pendientes = recordatorios.filter {
        !it.completado && (it.usuarioId == null || it.usuarioId == usuarioId)
    }.sortedBy { it.fechaProgramada }
    val ahora = System.currentTimeMillis()
    val fecha = remember { SimpleDateFormat("dd MMM", Locale("es", "CL")) }
    val scope = rememberCoroutineScope()
    var cerrando by remember { mutableStateOf(false) }
    val escala by animateFloatAsState(
        targetValue = if (cerrando) 0.90f else 1f,
        animationSpec = tween(190),
        label = "confirmarAvisoEscala"
    )
    val opacidad by animateFloatAsState(
        targetValue = if (cerrando) 0.75f else 1f,
        animationSpec = tween(190),
        label = "confirmarAvisoOpacidad"
    )

    AlertDialog(
        onDismissRequest = { if (!cerrando) visible = false },
        shape = RoundedCornerShape(26.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp,
        title = {
            Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Text("Tus recordatorios", style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Text(
                    if (pendientes.isEmpty()) "Todo al día"
                    else "${pendientes.size} ${if (pendientes.size == 1) "aviso pendiente" else "avisos pendientes"}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth().heightIn(max = 320.dp).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (pendientes.isEmpty()) {
                    Text("No tienes tareas pendientes por ahora.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyLarge)
                } else {
                    pendientes.take(3).forEach { rec ->
                        val vencido = rec.fechaProgramada <= ahora
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(5.dp)) {
                                Text(
                                    if (vencido) "VENCIDO · ${fecha.format(Date(rec.fechaProgramada))}"
                                    else "PRÓXIMO · ${fecha.format(Date(rec.fechaProgramada))}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (vencido) MaterialTheme.colorScheme.error
                                            else MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(rec.titulo, style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 2, overflow = TextOverflow.Ellipsis)
                                if (rec.mensaje.isNotBlank()) {
                                    Text(rec.mensaje, style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 2, overflow = TextOverflow.Ellipsis)
                                }
                            }
                        }
                    }
                    if (pendientes.size > 3) {
                        Text("Y ${pendientes.size - 3} más pendientes.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (!cerrando) {
                        cerrando = true
                        scope.launch {
                            delay(210)
                            visible = false
                        }
                    }
                },
                enabled = !cerrando,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp)
                    .interactiveFeedback()
                    .graphicsLayer { scaleX = escala; scaleY = escala; alpha = opacidad },
                shape = RoundedCornerShape(14.dp),
                contentPadding = PaddingValues(vertical = 13.dp)
            ) {
                Text("Entendido", style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold)
            }
        }
    )
}
