package cl.duoc.bitacoraescolar.ui.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.bitacoraescolar.data.auth.UserSession
import cl.duoc.bitacoraescolar.data.model.UsuarioEntity
import cl.duoc.bitacoraescolar.data.repository.BitacoraRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LoginUiState(
    val emailInput: String = "",
    val passwordInput: String = "",
    val emailError: String? = null,
    val passwordError: String? = null,
    val errorMessage: String? = null,
    val isLoading: Boolean = false,
    val loggedInUser: UsuarioEntity? = null
)

class LoginViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = BitacoraRepository(application)

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailChanged(email: String) {
        _uiState.value = _uiState.value.copy(emailInput = email, emailError = null, errorMessage = null)
    }

    fun onPasswordChanged(password: String) {
        _uiState.value = _uiState.value.copy(passwordInput = password, passwordError = null, errorMessage = null)
    }

    fun setQuickUser(email: String) {
        _uiState.value = _uiState.value.copy(
            emailInput = email,
            passwordInput = "123456",
            emailError = null,
            passwordError = null,
            errorMessage = null
        )
    }

    fun login() {
        val state = _uiState.value
        var hasError = false
        var emailErr: String? = null
        var passErr: String? = null

        if (state.emailInput.isBlank()) {
            emailErr = "El correo electrónico es obligatorio"
            hasError = true
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(state.emailInput).matches()) {
            emailErr = "Ingrese un correo electrónico válido"
            hasError = true
        }

        if (state.passwordInput.isBlank()) {
            passErr = "La contraseña es obligatoria"
            hasError = true
        }

        if (hasError) {
            _uiState.value = state.copy(emailError = emailErr, passwordError = passErr)
            return
        }

        _uiState.value = state.copy(isLoading = true, errorMessage = null)

        viewModelScope.launch {
            val result = repository.login(state.emailInput, state.passwordInput)
            result.onSuccess { user ->
                UserSession.login(user)
                _uiState.value = _uiState.value.copy(isLoading = false, loggedInUser = user)
            }.onFailure { ex ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = ex.message ?: "Error al iniciar sesión"
                )
            }
        }
    }
}
