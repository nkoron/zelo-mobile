package com.example.zelo.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyScreen(navController: NavController) {
    var dataSharing by remember { mutableStateOf(false) }
    var locationTracking by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Privacidad") },
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
            Text("Controla cómo se utilizan tus datos personales.")
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Compartir datos con terceros")
                Switch(
                    checked = dataSharing,
                    onCheckedChange = { dataSharing = it }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Seguimiento de ubicación")
                Switch(
                    checked = locationTracking,
                    onCheckedChange = { locationTracking = it }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = { /* View data usage */ }) {
                Text("Ver uso de datos")
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(onClick = { /* Request data deletion */ }) {
                Text("Solicitar eliminación de datos")
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = { /* View privacy policy */ }) {
                Text("Ver política de privacidad")
            }
        }
    }
}