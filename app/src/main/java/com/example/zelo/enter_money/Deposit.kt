package com.example.zelo.enter_money

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.zelo.R
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DepositScreen(
    onBack: () -> Unit = {},
    onDeposit: (Double) -> Unit = {}
) {
    var selectedBank by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var showSuccessMessage by remember { mutableStateOf(false) }

    val banks = listOf("Banco Nación", "Banco Galicia", "Banco Santander", "BBVA", "Banco Macro")

    val recentDeposits = remember {
        listOf(
            Deposit("Miguel", 1000.0, LocalDate.now()),
            Deposit("McDonalds", 11400.0, LocalDate.now()),
            Deposit("KFC", 100000.0, LocalDate.now()),
            Deposit("Miguel", 1900.0, LocalDate.now()),
            Deposit("Jose", 10000.0, LocalDate.now())
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.deposit)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black,
                    navigationIconContentColor = Color.Black
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            Text(
                stringResource(R.string.deposit_by_bank),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
            )

            // Bank Selection
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = selectedBank,
                    onValueChange = {},
                    readOnly = true,
                    placeholder = { Text(stringResource(R.string.select_your_bank)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        containerColor = Color(0xFFF5F5F5),
                        unfocusedBorderColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(8.dp)
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    banks.forEach { bank ->
                        DropdownMenuItem(
                            text = { Text(bank) },
                            onClick = {
                                selectedBank = bank
                                expanded = false
                            }
                        )
                    }
                }
            }

            // Amount Input
            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                placeholder = { Text(stringResource(R.string.amount_to_deposit)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    containerColor = Color(0xFFF5F5F5),
                    unfocusedBorderColor = Color.Transparent
                ),
                shape = RoundedCornerShape(8.dp)
            )

            // Account Information
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFF5F5F5)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
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
                            stringResource(R.string.target_account)+ ":",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                    Text("CBU: 10", style = MaterialTheme.typography.bodyMedium)
                    Text("Alias: n3pz7g90", style = MaterialTheme.typography.bodyMedium)
                }
            }

            // Deposit Button
            Button(
                onClick = {
                    amount.toDoubleOrNull()?.let {
                        onDeposit(it)
                        showSuccessMessage = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF6C63FF)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(stringResource(R.string.start_deposit))
            }

            if (showSuccessMessage) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .background(
                            color = Color(0xFF4CAF50),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = "Success",
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

            // Recent Deposits
            Text(
                stringResource(R.string.last_deposits),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(recentDeposits) { deposit ->
                    DepositItem(
                        deposit = deposit,
                        onRepeat = {
                            selectedBank = it.name
                            amount = it.amount.toString()
                        })
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
                    color = Color(0xFF6C63FF),
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