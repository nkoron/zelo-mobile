package com.example.zelo.transference

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.zelo.cards.inferBankName

data class PaymentMethod(
    val id : Int? = null,
    val type: String,
    val name: String,
    val digits: String,
    val balance: String? = null,
    val cardType: String? = null,
    val backgroundColor: Color = Color(0xFFF5F5F5)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransferDetailScreen(
    viewModel: TransferenceCBUViewModel,
    onConfirm: () -> Unit,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Transferencias") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)

        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Input Fields
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // CBU/ALIAS Field
                OutlinedTextField(
                    value = uiState.cbuAlias,
                    onValueChange = { viewModel.updateCbuAlias(it) },
                    label = { Text("CBU / ALIAS") },
                    placeholder = { Text("0000 0000 0000 2222 2222") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                // Amount Field
                OutlinedTextField(
                    value = uiState.amount,
                    onValueChange = { viewModel.updateAmount(it) },
                    label = { Text("Monto") },
                    placeholder = { Text("30000") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(8.dp)
                )

                // Concept Field
                OutlinedTextField(
                    value = uiState.concept,
                    onValueChange = { viewModel.updateConcept(it) },
                    label = { Text("Concepto") },
                    placeholder = { Text("Ingresar...") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )
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
                    uiState.availablePaymentMethods.forEach { method ->
                        PaymentMethodCard(
                            paymentMethod = method,
                            isSelected = uiState.selectedPaymentMethod == method,
                            onClick = { viewModel.selectPaymentMethod(method) }
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
                    onClick = {
                        Log.d("TransferenceCBU", "Transfer Button Clicked")
                        onConfirm()
                    },
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
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Icon(
                            Icons.Default.ArrowForward,
                            contentDescription = "Transferir"
                        )
                    }
                }

                // Cancel Button
                Button(
                    onClick = onBack,
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
                        style = MaterialTheme.typography.bodyLarge
                    )
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
            .height(100.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = paymentMethod.backgroundColor
        ),
        border = if (isSelected) {
            BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
        } else null
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
                    color = if (paymentMethod.type == "BALANCE") Color.Black else Color.White
                )
                if (paymentMethod.type == "BALANCE") {
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

            if (paymentMethod.type == "BALANCE") {
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
                    "•••• ${paymentMethod.digits.takeLast(4)}",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
                Text(
                    paymentMethod.type ?: "",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }
    }
}
