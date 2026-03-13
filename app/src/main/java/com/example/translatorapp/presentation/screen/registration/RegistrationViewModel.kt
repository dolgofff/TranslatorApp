package com.example.translatorapp.presentation.screen.registration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.translatorapp.domain.error.AuthError
import com.example.translatorapp.domain.usecase.authorization.RegistrationUseCase
import com.example.translatorapp.presentation.mapper.toUiMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class RegistrationViewModel @Inject constructor(private val registrationUseCase: RegistrationUseCase) :
    ViewModel() {
    private val _registrationState = MutableStateFlow(RegistrationState())
    val registrationState = _registrationState.asStateFlow()

    fun togglePasswordVisibility1() {
        _registrationState.update { it.copy(visibility1 = !_registrationState.value.visibility1) }
    }

    fun togglePasswordVisibility2() {
        _registrationState.update { it.copy(visibility2 = !_registrationState.value.visibility2) }
    }

    fun updateEmailText(input: String) {
        _registrationState.update { it.copy(email = input) }
    }

    fun updatePasswordText(input: String) {
        _registrationState.update { it.copy(password = input) }
    }

    fun updateConfirmPasswordText(input: String) {
        _registrationState.update { it.copy(confirmPassword = input) }
    }

    fun register(onSuccess: () -> Unit) {
        viewModelScope.launch {
            val result = registrationUseCase(
                email = _registrationState.value.email,
                password = _registrationState.value.password,
                confirmPassword = _registrationState.value.confirmPassword
            )

            result.onSuccess { _ ->
                _registrationState.update { it.copy(errorMessage = null) }
                onSuccess()
            }
                .onFailure { error ->
                    val authError = error as? AuthError ?: AuthError.Unknown(error.message)

                    _registrationState.update {
                        RegistrationState(
                            email = _registrationState.value.email,
                            errorMessage = authError.toUiMessage()
                        )
                    }
                }
        }
    }

    data class RegistrationState(
        val email: String = "",
        val password: String = "",
        val confirmPassword: String = "",
        val visibility1: Boolean = false,
        val visibility2: Boolean = false,
        val errorMessage: String? = null,
    )
}