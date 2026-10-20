package cl.duoc.bitacoraescolar.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.bitacoraescolar.R
import cl.duoc.bitacoraescolar.data.model.Rol

private val Azul = Color(0xFF287DCE)
private val Verde = Color(0xFF4BAA75)

@Composable
fun LoginScreen(
    onLoginSuccess: (Rol) -> Unit,
    darkMode: Boolean = false,
    onToggleTheme: () -> Unit = {},
    viewModel: LoginViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var mostrarPassword by remember { mutableStateOf(false) }
    LaunchedEffect(uiState.loggedInUser) {
        uiState.loggedInUser?.let { onLoginSuccess(it.rol) }
    }
    val cuentas = listOf(
        Triple("Docente 1", "docente1@duoc.cl", Azul),
        Triple("Docente 2", "docente2@duoc.cl", Verde),
        Triple("Estudiante 1", "estudiante1@duoc.cl", Azul),
        Triple("Estudiante 2", "estudiante2@duoc.cl", Verde)
    )
    Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                IconButton(onClick = onToggleTheme) {
                    Text(if (darkMode) "☀" else "☾", fontSize = 27.sp, color = MaterialTheme.colorScheme.primary)
                }
            }
            Icon(
                painter = painterResource(R.drawable.ic_bitacora_logo),
                contentDescription = "Logo Bitácora",
                tint = Color.Unspecified,
                modifier = Modifier.size(100.dp)
            )
            Spacer(Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Bita", fontSize = 35.sp, fontWeight = FontWeight.Bold, color = Verde)
                Text("cora", fontSize = 35.sp, fontWeight = FontWeight.Bold, color = Azul)
            }
            Spacer(Modifier.height(28.dp))
            OutlinedTextField(
                value = uiState.emailInput,
                onValueChange = viewModel::onEmailChanged,
                label = { Text("Usuario") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                isError = uiState.emailError != null,
                supportingText = uiState.emailError?.let { error -> { Text(error) } },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = uiState.passwordInput,
                onValueChange = viewModel::onPasswordChanged,
                label = { Text("Contraseña") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                trailingIcon = {
                    IconButton(onClick = { mostrarPassword = !mostrarPassword }) {
                        Text(if (mostrarPassword) "◉" else "◎", fontSize = 23.sp)
                    }
                },
                visualTransformation = if (mostrarPassword) VisualTransformation.None else PasswordVisualTransformation(),
                isError = uiState.passwordError != null,
                supportingText = uiState.passwordError?.let { error -> { Text(error) } },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            )
            uiState.errorMessage?.let {
                Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp))
            }
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = viewModel::login,
                enabled = !uiState.isLoading,
                colors = ButtonDefaults.buttonColors(containerColor = Azul),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth().height(54.dp)
            ) {
                if (uiState.isLoading) CircularProgressIndicator(Modifier.size(22.dp), color = Color.White)
                else Text("Iniciar sesión", fontSize = 17.sp)
            }
            Spacer(Modifier.height(28.dp))
            Text("O ingresa como", color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(16.dp))
            cuentas.chunked(2).forEach { fila ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    fila.forEach { (nombre, correo, color) ->
                        val elegido = uiState.emailInput == correo
                        Card(
                            modifier = Modifier.weight(1f).clickable { viewModel.setQuickUser(correo) },
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = if (elegido) color.copy(alpha = .22f) else color.copy(alpha = .10f)),
                            border = if (elegido) androidx.compose.foundation.BorderStroke(2.dp, color) else null
                        ) {
                            Column(
                                Modifier.fillMaxWidth().padding(vertical = 20.dp, horizontal = 6.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(Icons.Default.AccountCircle, contentDescription = null, tint = color, modifier = Modifier.size(44.dp))
                                Spacer(Modifier.height(8.dp))
                                Text(nombre, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurface)
                            }
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))
            }
        }
    }
}
