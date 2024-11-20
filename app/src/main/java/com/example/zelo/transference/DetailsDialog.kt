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


@Composable
fun TransferDetailsDialog(
    onDismiss: () -> Unit,
    onRepeatTransfer: () -> Unit,
    onViewReceipt: () -> Unit,
    onRequestRefund: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
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
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                Text(
                    text = stringResource(R.string.transfer_to),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    modifier = Modifier.align(Alignment.Start)
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
                            text = "Miguel Rodriguez",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = "CUIT/CUIL: 20454545456",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "mro*****@gmail.com",
                            style = MaterialTheme.typography.bodyMedium
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
                            text = stringResource(R.string.money_transfer)+": $3.000",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = stringResource(R.string.payment_method) + ": Dinero disponible en cuenta",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                Text(
                    text = "Creada el 6 de Septiembre - 18:54hs",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 16.dp)
                )

                Text(
                    text = stringResource(R.string.transaction_id) + ": 12345678",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )

                Button(
                    onClick = onRepeatTransfer,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFD14D72)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(stringResource(R.string.repeat_transfer))
                }

                Button(
                    onClick = onViewReceipt,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF8F00FF)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(stringResource(R.string.see_receipt))
                }

                TextButton(
                    onClick = onRequestRefund,
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text(
                        stringResource(R.string.close),
                        color = Color.Gray,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.clickable { onDismiss() }
                    )
                }
            }
        }
    }
}


// Preview
@Preview(showBackground = true, locale = "en")
@Composable
fun TransferDetailsDialogPreview() {
    MaterialTheme {
        TransferDetailsDialog(
            onDismiss = {},
            onRepeatTransfer = {},
            onViewReceipt = {},
            onRequestRefund = {}
        )
    }
}