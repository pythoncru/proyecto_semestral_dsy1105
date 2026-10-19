package cl.duoc.bitacoraescolar

import android.os.Bundle
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BitacoraEscolarTheme {
                MainAppNav()
            }
        }
    }
}

@Composable
fun MainAppNav() {
    val currentUser by UserSession.currentUser.collectAsState()

    when (val user = currentUser) {
        null -> {
            LoginScreen(
                onLoginSuccess = { _ ->
                    // El estado se actualiza en el ViewModel/UserSession
                }
            )
        }
        else -> {
            when (user.rol) {
                Rol.DOCENTE -> DocenteDashboardScreen(onLogout = {})
                Rol.ESTUDIANTE -> EstudianteDashboardScreen(onLogout = {})
            }
        }
    }
}
