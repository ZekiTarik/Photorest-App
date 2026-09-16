package com.tarikturkdil.photoProject.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.delay

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: RegisterViewModel = hiltViewModel()
) {
    val formState by viewModel.formState.collectAsStateWithLifecycle()
    val status by viewModel.status.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // Success: önce Snackbar göster, kısa bir bekleme sonrası Login'e yönlendir
    LaunchedEffect(status) {
        if (status is RegisterStatus.Success) {
            snackbarHostState.showSnackbar("Kayıt başarılı! Giriş ekranına yönlendiriliyorsunuz...")
            delay(1500)
            onRegisterSuccess()
        }
    }

    // Error: Snackbar göster, sonra state'i temizle
    LaunchedEffect(status) {
        val currentStatus = status
        if (currentStatus is RegisterStatus.Error) {
            snackbarHostState.showSnackbar(currentStatus.message)
            viewModel.consumeError()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Hesap Oluştur",
                style = MaterialTheme.typography.headlineSmall
            )

            OutlinedTextField(
                value = formState.username,
                onValueChange = viewModel::onUsernameChange,
                label = { Text("Kullanıcı Adı") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            )

            OutlinedTextField(
                value = formState.email,
                onValueChange = viewModel::onEmailChange,
                label = { Text("E-posta") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            )

            OutlinedTextField(
                value = formState.password,
                onValueChange = viewModel::onPasswordChange,
                label = { Text("Şifre") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            )

            OutlinedTextField(
                value = formState.confirmPassword,
                onValueChange = viewModel::onConfirmPasswordChange,
                label = { Text("Şifre (Tekrar)") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            )

            Button(
                onClick = { viewModel.register() },
                enabled = status !is RegisterStatus.Loading && status !is RegisterStatus.Success,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp)
            ) {
                if (status is RegisterStatus.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.padding(4.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Kayıt Ol")
                }
            }

            TextButton(
                onClick = onNavigateToLogin,
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text("Zaten hesabın var mı? Giriş yap")
            }
        }
    }
}