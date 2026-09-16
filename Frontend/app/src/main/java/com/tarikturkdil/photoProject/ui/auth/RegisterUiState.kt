package com.tarikturkdil.photoProject.ui.auth

data class RegisterFormState(
    val username: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = ""
)

sealed interface RegisterStatus {
    data object Idle : RegisterStatus
    data object Loading : RegisterStatus
    data object Success : RegisterStatus
    data class Error(val message: String) : RegisterStatus
}