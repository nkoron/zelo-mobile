package com.example.zelo.transference

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.zelo.MyApplication
import com.example.zelo.R
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransferScreen(
    onNavigateBack: () -> Unit = {},
    onNavigateToForm: () -> Unit = {},
    onNavigateToContacts: () -> Unit = {},
    onNavigateToTransferenceCBU: (String, Double) -> Unit,
    viewModel: TransferenceViewModel = viewModel(
        factory = TransferenceViewModel.provideFactory(
            LocalContext.current.applicationContext as MyApplication
        )
    )
) {
    val uiState by viewModel.uiState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.checkAuthenticationStatus()
        viewModel.getCurrentUser()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Action Buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = onNavigateToForm,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) {
                Text("EMAIL", textAlign = TextAlign.Center, fontSize = 12.sp)
            }
            Button(
                onClick = onNavigateToContacts,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) {
                Text(
                    stringResource(R.string.contacts),
                    textAlign = TextAlign.Center,
                    fontSize = 12.sp
                )
            }
        }

        // Search Bar
        val containerColor = MaterialTheme.colorScheme.onSurface
        TextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.medium),
            placeholder = { Text(stringResource(R.string.search)) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            trailingIcon = { Icon(Icons.Default.FilterList, contentDescription = "Filter") },
            colors = TextFieldDefaults.colors(
                focusedTextColor = MaterialTheme.colorScheme.tertiary,
                unfocusedTextColor = MaterialTheme.colorScheme.tertiary,
                focusedContainerColor = containerColor,
                unfocusedContainerColor = containerColor,
                disabledContainerColor = containerColor
            ),
            singleLine = true
        )
        Text(
            stringResource(R.string.recent_transfers),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        if(uiState.isFetching){
            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
        } else {
            if (uiState.movements.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.no_recent_transactions),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f)
                ) {
                    items(uiState.movements.size) { index ->
                        val payment = uiState.movements[index]
                        TransactionItem(
                            name = "${payment.receiver.firstName} ${payment.receiver.lastName}",
                            description = "${stringResource(R.string.sent)}: ${payment.amount}",
                            time = payment.createdAt,
                            showAvatar = true,
                            email = payment.receiver.email,
                            amount = payment.amount,
                            onRedoTransfer = { email, amount ->
                                onNavigateToTransferenceCBU(email, amount)
                            }
                        )
                    }
                }
        }
        // Recent Transfers


        }
    }
}

@Composable
fun TransactionItem(
    name: String,
    description: String,
    time: String,
    showAvatar: Boolean = false,
    email: String,
    amount: Double,
    onRedoTransfer: (String, Double) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (showAvatar) {
            Icon(
                Icons.Default.AccountCircle,
                contentDescription = null,
                modifier = Modifier.size(40.dp)
            )
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp)
        ) {
            Text(name, style = MaterialTheme.typography.bodyLarge)
            Text(
                description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                time,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        IconButton(
            onClick = { onRedoTransfer(email, amount) },
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer)
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "Redo transfer",
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

