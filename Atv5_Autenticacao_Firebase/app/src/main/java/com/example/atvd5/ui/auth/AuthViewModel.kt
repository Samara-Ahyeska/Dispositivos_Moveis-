package com.example.atvd5.ui.auth

import android.util.Patterns
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AuthViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState

    val currentUser get() = auth.currentUser

    fun loginUser(email: String, pass: String) {
        if (!validateInputs(email, pass)) return

        _uiState.value = AuthUiState.Loading
        auth.signInWithEmailAndPassword(email.trim(), pass)
            .addOnCompleteListener { task: Task<AuthResult> ->
                if (task.isSuccessful) {
                    _uiState.value = AuthUiState.Success("Login realizado com sucesso!")
                } else {
                    val errorMsg = when (task.exception) {
                        is FirebaseAuthInvalidUserException -> "Utilizador não encontrado."
                        is FirebaseAuthInvalidCredentialsException -> "E-mail ou palavra-passe incorretos."
                        else -> task.exception?.localizedMessage ?: "Erro ao realizar login."
                    }
                    _uiState.value = AuthUiState.Error(errorMsg)
                }
            }
    }

    fun registerUser(email: String, pass: String) {
        if (!validateInputs(email, pass)) return

        _uiState.value = AuthUiState.Loading
        auth.createUserWithEmailAndPassword(email.trim(), pass)
            .addOnCompleteListener { task: Task<AuthResult> ->
                if (task.isSuccessful) {
                    _uiState.value = AuthUiState.Success("Conta criada com sucesso!")
                } else {
                    val errorMsg = when (task.exception) {
                        is FirebaseAuthWeakPasswordException -> "A palavra-passe deve ter pelo menos 6 caracteres."
                        is FirebaseAuthUserCollisionException -> "Este e-mail já está registado."
                        is FirebaseAuthInvalidCredentialsException -> "Formato de e-mail inválido."
                        else -> task.exception?.localizedMessage ?: "Erro ao criar conta."
                    }
                    _uiState.value = AuthUiState.Error(errorMsg)
                }
            }
    }

    fun resetPassword(email: String) {
        if (email.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _uiState.value = AuthUiState.Error("Informe um e-mail válido para redefinição.")
            return
        }

        _uiState.value = AuthUiState.Loading
        auth.sendPasswordResetEmail(email.trim())
            .addOnCompleteListener { task: Task<Void> ->
                if (task.isSuccessful) {
                    _uiState.value = AuthUiState.Success("E-mail de redefinição enviado com sucesso!")
                } else {
                    _uiState.value = AuthUiState.Error("Falha ao enviar e-mail. Verifique o endereço.")
                }
            }
    }

    fun logout() {
        auth.signOut()
        _uiState.value = AuthUiState.Idle
    }

    fun resetState() {
        _uiState.value = AuthUiState.Idle
    }

    private fun validateInputs(email: String, pass: String): Boolean {
        if (email.isBlank() || pass.isBlank()) {
            _uiState.value = AuthUiState.Error("Preencha todos os campos.")
            return false
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _uiState.value = AuthUiState.Error("Formato de e-mail inválido.")
            return false
        }
        if (pass.length < 6) {
            _uiState.value = AuthUiState.Error("A palavra-passe deve conter no mínimo 6 caracteres.")
            return false
        }
        return true
    }
}