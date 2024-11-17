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
fun HelpScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ayuda") },
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
            Text("¿Cómo podemos ayudarte?")
            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = { /* Open FAQ */ }) {
                Text("Preguntas frecuentes")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = { /* Open chat support */ }) {
                Text("Chat de soporte")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = { /* Make a call */ }) {
                Text("Llamar a atención al cliente")
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(onClick = { /* Report a problem */ }) {
                Text("Reportar un problema")
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = { /* View terms and conditions */ }) {
                Text("Términos y condiciones")
            }
        }
    }
}