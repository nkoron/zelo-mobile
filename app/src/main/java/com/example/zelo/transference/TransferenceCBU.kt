package com.example.zelo.transference

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.example.zelo.R
import kotlinx.coroutines.launch

import android.util.Log

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransferDetailScreen(
    viewModel: TransferenceCBUViewModel,
    onConfirm: () -> Unit,
    onBack: () -> Unit,
    email: String? = null,
    amount: Double? = null
) {
    LaunchedEffect(Unit) {
        viewModel.resetTransferForm()
        viewModel.refreshPaymentMethods()
    }

    LaunchedEffect(Unit) {
        viewModel.resetTransferForm()
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(email) {
        email?.let { viewModel.updateEmail(it) }
    }

    LaunchedEffect(amount) {
        amount?.let {
            viewModel.updateAmount(it.toString())
            Log.d("TransferDetailScreen", "Amount updated: $it")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Input Fields
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Email Field
            OutlinedTextField(
                value = uiState.email,
                onValueChange = { viewModel.updateEmail(it) },
                label = { Text("Email") },
                placeholder = { Text("example@mail.com") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.primary,
                    focusedTextColor = MaterialTheme.colorScheme.tertiary,
                    unfocusedTextColor = MaterialTheme.colorScheme.tertiary
                )
            )

            // Amount Field
            OutlinedTextField(
                value = if (uiState.amount == "0.0") "" else uiState.amount,
                onValueChange = { viewModel.updateAmount(it) },
                label = { Text(stringResource(R.string.amount_to_transfer)) },
                placeholder = { Text("30000") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.primary,
                    focusedTextColor = MaterialTheme.colorScheme.tertiary,
                    unfocusedTextColor = MaterialTheme.colorScheme.tertiary
                ),
            )

            // Concept Field
            OutlinedTextField(
                value = uiState.concept,
                onValueChange = { viewModel.updateConcept(it) },
                label = { Text(stringResource(R.string.concept)) },
                placeholder = { Text(stringResource(R.string.enter_placeholder)) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.primary,
                    focusedTextColor = MaterialTheme.colorScheme.tertiary,
                    unfocusedTextColor = MaterialTheme.colorScheme.tertiary
                )
            )
        }

        // Payment Method Selection
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                stringResource(R.string.payment_method),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )

            Row(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .padding(vertical = 8.dp)
                    .background(color = MaterialTheme.colorScheme.onSurface),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                uiState.availablePaymentMethods.forEach { method ->
                    PaymentMethodCard(
                        paymentMethod = method,
                        isSelected = uiState.selectedPaymentMethod == method,
                        onClick = { viewModel.selectPaymentMethod(method) },
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
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        stringResource(R.string.transfer),
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White
                    )
                    Icon(
                        Icons.Default.ArrowForward,
                        contentDescription = stringResource(R.string.transfer),
                        tint = Color.White
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
                    containerColor = MaterialTheme.colorScheme.secondary
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    stringResource(R.string.cancel),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}



@Composable
fun PaymentMethodCard(
    paymentMethod: PaymentMethod,
    isSelected: Boolean,
    onClick: () -> Unit,
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

