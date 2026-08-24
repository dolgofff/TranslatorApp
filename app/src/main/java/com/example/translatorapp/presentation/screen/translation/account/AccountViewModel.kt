package com.example.translatorapp.presentation.screen.translation.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.translatorapp.domain.usecase.authorization.GetCurrentUserUseCase
import com.example.translatorapp.domain.usecase.authorization.LogOutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val logOutUseCase: LogOutUseCase,
) : ViewModel() {
    private val _userState = MutableStateFlow(UserInfo())
    val userState = _userState.asStateFlow()

    init {
        getUserInfo()
    }

    private fun getUserInfo() {
        viewModelScope.launch {
            val result = getCurrentUserUseCase()

            if (result != null) {
                _userState.update {
                    it.copy(
                        photoUrl = result.photoUrl ?: "",
                        name = result.displayName ?: "Unknown user",
                        email = result.email ?: ""
                    )
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            logOutUseCase()
        }
    }

    data class UserInfo(
        val photoUrl: String = "",
        val name: String = "",
        val email: String = "",
    )
}