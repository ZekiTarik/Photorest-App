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
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _formState = MutableStateFlow(RegisterFormState())
    val formState: StateFlow<RegisterFormState> = _formState.asStateFlow()

    private val _status = MutableStateFlow<RegisterStatus>(RegisterStatus.Idle)
    val status: StateFlow<RegisterStatus> = _status.asStateFlow()

    fun onUsernameChange(value: String) {
        _formState.update { it.copy(username = value) }
    }

    fun onEmailChange(value: String) {
        _formState.update { it.copy(email = value) }
    }

    fun onPasswordChange(value: String) {
        _formState.update { it.copy(password = value) }
    }

    fun onConfirmPasswordChange(value: String) {
        _formState.update { it.copy(confirmPassword = value) }
    }

    fun register() {
        val form = _formState.value

        val validationError = validate(form)
        if (validationError != null) {
            _status.value = RegisterStatus.Error(validationError)
            return
        }

        viewModelScope.launch {
            _status.value = RegisterStatus.Loading

            when (val result = authRepository.register(form.username, form.email, form.password)) {
                is AppResult.Success -> _status.value = RegisterStatus.Success
                is AppResult.Error -> _status.value = RegisterStatus.Error(result.message)
            }
        }
    }

    fun consumeError() {
        if (_status.value is RegisterStatus.Error) {
            _status.value = RegisterStatus.Idle
        }
    }

    private fun validate(form: RegisterFormState): String? {
        return when {
            form.username.isBlank() -> "Kullanıcı adı boş bırakılamaz."
            form.email.isBlank() -> "E-posta boş bırakılamaz."
            form.password.isBlank() -> "Şifre boş bırakılamaz."
            form.password.length < 6 -> "Şifre en az 6 karakter olmalı."
            form.password != form.confirmPassword -> "Şifreler eşleşmiyor."
            else -> null
        }
    }
}