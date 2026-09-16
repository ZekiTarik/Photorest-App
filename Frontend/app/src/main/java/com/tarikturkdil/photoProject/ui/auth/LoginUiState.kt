package com.tarikturkdil.photoProject.ui.auth

data class LoginFormState(
    val email: String = "",
    val password: String = ""
)

sealed interface LoginStatus {
    data object Idle : LoginStatus
    data object Loading : LoginStatus
    data object Success : LoginStatus
    data class Error(val message: String) : LoginStatus
}