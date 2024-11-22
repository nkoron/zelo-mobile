package com.example.zelo.enter_money

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.zelo.MyApplication
import com.example.zelo.R
import com.example.zelo.dashboard.DashboardViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DepositScreen(
    onBack: () -> Unit = {},
    onDeposit: (Double) -> Unit = {},
    viewModel: DepositViewModel = viewModel(factory = DepositViewModel.provideFactory(
        LocalContext.current.applicationContext as MyApplication
    ))
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedBank by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var showSuccessMessage by remember { mutableStateOf(false) }

    val banks = listOf("Banco Nación", "Banco Galicia", "Banco Santander", "BBVA", "Banco Macro")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header: Bank Selection and Amount Input
        item {
            Text(
                stringResource(R.string.deposit_by_bank),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
            )

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = selectedBank,
                    onValueChange = { showSuccessMessage = false },
                    readOnly = true,
                    placeholder = { Text(stringResource(R.string.select_your_bank)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    },
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = MaterialTheme.colorScheme.tertiary,
                        unfocusedTextColor = MaterialTheme.colorScheme.tertiary,
                        focusedContainerColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.onSurface,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(8.dp)
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    banks.forEach { bank ->
                        DropdownMenuItem(
                            text = { Text(text = bank, color = MaterialTheme.colorScheme.tertiary) },
                            onClick = {
                                showSuccessMessage = false
                                selectedBank = bank
                                expanded = false
                            }

                        )
                    }
                }
            }

            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it; showSuccessMessage = false },
                placeholder = { Text(stringResource(R.string.amount_to_deposit)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = MaterialTheme.colorScheme.tertiary,
                    unfocusedTextColor =  MaterialTheme.colorScheme.tertiary,
                    focusedContainerColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.onSurface,
                    disabledContainerColor = MaterialTheme.colorScheme.onSurface,
                    errorContainerColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                ),
                shape = RoundedCornerShape(8.dp)
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onSurface),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            stringResource(R.string.target_account) + ":",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                    Text("CBU: ${uiState.walletDetail?.cbu}", style = MaterialTheme.typography.bodyMedium)
                    Text("Alias: ${uiState.walletDetail?.alias}", style = MaterialTheme.typography.bodyMedium)
                }
            }

            Button(
                onClick = {
                    amount.toDoubleOrNull()?.let {
                        viewModel.rechargeWallet(it)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(text = stringResource(R.string.start_deposit), color = Color.White)
            }
            LaunchedEffect(uiState.balance) {
                if ( !uiState.isFetching && uiState.error == null && uiState.balance > 0) {
                    showSuccessMessage = true
                }
            }

            if (uiState.error != null) {
                Text(
                    text = uiState.error?.message ?: stringResource(R.string.error_occurred),
                    color = Color.Red,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            if (showSuccessMessage) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .background(color = Color(0xFF4CAF50), shape = RoundedCornerShape(8.dp))
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = stringResource(R.string.success),
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        stringResource(R.string.successful_deposit),
                        color = Color.White,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }
    }
}


@Composable
private fun DepositItem(deposit: Deposit, onRepeat: (deposit:Deposit)-> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                "${deposit.name} - $${String.format("%,.0f", deposit.amount)}",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium
                )
            )
            Text(
                deposit.date.format(DateTimeFormatter.ofPattern(stringResource(R.string.date_format))),
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }

        IconButton(
            onClick = { onRepeat(deposit) },
            modifier = Modifier
                .size(40.dp)
                .background(
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(8.dp)
                )
        ) {
            Icon(
                Icons.Default.Repeat,
                contentDescription = stringResource(R.string.repeat_transfer),
                tint = Color.White
            )
        }
    }
}

data class Deposit(
    val name: String,
    val amount: Double,
    val date: LocalDate
)

@Preview(showBackground = true)
@Composable
fun DepositScreenPreview() {
    MaterialTheme {
        DepositScreen()
    }
}