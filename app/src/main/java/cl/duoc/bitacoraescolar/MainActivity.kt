package cl.duoc.bitacoraescolar

import android.os.Bundle
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cl.duoc.bitacoraescolar.data.auth.UserSession
import cl.duoc.bitacoraescolar.data.model.Rol
import cl.duoc.bitacoraescolar.ui.auth.LoginScreen
import cl.duoc.bitacoraescolar.ui.dashboard.DocenteDashboardScreen
import cl.duoc.bitacoraescolar.ui.dashboard.EstudianteDashboardScreen
import cl.duoc.bitacoraescolar.ui.theme.BitacoraEscolarTheme

class MainActivity : ComponentActivity() {
    private var darkMode by mutableStateOf(false)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        darkMode = getSharedPreferences("appearance", MODE_PRIVATE).getBoolean("dark_mode", false)
        enableEdgeToEdge()
        setContent {
            BitacoraEscolarTheme(darkTheme = darkMode) {
                MainAppNav(darkMode = darkMode, onToggleTheme = {
                    darkMode = !darkMode
                    getSharedPreferences("appearance", MODE_PRIVATE).edit().putBoolean("dark_mode", darkMode).apply()
                })
            }
        }
    }
}

@Composable
fun MainAppNav(darkMode: Boolean, onToggleTheme: () -> Unit) {
    val currentUser by UserSession.currentUser.collectAsState()

    when (val user = currentUser) {
        null -> {
            LoginScreen(
                darkMode = darkMode,
                onToggleTheme = onToggleTheme,
                onLoginSuccess = { _ ->
                    // El estado se actualiza en el ViewModel/UserSession
                }
            )
        }
        else -> {
            when (user.rol) {
                Rol.DOCENTE -> DocenteDashboardScreen(onLogout = {}, darkMode = darkMode, onToggleTheme = onToggleTheme)
                Rol.ESTUDIANTE -> EstudianteDashboardScreen(onLogout = {}, darkMode = darkMode, onToggleTheme = onToggleTheme)
            }
        }
    }
}
