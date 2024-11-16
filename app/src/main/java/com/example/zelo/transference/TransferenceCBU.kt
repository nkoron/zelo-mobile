package com.example.zelo.transference

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ArrowRightAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransferDetailScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    var cbuAlias by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var concept by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Transferencias") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Balance Display
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "SALDO DISPONIBLE",
                        style = MaterialTheme.typography.labelMedium
                    )
                    Text(
                        "$81,910.00",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            // Input Fields
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // CBU/ALIAS Field
                Column {
                    Text(
                        "CBU / ALIAS",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Black
                    )
                    val containerColor = Color(0xFFF5F5F5)
                    TextField(
                        value = cbuAlias,
                        onValueChange = { cbuAlias = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("0000 0000 0000 2222 2222") },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = containerColor,
                            unfocusedContainerColor = containerColor,
                            disabledContainerColor = containerColor,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )
                }

                // Amount Field
                Column {
                    Text(
                        "Monto",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Black
                    )
                    val containerColor = Color(0xFFF5F5F5)
                    TextField(
                        value = amount,
                        onValueChange = { amount = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("30000") },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = containerColor,
                            unfocusedContainerColor = containerColor,
                            disabledContainerColor = containerColor,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )
                }

                // Concept Field
                Column {
                    Text(
                        "Concepto",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Black
                    )
                    val containerColor = Color(0xFFF5F5F5)
                    TextField(
                        value = concept,
                        onValueChange = { concept = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Ingresar...") },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = containerColor,
                            unfocusedContainerColor = containerColor,
                            disabledContainerColor = containerColor,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            }

            // Transfer Button
            Button(
                onClick = { navController.navigate("transference/confirm") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Transferir",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = Color.White
                        )
                    )
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Transferir")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TransferDetailScreenPreview() {
    val navController = rememberNavController();
    MaterialTheme {
        TransferDetailScreen(navController);
    }
}