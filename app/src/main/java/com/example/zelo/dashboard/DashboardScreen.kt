import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.provider.ContactsContract
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.zelo.MyApplication
import com.example.zelo.R
import com.example.zelo.dashboard.DashboardUiState
import com.example.zelo.dashboard.DashboardViewModel
import com.example.zelo.dashboard.generateQRCode
import com.example.zelo.network.model.Payment
import com.example.zelo.network.model.User
import com.example.zelo.transference.TransferDetailsDialog
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit


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
                )
                isLandscape -> LandscapeDashboardContent(
                    uiState = uiState,
                    navController = navController,
                )
                else -> PhoneDashboardContent(
                    uiState = uiState,
                    navController = navController,
                )
            }
        }
    }
}

@Composable
private fun LandscapeDashboardContent(
    uiState: DashboardUiState,
    navController: NavController,
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
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

        }
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(top = 16.dp)

        ){
            QuickActions(uiState)
            RecentMovementsFullScreen(uiState, viewModel())
        }
    }
}

@Composable
private fun RecentMovementsFullScreen(uiState: DashboardUiState, viewModel: DashboardViewModel) {
    Card(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.transactions),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.padding(12.dp)
            )

            // Check if there are no movements
            if (uiState.movements.isEmpty()) {
                // Display "No recent transactions" if no movements
                Text(
                    text = stringResource(R.string.no_recent_transactions),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            } else {
                // Display the list of transactions if there are movements
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    items(if (uiState.movements.size > 10) 10 else uiState.movements.size) {
                        val payment = uiState.movements[it]
                        val me: User?;
                        val you: User?;
                        val receive: Boolean
                        if (payment.receiver.id == uiState.user?.id) {
                            receive = true
                            me = payment.receiver
                            you = payment.payer
                        } else {
                            receive = false
                            me = payment.payer
                            you = payment.receiver
                        }
                        TransactionItem(
                            name = "${you?.firstName} ${you?.lastName}",
                            description = "${
                                if (receive) stringResource(R.string.transferred) else stringResource(
                                    R.string.sent
                                )
                            }: ${payment.amount}",
                            time = payment.createdAt,
                            showAvatar = true,
                            movements = uiState.movements,
                            id = it,
                            isPayer = !receive
                        )
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
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
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
            QuickActions(uiState)
        }
        RecentMovementsFullScreen(uiState, viewModel() )
    }
}

@Composable
private fun PhoneDashboardContent(
    uiState: DashboardUiState,
    navController: NavController,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        BalanceCard(
            balance = uiState.walletDetail?.balance,
            navController = navController,
        )
        Spacer(modifier = Modifier.height(24.dp))
        QuickActions(uiState)
        Spacer(modifier = Modifier.height(24.dp))
        RecentMovements(uiState, viewModel())
    }
}

@Composable
private fun RecentMovements(uiState: DashboardUiState, viewModel: DashboardViewModel) {
    var flag = false
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.transactions),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.padding(12.dp)
            )
            if (uiState.movements.isEmpty()) {
                // Display "No recent transactions" if no movements
                Text(
                    text = stringResource(R.string.no_recent_transactions),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.6f)
                )
            } else {
                // Display the list of transactions if there are movements
                LazyColumn {
                    items(if (uiState.movements.size > 10) 10 else uiState.movements.size) {
                        val payment = uiState.movements[it]
                        val me: User?
                        val you: User?
                        val receive: Boolean
                        if (payment.receiver.id == uiState.user?.id) {
                            receive = true
                            me = payment.receiver
                            you = payment.payer
                        } else {
                            receive = false
                            me = payment.payer
                            you = payment.receiver
                        }
                        TransactionItem(
                            name = "${you?.firstName} ${you?.lastName}",
                            description = "${
                                if (receive) stringResource(R.string.transferred) else stringResource(
                                    R.string.sent
                                )
                            }: ${payment.amount}",
                            time = payment.createdAt,
                            showAvatar = true,
                            movements = uiState.movements,
                            id = it,
                            isPayer = !receive
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun QuickActions(uiState: DashboardUiState
) {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    var taxId by remember { mutableStateOf(sharedPreferences.getString("taxId", "") ?: "") }
    var showDialog by remember { mutableStateOf(false) }
    var showPaymentLink by remember { mutableStateOf(false) }
    var showQRDialog by remember { mutableStateOf(false) }
    if (showDialog) {
        UserDataDialog(
            onDismiss = { showDialog = false },
            userData = UserData(
                fullName = uiState.user?.firstName + " " + uiState.user?.lastName,
                alias = uiState.walletDetail?.alias,
                cbu = uiState.walletDetail?.cbu,
                cuit = taxId
            )
        )
    }
    if (showPaymentLink) {
        PaymentLinkScreen(
            onDismiss = { showPaymentLink = false },
        )
    }
    if (showQRDialog) {
        QRCodeDialog(
            onDismiss = { showQRDialog = false },
            content = uiState.user?.email.toString()
        )
    }
    // Quick Actions
    Card(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.quick_actions),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.padding(3.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
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
                    onClick = {
                        val intent = Intent(Intent.ACTION_INSERT).apply {
                            type = ContactsContract.RawContacts.CONTENT_TYPE
                            putExtra(ContactsContract.Intents.Insert.NAME, "John Doe") // Optional
                            putExtra(ContactsContract.Intents.Insert.PHONE, "123456789") // Optional
                        }
                        context.startActivity(intent)
                    },
                    icon = Icons.Default.PersonAdd,
                    text = stringResource(R.string.contacts)
                )
                QuickActionButton(
                    onClick = { showQRDialog = !showQRDialog },
                    icon = Icons.Default.QrCode,
                    text = "QR"

                )
            }
        }
    }
}

@SuppressLint("DefaultLocale")
@Composable
private fun BalanceCard(balance: Double?, navController: NavController) {
    var isBalanceVisible by remember { mutableStateOf(false) } // Initial state: hidden

    Card(
        modifier = Modifier
            .fillMaxWidth()
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
                color = MaterialTheme.colorScheme.tertiary
            )
            Text(
                text = if (isBalanceVisible)
                    "$${String.format("%,.2f", balance)}"
                else "****",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = { isBalanceVisible = !isBalanceVisible }) {
                Icon(
                    imageVector = if (!isBalanceVisible) Icons.Default.VisibilityOff
                    else Icons.Default.Visibility,
                    contentDescription = stringResource(R.string.balance_visibility)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = { navController.navigate("home/transference") },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(text = stringResource(R.string.transfer), color = Color.White)
                    Icon(
                        imageVector = Icons.Default.ArrowUpward,
                        contentDescription = null,
                        modifier = Modifier.padding(start = 4.dp),
                        tint = Color.White
                    )
                }

                Button(
                    onClick = { navController.navigate("home/deposit") },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(text = stringResource(R.string.deposit), color = Color.White)
                    Icon(
                        imageVector = Icons.Default.ArrowDownward,
                        contentDescription = null,
                        modifier = Modifier.padding(start = 4.dp),
                        tint = Color.White
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
            color = MaterialTheme.colorScheme.primary,
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
    showLogo: Boolean = false,
    movements: List<Payment>,
    id: Int,
    isPayer: Boolean
) {

    var showDetailsDialog by remember { mutableStateOf(false) }

    if(showDetailsDialog) {
        TransferDetailsDialog(
            onDismiss = { showDetailsDialog = false },
            onRepeatTransfer = { /* Handle repeat transfer */ },
            onViewReceipt = { /* Handle view receipt */ },
            onRequestRefund = { /* Handle refund request */ },
            movements = movements,
            id = id,
            isPayer = isPayer
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
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable { showDetailsDialog =! showDetailsDialog }
                )
            }
        }
        Text(
            text = analyzeDate(time),
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )
    }
}
@Composable
fun analyzeDate(inputDate: String): String {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val date = LocalDate.parse(inputDate, formatter)
    val today = LocalDate.now()

    return when {
        date.isEqual(today) -> stringResource(R.string.today)
        date.isBefore(today) -> {
            val daysAgo = ChronoUnit.DAYS.between(date, today)
            "${daysAgo}D"
        }
        else -> throw IllegalArgumentException("La fecha ingresada es mayor a hoy")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDataDialog(
    onDismiss: () -> Unit,
    userData: UserData
) {
    val clipboardManager = LocalClipboardManager.current
    var showShareOptions by remember { mutableStateOf(false) }
    val isTablet = LocalConfiguration.current.screenWidthDp >= 600
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(R.string.your_info),
                color = MaterialTheme.colorScheme.primary // Use desired color
            )
        },
        text = {
            Column {
                if (isTablet) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            DataField(
                                label = stringResource(R.string.name_and_surname),
                                value = userData.fullName.toString(),
                                canEdit = false,
                                onCopy = { clipboardManager.setText(AnnotatedString(userData.fullName.toString())) }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            DataField(
                                label = "Alias",
                                value = userData.alias.toString(),
                                canEdit = true,
                                onCopy = { clipboardManager.setText(AnnotatedString(userData.alias.toString())) }
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            DataField(
                                label = "CBU",
                                value = userData.cbu.toString(),
                                canEdit = false,
                                onCopy = { clipboardManager.setText(AnnotatedString(userData.cbu.toString())) }
                            )
                        }
                    }
                } else {
                    DataField(
                        label = stringResource(R.string.name_and_surname),
                        value = userData.fullName.toString(),
                        canEdit = false,
                        onCopy = { clipboardManager.setText(AnnotatedString(userData.fullName.toString())) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    DataField(
                        label = "Alias",
                        value = userData.alias.toString(),
                        canEdit = true,
                        onCopy = { clipboardManager.setText(AnnotatedString(userData.alias.toString())) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    DataField(
                        label = "CBU",
                        value = userData.cbu.toString(),
                        canEdit = false,
                        onCopy = { clipboardManager.setText(AnnotatedString(userData.cbu.toString())) }
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { showShareOptions = true }
            ) {
                Text(stringResource(R.string.share))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.close))
            }
        }
    )

    if (showShareOptions) {
        ShareOptionsDialog(
            onDismiss = { showShareOptions = false },
            userData = userData
        )
    }
}

@Composable
fun DataField(
    label: String,
    value: String,
    canEdit: Boolean,
    onCopy: () -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = { },
        label = { Text(label) },
        singleLine = true,
        readOnly = !canEdit,
        modifier = Modifier.fillMaxWidth(),
        trailingIcon = {
            IconButton(onClick = onCopy) {
                Icon(Icons.Default.ContentCopy, contentDescription = stringResource(R.string.copy))
            }
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = MaterialTheme.colorScheme.tertiary,
            unfocusedTextColor = MaterialTheme.colorScheme.tertiary
        )
    )
}

@Composable
fun ShareOptionsDialog(
    onDismiss: () -> Unit,
    userData: UserData
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(R.string.share_options), color = MaterialTheme.colorScheme.tertiary) },
        text = {
            Column {
                ListItem(
                    headlineContent = { Text(text = stringResource(R.string.share_all_info), color = MaterialTheme.colorScheme.tertiary) },
                    leadingContent = { Icon(Icons.Default.Share, contentDescription = null) },
                    modifier = Modifier.clickable { /* Handle sharing all info */ }
                )
                ListItem(
                    headlineContent = { Text(text = stringResource(R.string.share_alias), color = MaterialTheme.colorScheme.tertiary) },
                    leadingContent = { Icon(Icons.Default.Link, contentDescription = null) },
                    modifier = Modifier.clickable { /* Handle sharing alias */ }
                )
                ListItem(
                    headlineContent = { Text(text = stringResource(R.string.share_cbu), color = MaterialTheme.colorScheme.tertiary) },
                    leadingContent = { Icon(Icons.Default.AccountBalance, contentDescription = null) },
                    modifier = Modifier.clickable { /* Handle sharing CBU */ }
                )
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}



@Composable
fun QRCodeDialog(
    onDismiss: () -> Unit,
    content: String
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                Image(
                    bitmap = generateQRCode(content).asImageBitmap(),
                    contentDescription = stringResource(R.string.qr_code),
                    modifier = Modifier
                        .size(200.dp)
                        .align(Alignment.CenterHorizontally)
                )
                Spacer(modifier = Modifier.height(16.dp))
                TextButton(onClick = onDismiss) {
                    Text(stringResource(R.string.cancel))
                }
            }
        }
    }
}



data class UserData(
    val fullName: String?,
    val alias: String?,
    val cbu: String?,
    val cuit: String?
)