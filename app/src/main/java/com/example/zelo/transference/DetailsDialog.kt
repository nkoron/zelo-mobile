package com.example.zelo.transference

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.zelo.R
import com.example.zelo.dashboard.DashboardUiState
import com.example.zelo.network.model.Payment


@Composable
fun TransferDetailsDialog(
    onDismiss: () -> Unit,
    onRepeatTransfer: () -> Unit,
    onViewReceipt: () -> Unit,
    onRequestRefund: () -> Unit,
    movements: List<Payment>,
    id: Int,
    isPayer: Boolean
) {
    val payment = movements[id]
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.transaction_details),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 24.dp),
                    color = MaterialTheme.colorScheme.tertiary
                )

                Text(
                    text = if (isPayer) stringResource(R.string.transfer_to) else stringResource(R.string.transfer_from),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    modifier = Modifier.align(Alignment.Start),
                )

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    color = Color(0xFFF5F5F5),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = stringResource(R.string.full_name)+": " + if (isPayer) payment.receiver.firstName + " " + payment.receiver.lastName else payment.payer.firstName + " " + payment.payer.lastName,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                        Text(
                            text = stringResource(R.string.email)+": "+ if (isPayer) payment.receiver.email else payment.payer.email,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    }
                }

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    color = Color(0xFFF5F5F5),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = stringResource(R.string.amount)+": "+ payment.amount.toString(),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                        Text(
                            text = stringResource(R.string.payment_method) + ": " + payment.type,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    }
                }

                Text(
                    text = stringResource(R.string.transaction_id) + ": "+ payment.id,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )

                Button(
                    onClick = onViewReceipt,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(stringResource(R.string.see_receipt),color = Color.White)
                }

                Button(
                    onClick = onRequestRefund,
                    modifier = Modifier.padding(top = 8.dp)
                        .fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Gray
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        stringResource(R.string.close),
                        color = Color.White,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.clickable { onDismiss() }
                    )
                }
            }
        }
    }
}


//// Preview
//@Preview(showBackground = true, locale = "en")
//@Composable
//fun TransferDetailsDialogPreview() {
//    MaterialTheme {
//        TransferDetailsDialog(
//            onDismiss = {},
//            onRepeatTransfer = {},
//            onViewReceipt = {},
//            onRequestRefund = {}
//        )
//    }
//}