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
fun MessagesScreen(navController: NavController) {
    var promotions by remember { mutableStateOf(false) }
    var transactionAlerts by remember { mutableStateOf(true) }
    var securityAlerts by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mensajes") },
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
            Text("Configura las notificaciones y mensajes que deseas recibir.")
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Promociones y ofertas")
                Switch(
                    checked = promotions,
                    onCheckedChange = { promotions = it }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Alertas de transacciones")
                Switch(
                    checked = transactionAlerts,
                    onCheckedChange = { transactionAlerts = it }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Alertas de seguridad")
                Switch(
                    checked = securityAlerts,
                    onCheckedChange = { securityAlerts = it }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = { /* Save message preferences */ }) {
                Text("Guardar preferencias")
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(onClick = { /* View message history */ }) {
                Text("Ver historial de mensajes")
            }
        }
    }
}