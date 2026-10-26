package com.example.atvd5

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.atvd5.ui.auth.AuthScreen
import com.example.atvd5.ui.auth.AuthViewModel
import com.example.atvd5.ui.dashboard.DashboardScreen
import com.example.atvd5.ui.theme.Atvd5Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Atvd5Theme {
                val navController = rememberNavController()
                val authViewModel: AuthViewModel = viewModel()

                val startDestination = if (authViewModel.currentUser != null) "dashboard" else "auth"

                NavHost(navController = navController, startDestination = startDestination) {
                    composable("auth") {
                        AuthScreen(
                            viewModel = authViewModel,
                            onAuthSuccess = {
                                navController.navigate("dashboard") {
                                    popUpTo("auth") { inclusive = true }
                                }
                            }
                        )
                    }
                    composable("dashboard") {
                        DashboardScreen(
                            viewModel = authViewModel,
                            onLogout = {
                                navController.navigate("auth") {
                                    popUpTo("dashboard") { inclusive = true }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}