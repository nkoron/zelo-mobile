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
fun AccessibilityScreen(navController: NavController) {
    var textSizeMultiplier by remember { mutableStateOf(1f) }
    var highContrastMode by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Accesibilidad") },
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
            Text("Ajusta las opciones de accesibilidad para mejorar tu experiencia en la app.")
            Spacer(modifier = Modifier.height(16.dp))

            Text("Tamaño del texto")
            Slider(
                value = textSizeMultiplier,
                onValueChange = { textSizeMultiplier = it },
                valueRange = 0.5f..2f,
                steps = 3
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Modo de alto contraste")
                Switch(
                    checked = highContrastMode,
                    onCheckedChange = { highContrastMode = it }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = { /* Save accessibility settings */ }) {
                Text("Guardar cambios")
            }
        }
    }
}