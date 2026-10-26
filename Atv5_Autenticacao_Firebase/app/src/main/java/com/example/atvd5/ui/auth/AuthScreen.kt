package com.example.atvd5.ui.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class AuthMode { LOGIN, REGISTER, FORGOT_PASSWORD }

@Composable
fun AuthScreen(
    viewModel: AuthViewModel,
    onAuthSuccess: () -> Unit
) {
    var mode by remember { mutableStateOf(AuthMode.LOGIN) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }

    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState) {
        when (uiState) {
            is AuthUiState.Success -> {
                snackbarHostState.showSnackbar((uiState as AuthUiState.Success).message)
                if (mode == AuthMode.LOGIN || mode == AuthMode.REGISTER) {
                    onAuthSuccess()
                } else {
                    mode = AuthMode.LOGIN
                }
                viewModel.resetState()
            }
            is AuthUiState.Error -> {
                snackbarHostState.showSnackbar((uiState as AuthUiState.Error).message)
                viewModel.resetState()
            }
            else -> {}
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = when (mode) {
                        AuthMode.LOGIN -> "Bem-vindo de volta"
                        AuthMode.REGISTER -> "Criar nova conta"
                        AuthMode.FORGOT_PASSWORD -> "Recuperar palavra-passe"
                    },
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("E-mail") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                AnimatedVisibility(visible = mode != AuthMode.FORGOT_PASSWORD) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text("Palavra-passe") },
                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                            trailingIcon = {
                                IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                    Icon(
                                        imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = null
                                    )
                                }
                            },
                            singleLine = true,
                            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        if (mode == AuthMode.REGISTER && password.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            PasswordStrengthIndicator(password = password)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        when (mode) {
                            AuthMode.LOGIN -> viewModel.loginUser(email, password)
                            AuthMode.REGISTER -> viewModel.registerUser(email, password)
                            AuthMode.FORGOT_PASSWORD -> viewModel.resetPassword(email)
                        }
                    },
                    enabled = uiState !is AuthUiState.Loading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (uiState is AuthUiState.Loading) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text(
                            text = when (mode) {
                                AuthMode.LOGIN -> "Entrar"
                                AuthMode.REGISTER -> "Registar"
                                AuthMode.FORGOT_PASSWORD -> "Enviar E-mail"
                            },
                            fontSize = 16.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                when (mode) {
                    AuthMode.LOGIN -> {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            TextButton(onClick = { mode = AuthMode.FORGOT_PASSWORD }) {
                                Text("Esqueci a minha palavra-passe")
                            }
                            TextButton(onClick = { mode = AuthMode.REGISTER }) {
                                Text("Criar conta")
                            }
                        }
                    }
                    AuthMode.REGISTER -> {
                        TextButton(onClick = { mode = AuthMode.LOGIN }) {
                            Text("Já tem conta? Faça Login")
                        }
                    }
                    AuthMode.FORGOT_PASSWORD -> {
                        TextButton(onClick = { mode = AuthMode.LOGIN }) {
                            Text("Voltar ao Login")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PasswordStrengthIndicator(password: String) {
    val strength = when {
        password.length < 6 -> "Fraca" to Color.Red
        password.length >= 8 && password.any { it.isDigit() } && password.any { !it.isLetterOrDigit() } -> "Forte" to Color(0xFF2E7D32)
        else -> "Média" to Color(0xFFFF9800)
    }

    Row(verticalAlignment = Alignment.CenterVertically) {
        Text("Força da palavra-passe: ", style = MaterialTheme.typography.bodySmall)
        Text(
            text = strength.first,
            color = strength.second,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.bodySmall
        )
    }
}