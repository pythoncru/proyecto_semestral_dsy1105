package cl.duoc.bitacoraescolar.ui.dashboard

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import cl.duoc.bitacoraescolar.data.model.RecordatorioEntity
import java.text.DateFormat
import java.util.Date

/** Muestra el resumen de recordatorios una vez por entrada al panel del usuario. */
@Composable
fun RecordatorioPopup(usuarioId: Long, recordatorios: List<RecordatorioEntity>) {
    var visible by remember(usuarioId) { mutableStateOf(true) }
    if (!visible) return

    val pendientes = recordatorios.filter { rec ->
        !rec.completado && (rec.usuarioId == null || rec.usuarioId == usuarioId)
    }.sortedBy { it.fechaProgramada }
    val ahora = System.currentTimeMillis()
    val mensaje = if (pendientes.isEmpty()) {
        "No tienes recordatorios pendientes."
    } else {
        pendientes.joinToString("\n\n") { rec ->
            val estado = if (rec.fechaProgramada <= ahora) "Vencido" else "Próximo"
            "$estado: ${rec.titulo}\n${rec.mensaje}\nFecha: ${DateFormat.getDateInstance(DateFormat.MEDIUM).format(Date(rec.fechaProgramada))}"
        }
    }

    AlertDialog(
        onDismissRequest = { visible = false },
        title = { Text("Recordatorios") },
        text = { Text(mensaje) },
        confirmButton = {
            TextButton(onClick = { visible = false }) { Text("Entendido") }
        }
    )
}
