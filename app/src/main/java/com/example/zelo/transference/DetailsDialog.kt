package com.example.zelo.transference

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog



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
                    text = "Detalle del movimiento",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                Text(
                    text = "Transferencia a",
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
                            text = "Transferencia de dinero: $3.000",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = "Medio de pago: Dinero disponible en cuenta",
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
                    text = "Numero de operacion: 12345678",
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
                    Text("Repetir transferencia")
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
                    Text("Ver comprobante")
                }

                TextButton(
                    onClick = onRequestRefund,
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text(
                        "Cerrar",
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
@Preview(showBackground = true)
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