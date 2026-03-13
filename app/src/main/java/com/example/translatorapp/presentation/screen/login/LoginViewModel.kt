package com.example.translatorapp.presentation.screen.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.translatorapp.domain.error.AuthError
import com.example.translatorapp.domain.usecase.authorization.ResetPasswordUseCase
import com.example.translatorapp.domain.usecase.authorization.SignInEmailUseCase
import com.example.translatorapp.domain.usecase.authorization.SignInGoogleUseCase
import com.example.translatorapp.presentation.mapper.toUiMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val signInEmailUseCase: SignInEmailUseCase,
    private val signInGoogleUseCase: SignInGoogleUseCase,
    private val resetPasswordUseCase: ResetPasswordUseCase,
) : ViewModel() {
    private val _loginState = MutableStateFlow(LoginState())
    val loginState = _loginState.asStateFlow()

    fun togglePasswordVisibility() {
        _loginState.update { it.copy(visibility = !_loginState.value.visibility) }
    }

    fun updateEmailText(input: String) {
        _loginState.update { it.copy(email = input) }
    }

    fun updatePasswordText(input: String) {
        _loginState.update { it.copy(password = input) }
    }

    fun updateResetPasswordEvent() {
        _loginState.update { it.copy(resetPasswordEmailSent = false) }
    }

    fun signInEmail(onSuccess: () -> Unit) {
        viewModelScope.launch {
            val result = signInEmailUseCase(
                email = _loginState.value.email,
                password = _loginState.value.password
            )

            result.onSuccess { _ ->
                _loginState.update { it.copy(errorMessage = null) }
                onSuccess()
            }
                .onFailure { error ->
                    val authError = error as? AuthError ?: AuthError.Unknown(error.message)

                    _loginState.update {
                        LoginState(
                            email = _loginState.value.email,
                            errorMessage = authError.toUiMessage()
                        )
                    }
                }
        }
    }

    fun signInWithGoogle(idToken: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val result = signInGoogleUseCase(idToken = idToken)

            result.onSuccess { _ ->
                _loginState.update { it.copy(errorMessage = null) }
                onSuccess()
            }
                .onFailure { error ->
                    val authError = error as? AuthError ?: AuthError.Unknown(error.message)

                    _loginState.update { LoginState(errorMessage = authError.toUiMessage()) }
                }
        }
    }

    fun resetPassword() {
        viewModelScope.launch {
            val result = resetPasswordUseCase(email = _loginState.value.email)

            result.onSuccess { _ ->
                _loginState.update { it.copy(errorMessage = null, resetPasswordEmailSent = true) }
            }
                .onFailure { error ->
                    val authError = error as? AuthError ?: AuthError.Unknown(error.message)

                    _loginState.update {
                        LoginState(
                            errorMessage = authError.toUiMessage(),
                            resetPasswordEmailSent = false
                        )
                    }
                }
        }
    }

    data class LoginState(
        val email: String = "",
        val password: String = "",
        val visibility: Boolean = false,
        val errorMessage: String? = null,
        val resetPasswordEmailSent: Boolean = false,
    )
}