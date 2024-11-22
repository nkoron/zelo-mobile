package com.example.zelo.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.zelo.MyApplication

@Composable
fun PaymentLinkDetailsScreen(
    linkUuid: String,
    viewModel: PaymentLinkDetailsViewModel = viewModel(
        factory = PaymentLinkDetailsViewModel.provideFactory(LocalContext.current.applicationContext as MyApplication, linkUuid)
    ),
    onPaymentComplete: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(linkUuid) {
        viewModel.loadLinkDetails(linkUuid)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Payment Details",
            style = MaterialTheme.typography.headlineMedium
        )

        when {
            uiState.isLoading -> CircularProgressIndicator()
            uiState.error != null -> Text(
                text = uiState.error!!,
                color = MaterialTheme.colorScheme.error
            )
            uiState.paymentDetails != null -> {
                val details = uiState.paymentDetails!!
                Text("Amount: ${details.amount}")
                Text("Description: no hay en api creo")

            }
        }
    }

    if (uiState.paymentComplete) {
        LaunchedEffect(Unit) {
            onPaymentComplete()
        }
    }
}