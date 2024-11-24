package com.example.zelo.activity

import TransactionItem
import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.zelo.MyApplication
import com.example.zelo.R
import com.example.zelo.components.ZeloSearchBar
import com.example.zelo.network.model.User


@Composable
fun ExpensesScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: ExpensesViewModel = viewModel(factory = ExpensesViewModel.provideFactory(
        LocalContext.current.applicationContext as MyApplication
    ))
) {
    val uiState by viewModel.uiState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    // Filtrar movimientos basados en la búsqueda
    val filteredMovements = remember(searchQuery, uiState.movements) {
        if (searchQuery.isBlank()) {
            uiState.movements
        } else {
            uiState.movements.filter { movement ->
                val payer = movement.receiver
                // Verificar si el nombre o el monto coinciden con la búsqueda
                payer?.firstName?.contains(searchQuery, ignoreCase = true) == true ||
                        payer?.lastName?.contains(searchQuery, ignoreCase = true) == true ||
                        movement.amount.toString().contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(R.string.recent_expenses),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(vertical = 16.dp)
        )
        ZeloSearchBar(
            searchQuery = searchQuery,
            valueChange = { searchQuery = it }
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (filteredMovements.isEmpty()) {
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
            LazyColumn {
                items(filteredMovements.size) { index ->
                    val payment = filteredMovements[index]
                    TransactionItem(
                        name = "${payment.receiver.firstName} ${payment.receiver.lastName}",
                        description = "${stringResource(R.string.sent)}: ${payment.amount}",
                        time = payment.createdAt,
                        showAvatar = true,
                        movements = filteredMovements,
                        id = index,
                        isPayer = true
                    )
                }
            }
        }
    }
}

