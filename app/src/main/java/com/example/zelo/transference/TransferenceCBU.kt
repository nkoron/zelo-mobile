package com.example.zelo.transference

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
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

data class PaymentMethod(
    val type: String,
    val name: String,
    val lastDigits: String,
    val balance: String? = null,
    val cardType: String? = null,
    val backgroundColor: Color = Color(0xFFF5F5F5)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransferDetailScreen(
    onConfirm: () -> Unit = {},
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    var cbuAlias by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var concept by remember { mutableStateOf("") }
    var selectedPaymentMethod by remember { mutableStateOf<PaymentMethod?>(null) }

    val paymentMethods = listOf(
        PaymentMethod(
            type = "balance",
            name = "Saldo en Cuenta",
            lastDigits = "",
            balance = "$0",
            backgroundColor = Color(0xFFF5F5F5)
        ),
        PaymentMethod(
            type = "credit",
            name = "Brubank",
            lastDigits = "3456",
            cardType = "Tarjeta de Crédito",
            backgroundColor = Color(0xFF6C63FF)
        ),
        PaymentMethod(
            type = "credit",
            name = "Galicia",
            lastDigits = "7654",
            cardType = "Tarjeta de Crédito",
            backgroundColor = Color(0xFFFF4B4B)
        ),
        PaymentMethod(
            type = "credit",
            name = "Galicia",
            lastDigits = "7654",
            cardType = "Tarjeta de Crédito",
            backgroundColor = Color(0xFFFF4B4B)
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Transferencias") },
                navigationIcon = {
                    IconButton(onClick = { onBack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
    ) { paddingValues ->
        Card {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))

//            // Balance Display
//            Card(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(vertical = 8.dp),
//                colors = CardDefaults.cardColors(
//                    containerColor = MaterialTheme.colorScheme.surfaceVariant
//                )
//            ) {
//                Column(
//                    modifier = Modifier.padding(16.dp),
//                    horizontalAlignment = Alignment.CenterHorizontally
//                ) {
//                    Text(
//                        "SALDO DISPONIBLE",
//                        style = MaterialTheme.typography.labelMedium
//                    )
//                    Text(
//                        "$81,910.00",
//                        style = MaterialTheme.typography.headlineMedium.copy(
//                            fontWeight = FontWeight.Bold,
//                            color = MaterialTheme.colorScheme.primary
//                        )
//                    )
//                }
//            }

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

                // Payment Method Selection
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        "Seleccionar método de pago",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )

                    Row(
                        modifier = Modifier
                            .horizontalScroll(rememberScrollState())
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        paymentMethods.forEach { method ->
                            PaymentMethodCard(
                                paymentMethod = method,
                                isSelected = selectedPaymentMethod == method,
                                onClick = { selectedPaymentMethod = method }
                            )
                        }
                    }
                }

                // Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Transfer Button
                    Button(
                        onClick = { onConfirm() },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF6C63FF)
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
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Transferir"
                            )
                        }
                    }

                    // Cancel Button
                    Button(
                        onClick = { onBack() },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFABABAB)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            "CANCELAR",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = Color.White
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PaymentMethodCard(
    paymentMethod: PaymentMethod,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(200.dp)
            .height(100.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = paymentMethod.backgroundColor
        ),
        border = if (isSelected) {
            CardDefaults.outlinedCardBorder(true)
        } else null,
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    paymentMethod.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (paymentMethod.type == "balance") Color.Black else Color.White
                )
                if (paymentMethod.type == "balance") {
                    Icon(
                        Icons.Default.AccountBalance,
                        contentDescription = null,
                        tint = Color.Black
                    )
                } else {
                    Icon(
                        Icons.Default.CreditCard,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            }

            if (paymentMethod.type == "balance") {
                Text(
                    "Dinero Disponible",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
                Text(
                    paymentMethod.balance ?: "",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Black
                )
            } else {
                Text(
                    "•••• ${paymentMethod.lastDigits}",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
                Text(
                    paymentMethod.cardType ?: "",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }
    }
}