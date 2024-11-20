package com.example.zelo.cards

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.zelo.MyApplication
import com.example.zelo.R
import com.example.zelo.network.model.Card

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardsScreen(
    onBack: () -> Unit,
    viewModel: CardsViewModel = viewModel(
        factory = CardsViewModel.provideFactory(LocalContext.current.applicationContext as MyApplication)
    )
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadCards()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.my_cards)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
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
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
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
                            onDeleteCard = { viewModel.deleteCard(it.id ?: 0) }
                        )
                    }
                }
            }

            AddCardButton(
                onAddCard = { card ->
                    viewModel.addCard(card)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
        }
    }
}

@Composable
fun CardsList(
    cards: List<Card>,
    onDeleteCard: (Card) -> Unit
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
    } else if (LocalConfiguration.current.screenWidthDp <= 600) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(items = cards, key = { it.id ?: 0 }) { card ->
                CreditCard(card = card, onDelete = onDeleteCard)
            }
        }
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(items = cards, key = { it.id ?: 0 }) { card ->
                CreditCard(card = card, onDelete = onDeleteCard)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreditCard(card: Card, onDelete: (Card) -> Unit) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { dismissValue ->
            if (dismissValue == SwipeToDismissBoxValue.EndToStart) {
                onDelete(card)
                true
            } else false
        }
    )

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
            CardItem(card)
        },
        enableDismissFromStartToEnd = false,
        enableDismissFromEndToStart = true
    )
}

@Composable
fun CardItem(card: Card) {
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
            }
            IconButton(
                onClick = { showDetails = !showDetails },
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                Icon(
                    imageVector = if (!showDetails) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                    contentDescription = if (showDetails) stringResource(R.string.hide_details) else stringResource(R.string.show_details),
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
fun AddCardButton(onAddCard: (Card) -> Unit, modifier: Modifier = Modifier) {
    var showAddCardDialog by remember { mutableStateOf(false) }

    Button(
        onClick = { showAddCardDialog = true },
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6C63FF)),
        modifier = modifier
    ) {
        Icon(imageVector = Icons.Default.Add, contentDescription = stringResource(R.string.add_card))
        Spacer(modifier = Modifier.width(8.dp))
        Text(stringResource(R.string.add_card))
    }

    if (showAddCardDialog) {
        AddCardDialog(
            onDismiss = { showAddCardDialog = false },
            onAddCard = { number, expirationDate, fullName, type, cvv ->
                onAddCard(
                    Card(
                        id = null,
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
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCardDialog(
    onDismiss: () -> Unit,
    onAddCard: (String, String, String, String, String) -> Unit
) {
    var number by remember { mutableStateOf("") }
    var expirationDate by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("") }
    var cvv by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.add_new_card)) },
        text = {
            Column {
                TextField(
                    value = number,
                    onValueChange = { if (it.length <= 16) number = it },
                    label = { Text(stringResource(R.string.card_number)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = expirationDate,
                    onValueChange = { if (it.length <= 5) expirationDate = it },
                    label = { Text(stringResource(R.string.expiration_date)) },
                    placeholder = { Text("MM/YY") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text(stringResource(R.string.full_name)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = type,
                    onValueChange = { type = it },
                    label = { Text(stringResource(R.string.card_type)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = cvv,
                    onValueChange = { if (it.length <= 3) cvv = it },
                    label = { Text(stringResource(R.string.cvv)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (number.isNotEmpty() && expirationDate.isNotEmpty() && fullName.isNotEmpty() && type.isNotEmpty()) {
                        onAddCard(number, expirationDate, fullName, type, cvv)
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