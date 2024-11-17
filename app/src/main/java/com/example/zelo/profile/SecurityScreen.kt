package com.example.zelo.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecurityScreen(navController: NavController) {
    var twoFactorAuth by remember { mutableStateOf(false) }
    var biometricLogin by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Seguridad") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text("Configura las opciones de seguridad de tu cuenta.")
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Autenticación de dos factores")
                Switch(
                    checked = twoFactorAuth,
                    onCheckedChange = { twoFactorAuth = it }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Inicio de sesión biométrico")
                Switch(
                    checked = biometricLogin,
                    onCheckedChange = { biometricLogin = it }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = { /* Change password */ }) {
                Text("Cambiar contraseña")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = { /* View login history */ }) {
                Text("Ver historial de inicio de sesión")
            }
        }
    }
}