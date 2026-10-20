package cl.duoc.bitacoraescolar.data.auth

import cl.duoc.bitacoraescolar.data.model.UsuarioEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object UserSession {
    private val _currentUser = MutableStateFlow<UsuarioEntity?>(null)
    val currentUser: StateFlow<UsuarioEntity?> = _currentUser.asStateFlow()

    fun login(usuario: UsuarioEntity) {
        _currentUser.value = usuario
    }

    fun logout() {
        _currentUser.value = null
    }

    val isLoggedIn: Boolean
        get() = _currentUser.value != null
}
