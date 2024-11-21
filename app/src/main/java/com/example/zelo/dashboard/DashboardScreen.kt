import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.zelo.MyApplication
import com.example.zelo.R
import com.example.zelo.dashboard.DashboardUiState
import com.example.zelo.dashboard.DashboardViewModel
import com.example.zelo.dashboard.PaymentLinkDialog
import com.example.zelo.transference.TransferDetailsDialog


@Composable
fun DashboardScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: DashboardViewModel = viewModel(factory = DashboardViewModel.provideFactory(LocalContext.current.applicationContext as MyApplication))
) {
    val uiState by viewModel.uiState.collectAsState()
    val configuration = LocalConfiguration.current
    val isTablet = configuration.screenWidthDp >= 600 && configuration.screenHeightDp >=600
    val isLandscape = configuration.screenWidthDp > 600 && configuration.screenHeightDp < 600 && !isTablet


    Box(modifier = modifier.fillMaxSize()) {
        if (uiState.isFetching) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else {
            when {
                isTablet -> TabletDashboardContent(
                    uiState = uiState,
                    navController = navController,
                    onLogin = viewModel::login
                )
                isLandscape -> LandscapeDashboardContent(
                    uiState = uiState,
                    navController = navController,
                    onLogin = viewModel::login
                )
                else -> PhoneDashboardContent(
                    uiState = uiState,
                    navController = navController,
                    onLogin = viewModel::login
                )
            }
        }
    }
}

@Composable
private fun LandscapeDashboardContent(
    uiState: DashboardUiState,
    navController: NavController,
    onLogin: (String, String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(
            modifier = Modifier
                .width(300.dp)
                .fillMaxHeight()
                .verticalScroll(rememberScrollState())
        ) {
            BalanceCard(
                balance = uiState.walletDetail?.balance,
                navController = navController,
            )
            Spacer(modifier = Modifier.height(16.dp))
            QuickActions { onLogin("landscape@example.com", "1234567890") }
        }
        RecentMovementsFullScreen()
    }
}

@Composable
private fun RecentMovementsFullScreen() {
    Card(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Text(
                text = stringResource(R.string.transactions),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(16.dp)
            )
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                items(20) { index ->
                    TransactionItem(
                        name = "Transaction ${index + 1}",
                        description = "Description for transaction ${index + 1}",
                        time = "${index + 1}h ago",
                        showAvatar = index % 2 == 0,
                        showLogo = index % 2 != 0
                    )
                    if (index < 19) {
                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun TabletDashboardContent(
    uiState: DashboardUiState,
    navController: NavController,
    onLogin: (String, String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(
            modifier = Modifier
                .width(300.dp)
                .fillMaxHeight()
        ) {
            BalanceCard(
                balance = uiState.walletDetail?.balance,
                navController = navController,
            )
            Spacer(modifier = Modifier.height(24.dp))
            QuickActions { onLogin("ocie.collins86@ethereal.email", "1234567890") }
        }
        RecentMovementsFullScreen()
    }
}

@Composable
private fun PhoneDashboardContent(
    uiState: DashboardUiState,
    navController: NavController,
    onLogin: (String, String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(16.dp)
    ) {
        BalanceCard(
            balance = uiState.walletDetail?.balance,
            navController = navController,
        )
        Spacer(modifier = Modifier.height(24.dp))
        QuickActions { onLogin("johndoe@email.com", "1234567890") }
        Spacer(modifier = Modifier.height(24.dp))
        RecentMovements()
    }
}

@Composable
private fun RecentMovements() {
    Card(
        modifier =  Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Text(
            text = stringResource(R.string.transactions),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(8.dp)
        )
        LazyColumn {
            items(3) { index ->
                when (index) {
                    0 -> TransactionItem(
                        name = "Jose",
                        description = stringResource(R.string.transferred) + " $10000",
                        time = "Ahora",
                        showAvatar = true
                    )
                    1 -> TransactionItem(
                        name = "Open 25",
                        description = stringResource(R.string.sent) + "$1000",
                        time = "15m",
                        showLogo = true
                    )
                    2 -> TransactionItem(
                        name = "Fer Galan",
                        description = stringResource(R.string.sent) + "$1000",
                        time = "6h",
                        showAvatar = true
                    )
                }
            }
        }
    }
}
@Composable
private fun QuickActions(
    login: ()->Unit,
) {
    var showDialog by remember { mutableStateOf(false) }
    var showPaymentLink by remember { mutableStateOf(false) }
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
    if (showPaymentLink) {
        PaymentLinkDialog(
            onDismiss = { showPaymentLink = false },
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
                onClick = { showPaymentLink = !showPaymentLink },
                icon = Icons.Default.Link,
                text = stringResource(R.string.payment_link)
            )
            QuickActionButton(
                onClick = { showDialog = !showDialog },
                icon = Icons.Default.Person,
                text = stringResource(R.string.your_info)
            )
            QuickActionButton(
                onClick = login,
                icon = Icons.Default.PersonAdd,
                text = stringResource(R.string.contacts)
            )
        }
    }
}

@Composable
private fun BalanceCard(balance: Double?, navController: NavController) {
    Card(
        modifier =  Modifier.fillMaxWidth()
        .padding(vertical = 16.dp),
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
                    contentDescription = stringResource(R.string.balance_visibility)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = { navController.navigate("transference") },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1A1B25)
                    )
                ) {
                    Text(stringResource(R.string.transfer))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
                Button(
                    onClick = { navController.navigate("home/deposit") },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1A1B25)
                    )
                ) {
                    Text(stringResource(R.string.deposit))
                    Icon(
                        imageVector = Icons.Default.ArrowDownward,
                        contentDescription = null,
                        modifier = Modifier.padding(start = 8.dp)
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

    var showDetailsDialog by remember { mutableStateOf(false) }

    if(showDetailsDialog) {
        TransferDetailsDialog(
            onDismiss = { showDetailsDialog = false },
            onRepeatTransfer = { /* Handle repeat transfer */ },
            onViewReceipt = { /* Handle view receipt */ },
            onRequestRefund = { /* Handle refund request */ }
        )
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
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
                    text = stringResource(R.string.see_details),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF6C63FF),
                    modifier = Modifier.clickable { showDetailsDialog =! showDetailsDialog }
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
                        text = stringResource(R.string.your_info),
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.align(Alignment.Center)
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.align(Alignment.CenterEnd)
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = stringResource(R.string.close),
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
                        stringResource(R.string.share),
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
                            contentDescription = stringResource(R.string.edit),
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
                        contentDescription = stringResource(R.string.copy),
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