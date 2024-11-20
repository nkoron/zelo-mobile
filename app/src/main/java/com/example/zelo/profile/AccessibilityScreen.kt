package com.example.zelo.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.zelo.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccessibilityScreen(onBack: () -> Unit) {
    var textSizeMultiplier by remember { mutableFloatStateOf(1f) }
    var highContrastMode by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.accessibility)) },
                navigationIcon = {
                    IconButton(onClick = { onBack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.back))
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
            Text(stringResource(R.string.adjust_access_options))
            Spacer(modifier = Modifier.height(16.dp))

            Text(stringResource(R.string.text_size))
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
                Text(stringResource(R.string.high_contrast_mode))
                Switch(
                    checked = highContrastMode,
                    onCheckedChange = { highContrastMode = it }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = { /* Save accessibility settings */ }) {
                Text(stringResource(R.string.save_changes))
            }
        }
    }
}