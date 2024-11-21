package com.example.zelo.cards

import android.app.DatePickerDialog
import android.icu.util.Calendar
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.zelo.MyApplication
import com.example.zelo.R
import com.example.zelo.network.model.Card
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardsScreen(
    onBack: () -> Unit,
    viewModel: CardsViewModel = viewModel(
        factory = CardsViewModel.provideFactory(LocalContext.current.applicationContext as MyApplication)
    )
) {
    val uiState by viewModel.uiState.collectAsState()
    val configuration = LocalConfiguration.current
    val isTablet = configuration.screenWidthDp >= 600
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

    var resetKey by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        viewModel.loadCards()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.my_cards)) },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.semantics { contentDescription = "Navigate back" }
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                when {
                    uiState.isLoading -> {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .semantics { contentDescription = "Loading cards" }
                        )
                    }
                    uiState.error != null -> {
                        Text(
                            text = uiState.error?.message ?: stringResource(R.string.unknown_error),
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    else -> {
                        CardsList(
                            cards = uiState.cards,
                            onDeleteCard = { card ->
                                viewModel.showDeleteConfirmation(card)
                            },
                            isTablet = isTablet,
                            isLandscape = isLandscape,
                            resetKey = resetKey
                        )
                    }
                }
            }

            AddCardButton(
                onAddCard = { card ->
                    viewModel.addCard(card)
                },
                isTablet = isTablet,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
        }
    }

    // Delete confirmation dialog
    uiState.cardToDelete?.let { card ->
        AlertDialog(
            onDismissRequest = {
                viewModel.dismissDeleteConfirmation()
                resetKey++ // Increment the reset key to trigger a recomposition
            },
            title = { Text("Confirm Deletion") },
            text = { Text("Are you sure you want to delete this card?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        card.id?.let { viewModel.deleteCard(it) }
                        viewModel.dismissDeleteConfirmation()
                        resetKey++
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        viewModel.dismissDeleteConfirmation()
                        resetKey++ // Increment to trigger LaunchedEffect and reset swipe state
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun CardsList(
    cards: List<Card>,
    onDeleteCard: (Card) -> Unit,
    isTablet: Boolean,
    isLandscape: Boolean,
    resetKey: Any
) {
    if (cards.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.no_cards_message),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
        }
    } else if (!isTablet || (isTablet && !isLandscape)) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(items = cards, key = { it.id ?: 0 }) { card ->
                CreditCard(card = card, onDelete = onDeleteCard, isTablet = isTablet, resetKey = resetKey)
            }
        }
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(items = cards, key = { it.id ?: 0 }) { card ->
                CreditCard(card = card, onDelete = onDeleteCard, isTablet = isTablet, resetKey = resetKey)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreditCard(
    card: Card,
    onDelete: (Card) -> Unit,
    isTablet: Boolean,
    resetKey: Any
) {
    var isDeleting by remember { mutableStateOf(false) }

    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { dismissValue ->
            when (dismissValue) {
                SwipeToDismissBoxValue.EndToStart -> {
                    isDeleting = true
                    onDelete(card)
                    false // Don't dismiss immediately, wait for the delete confirmation
                }
                SwipeToDismissBoxValue.StartToEnd -> false
                SwipeToDismissBoxValue.Settled -> {
                    isDeleting = false
                    true
                }
            }
        },
        positionalThreshold = { totalDistance -> totalDistance * 0.5f }
    )

    // Reset dismiss state when resetKey changes
    LaunchedEffect(resetKey) {
        dismissState.reset()
        isDeleting = false
    }

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            val color by animateColorAsState(
                when (dismissState.targetValue) {
                    SwipeToDismissBoxValue.Settled -> Color.White
                    SwipeToDismissBoxValue.EndToStart -> Color.Red
                    else -> Color.White
                }
            )
            val scale by animateFloatAsState(
                if (dismissState.targetValue == SwipeToDismissBoxValue.Settled) 0.75f else 1f
            )
            Box(
                Modifier
                    .fillMaxSize()
                    .background(color)
                    .padding(horizontal = 20.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = stringResource(R.string.delete),
                    modifier = Modifier.scale(scale)
                )
            }
        },
        content = {
            CardItem(card, onDelete, isTablet)
        },
        enableDismissFromStartToEnd = false,
        enableDismissFromEndToStart = !isDeleting
    )
}

@Composable
fun CardItem(card: Card, onDelete: (Card) -> Unit, isTablet: Boolean) {
    var showDetails by remember { mutableStateOf(false) }
    val bankName = remember(card.number) { inferBankName(card.number) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF6C63FF))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Column {
                Text(
                    text = bankName,
                    color = Color.White,
                    fontSize = 24.sp
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = if (showDetails) card.number else "**** **** **** ${card.number.takeLast(4)}",
                    color = Color.White,
                    fontSize = 18.sp
                )
                Text(
                    text = "Expires: ${card.expirationDate}",
                    color = Color.White,
                    fontSize = 14.sp
                )
                Text(
                    text = card.fullName,
                    color = Color.White,
                    fontSize = 14.sp
                )
                Text(
                    text = card.type,
                    color = Color.White,
                    fontSize = 14.sp
                )
            }
            Row(
                modifier = Modifier.align(Alignment.TopEnd),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    onClick = { showDetails = !showDetails }
                ) {
                    Icon(
                        imageVector = if (!showDetails) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = if (showDetails) stringResource(R.string.hide_details) else stringResource(R.string.show_details),
                        tint = Color.White
                    )
                }
                if (isTablet) {
                    IconButton(
                        onClick = { onDelete(card) }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = stringResource(R.string.delete),
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCardButton(onAddCard: (Card) -> Unit, isTablet: Boolean, modifier: Modifier = Modifier) {
    var showAddCardDialog by remember { mutableStateOf(false) }

    Button(
        onClick = { showAddCardDialog = true },
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6C63FF)),
        modifier = modifier
    ) {
        Icon(imageVector = Icons.Default.Add, contentDescription = null)
        Spacer(modifier = Modifier.width(8.dp))
        Text(stringResource(R.string.add_card))
    }

    if (showAddCardDialog) {
        AddCardDialog(
            onDismiss = { showAddCardDialog = false },
            onAddCard = { number, expirationDate, fullName, type, cvv ->
                onAddCard(
                    Card(
                        id = null, // Temporary ID, will be replaced by the backend
                        number = number,
                        expirationDate = expirationDate,
                        fullName = fullName,
                        type = type,
                        cvv = cvv,
                        createdAt = null,
                        updatedAt = null
                    )
                )
                showAddCardDialog = false
            },
            isTablet = isTablet
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCardDialog(
    onDismiss: () -> Unit,
    onAddCard: (String, String, String, String, String) -> Unit,
    isTablet: Boolean
) {
    var number by remember { mutableStateOf("") }
    var expirationDate by remember { mutableStateOf(LocalDate.now()) }
    var fullName by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("CREDIT") }
    var cvv by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }

    val dateFormatter = remember { DateTimeFormatter.ofPattern("MM/yy") }
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.add_new_card)) },
        text = {
            Column {
                if (isTablet) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            OutlinedTextField(
                                value = number,
                                onValueChange = { if (it.length <= 16) number = it },
                                label = { Text(stringResource(R.string.card_number)) },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = expirationDate.format(dateFormatter),
                                onValueChange = { },
                                label = { Text(stringResource(R.string.expiration_date)) },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                readOnly = true,
                                trailingIcon = {
                                    IconButton(onClick = { showDatePicker = true }) {
                                        Icon(Icons.Default.DateRange, contentDescription = "Select date")
                                    }
                                }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = fullName,
                                onValueChange = { fullName = it },
                                label = { Text(stringResource(R.string.full_name))
                                },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            ExposedDropdownMenuBox(
                                expanded = expanded,
                                onExpandedChange = { expanded = !expanded },
                            ) {
                                OutlinedTextField(
                                    value = type,
                                    onValueChange = { },
                                    label = { Text(stringResource(R.string.card_type)) },
                                    singleLine = true,
                                    readOnly = true,
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                                    modifier = Modifier.menuAnchor().fillMaxWidth()
                                )
                                ExposedDropdownMenu(
                                    expanded = expanded,
                                    onDismissRequest = { expanded = false },
                                ) {
                                    DropdownMenuItem(
                                        text = { Text("CREDIT") },
                                        onClick = {
                                            type = "CREDIT"
                                            expanded = false
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("DEBIT") },
                                        onClick = {
                                            type = "DEBIT"
                                            expanded = false
                                        }
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = cvv,
                                onValueChange = { if (it.length <= 3) cvv = it },
                                label = { Text(stringResource(R.string.cvv)) },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                } else {
                    OutlinedTextField(
                        value = number,
                        onValueChange = { if (it.length <= 16) number = it },
                        label = { Text(stringResource(R.string.card_number)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = expirationDate.format(dateFormatter),
                        onValueChange = { },
                        label = { Text(stringResource(R.string.expiration_date)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true,
                        trailingIcon = {
                            IconButton(onClick = { showDatePicker = true }) {
                                Icon(Icons.Default.DateRange, contentDescription = "Select date")
                            }
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        label = { Text(stringResource(R.string.full_name)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded },
                    ) {
                        OutlinedTextField(
                            value = type,
                            onValueChange = { },
                            label = { Text(stringResource(R.string.card_type)) },
                            singleLine = true,
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                        ) {
                            DropdownMenuItem(
                                text = { Text("CREDIT") },
                                onClick = {
                                    type = "CREDIT"
                                    expanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("DEBIT") },
                                onClick = {
                                    type = "DEBIT"
                                    expanded = false
                                }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = cvv,
                        onValueChange = { if (it.length <= 3) cvv = it },
                        label = { Text(stringResource(R.string.cvv)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (number.isNotEmpty() && fullName.isNotEmpty() && type.isNotEmpty()) {
                        onAddCard(number, expirationDate.format(dateFormatter), fullName, type, cvv)
                    }
                }
            ) {
                Text(stringResource(R.string.add))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )

    if (showDatePicker) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            context,
            { _, year, month, _ ->
                expirationDate = LocalDate.of(year, month + 1, 1)
                showDatePicker = false
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }
}

fun inferBankName(cardNumber: String): String {
    return when {
        cardNumber.startsWith("4") -> "Visa"
        cardNumber.startsWith("5") -> "MasterCard"
        cardNumber.startsWith("34") || cardNumber.startsWith("37") -> "American Express"
        cardNumber.startsWith("6") -> "Discover"
        else -> "Unknown Bank"
    }
}