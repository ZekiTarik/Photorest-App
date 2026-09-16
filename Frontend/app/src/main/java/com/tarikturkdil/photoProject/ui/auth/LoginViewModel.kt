package com.tarikturkdil.photoProject.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tarikturkdil.photoProject.domain.repository.AuthRepository
import com.tarikturkdil.photoProject.domain.repository.AppResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _formState = MutableStateFlow(LoginFormState())
    val formState: StateFlow<LoginFormState> = _formState.asStateFlow()

    private val _status = MutableStateFlow<LoginStatus>(LoginStatus.Idle)
    val status: StateFlow<LoginStatus> = _status.asStateFlow()

    fun onEmailChange(newEmail: String) {
        _formState.update { it.copy(email = newEmail) }
    }

    fun onPasswordChange(newPassword: String) {
        _formState.update { it.copy(password = newPassword) }
    }

    fun login() {
        val currentForm = _formState.value

        if (currentForm.email.isBlank() || currentForm.password.isBlank()) {
            _status.value = LoginStatus.Error("E-posta ve şifre boş bırakılamaz.")
            return
        }

        viewModelScope.launch {
            _status.value = LoginStatus.Loading

            when (val result = authRepository.login(currentForm.email, currentForm.password)) {
                is AppResult.Success -> _status.value = LoginStatus.Success
                is AppResult.Error -> _status.value = LoginStatus.Error(result.message)
            }
        }
    }

    fun consumeError() {
        if (_status.value is LoginStatus.Error) {
            _status.value = LoginStatus.Idle
        }
    }
}