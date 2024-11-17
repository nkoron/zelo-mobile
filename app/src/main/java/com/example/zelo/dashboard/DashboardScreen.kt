import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController

@Composable
fun DashboardScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    userName: String = "Fer Galan",
    balance: Double = 81910.00
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(16.dp)
    ) {
        // Profile Section
//        Row(
//            modifier = Modifier.fillMaxWidth(),
//            horizontalArrangement = Arrangement.SpaceBetween,
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Row(verticalAlignment = Alignment.CenterVertically) {
//                Surface(
//                    shape = CircleShape,
//                    color = Color(0xFF6C63FF),
//                    modifier = Modifier.size(48.dp)
//                ) {
//                    Text(
//                        text = userName.first().toString(),
//                        color = Color.White,
//                        modifier = Modifier.padding(16.dp)
//                    )
//                }
//                Spacer(modifier = Modifier.width(12.dp))
//                Column {
//                    Text(
//                        text = "Hola, $userName!",
//                        style = MaterialTheme.typography.titleMedium,
//                        fontWeight = FontWeight.Bold
//                    )
//                }
//            }
//            IconButton(onClick = { /* Toggle notifications */ }) {
//                Icon(
//                    imageVector = Icons.Default.Notifications,
//                    contentDescription = "Notificaciones"
//                )
//            }
//        }

        Spacer(modifier = Modifier.height(24.dp))

        // Balance Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Balance",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Gray
                )
                Text(
                    text = "$${String.format("%,.2f", balance)}",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = { /* Toggle balance visibility */ }) {
                    Icon(
                        imageVector = Icons.Default.Visibility,
                        contentDescription = "Toggle balance visibility"
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = { navController.navigate("transference")},
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF1A1B25)
                        )
                    ) {
                        Text("Transferir")
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                    Button(
                        onClick = { /* Handle deposit */ },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF1A1B25)
                        )
                    ) {
                        Text("Ingresar")
                        Icon(
                            imageVector = Icons.Default.ArrowDownward,
                            contentDescription = null,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        var showDialog by remember { mutableStateOf(false) }

        if (showDialog) {
            UserDataDialog(
                onDismiss = { showDialog = false },
                userData = UserData(
                    fullName = "Juan Rodriguez",
                    alias = "perro.overo.bien.cl",
                    cbu = "0000120043456552634343",
                    cuit = "20-20979631-9"
                )
            )
        }
        // Quick Actions
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                QuickActionButton(
                    icon = Icons.Default.Link,
                    text = "Link de pago"
                )
                QuickActionButton(
                    onClick = {showDialog = ! showDialog},
                    icon = Icons.Default.Person,
                    text = "Tus datos"
                )
                QuickActionButton(
                    icon = Icons.Default.PersonAdd,
                    text = "Añadir contacto"
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Recent Movements
        Text(
            text = "Movimientos",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        LazyColumn {
            items(3) { index ->
                when (index) {
                    0 -> TransactionItem(
                        name = "Jose",
                        description = "Te transfirió $10,000",
                        time = "Ahora",
                        showAvatar = true
                    )
                    1 -> TransactionItem(
                        name = "Open 25",
                        description = "Pagaste $1000",
                        time = "15m",
                        showLogo = true
                    )
                    2 -> TransactionItem(
                        name = "Fer Galan",
                        description = "Enviaste $3,000",
                        time = "6h",
                        showAvatar = true
                    )
                }
            }
        }
    }
}

@Composable
private fun QuickActionButton(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit = {}
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFF6C63FF),
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                tint = Color.White,
                modifier = Modifier.padding(12.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun TransactionItem(
    name: String,
    description: String,
    time: String,
    showAvatar: Boolean = false,
    showLogo: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            when {
                showAvatar -> {
                    Surface(
                        shape = CircleShape,
                        color = Color.LightGray,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
                showLogo -> {
                    Surface(
                        shape = CircleShape,
                        modifier = Modifier.size(40.dp)
                    ) {
                        // You would typically use an Image composable here with your logo
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Red)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
                Text(
                    text = "Detalles",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF6C63FF)
                )
            }
        }
        Text(
            text = time,
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )
    }
}

@Composable
fun UserDataDialog(
    onDismiss: () -> Unit,
    userData: UserData = UserData(
        fullName = "Juan Rodriguez",
        alias = "perro.overo.bien.cl",
        cbu = "0000120043456552634343",
        cuit = "20-20979631-9"
    )
) {
    val clipboardManager = LocalClipboardManager.current

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header
                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Tus Datos",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.align(Alignment.Center)
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.align(Alignment.CenterEnd)
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.Black
                        )
                    }
                }

                // Data Fields
                DataField(
                    label = "Nombre y Apellido",
                    value = userData.fullName,
                    canEdit = true,
                    onCopy = { clipboardManager.setText(AnnotatedString(userData.fullName)) }
                )

                DataField(
                    label = "Alias",
                    value = userData.alias,
                    canEdit = true,
                    onCopy = { clipboardManager.setText(AnnotatedString(userData.alias)) }
                )

                DataField(
                    label = "CBU",
                    value = userData.cbu,
                    canEdit = false,
                    onCopy = { clipboardManager.setText(AnnotatedString(userData.cbu)) }
                )

                DataField(
                    label = "CUIT",
                    value = userData.cuit,
                    canEdit = false,
                    onCopy = { clipboardManager.setText(AnnotatedString(userData.cuit)) }
                )

                // Share Button
                Button(
                    onClick = { /* Handle sharing */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF6C63FF)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        "COMPARTIR",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun DataField(
    label: String,
    value: String,
    canEdit: Boolean,
    onCopy: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFF5F5F5))
                .padding(12.dp)
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Black,
                modifier = Modifier.align(Alignment.CenterStart)
            )

            Row(
                modifier = Modifier.align(Alignment.CenterEnd),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (canEdit) {
                    IconButton(
                        onClick = { /* Handle edit */ },
                        modifier = Modifier.size(20.dp)
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = Color.Gray
                        )
                    }
                }

                IconButton(
                    onClick = onCopy,
                    modifier = Modifier.size(20.dp)
                ) {
                    Icon(
                        Icons.Default.ContentCopy,
                        contentDescription = "Copy",
                        tint = Color.Gray
                    )
                }
            }
        }
    }
}

data class UserData(
    val fullName: String,
    val alias: String,
    val cbu: String,
    val cuit: String
)

@Preview(showBackground = true)
@Composable
fun UserDataDialogPreview() {
    MaterialTheme {
        UserDataDialog(
            onDismiss = {}
        )
    }
}