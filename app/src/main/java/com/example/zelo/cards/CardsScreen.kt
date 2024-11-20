package com.example.zelo.cards

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.example.zelo.R

data class CreditCard(
    val id: Int,
    val bank: String,
    val lastFourDigits: String,
    val color: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardsScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    onBackPress: () -> Unit = { navController.popBackStack() }
) {
    val cards = remember {
        mutableStateListOf(
            CreditCard(1, "Brubank", "1890", Color(0xFF8B5CF6)),
            CreditCard(2, "Visa", "0000", Color(0xFF3B82F6)),
            CreditCard(3, "Mastercard", "1890", Color(0xFFEF4444)),
            CreditCard(4, "Visa", "8560", Color(0xFF10B981))
        )
    }

    val listState = rememberLazyListState()
    var showAddCardDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.Start,  // Aligns items to the start (left)
            verticalAlignment = Alignment.CenterVertically // Aligns items vertically in the center
        ) {
            // Arrow icon on the left
            IconButton(onClick = onBackPress) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.back),
                    tint = Color.Black
                )
            }

            // Text aligned vertically to the center of the Row
            Text(
                text = stringResource(R.string.my_cards),
                fontSize = 24.sp,
                modifier = Modifier
                    .padding(start = 75.dp) // Adds space between the icon and the text
                    .align(Alignment.CenterVertically) // Aligns text vertically with the icon
            )
        }

        if(LocalContext.current.resources.configuration.screenWidthDp <= 600){
            LazyColumn(
                state = listState,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(items = cards, key = { it.id }) { card ->
                    creditCard(cards, card)
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(1f)
            ){
                items(items = cards, key = { it.id }) { card ->
                    creditCard(cards, card)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { showAddCardDialog = true },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF6C63FF)
            ) ,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = stringResource(R.string.add_card)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(stringResource(R.string.add_card))
        }
    }

    if (showAddCardDialog) {
        AddCardDialog(
            onDismiss = { showAddCardDialog = false },
            onAddCard = { bank, lastFourDigits ->
                val newId = cards.maxByOrNull { it.id }?.id?.plus(1) ?: 1
                val newCard = CreditCard(newId, bank, lastFourDigits, Color.random())
                cards.add(newCard)
                showAddCardDialog = false
            }
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun creditCard(
    cards: SnapshotStateList<CreditCard>,
    card: CreditCard
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { dismissValue ->
            if (dismissValue == SwipeToDismissBoxValue.EndToStart) {
                cards.remove(card)
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
fun CardItem(card: CreditCard) {
    var showDetails by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        colors = CardDefaults.cardColors(containerColor = card.color)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Column {
                Text(
                    text = card.bank,
                    color = Color.White,
                    fontSize = 24.sp
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = if (showDetails) "**** **** **** ${card.lastFourDigits}" else "**** **** **** ****",
                    color = Color.White,
                    fontSize = 18.sp
                )
            }
            IconButton(
                onClick = { showDetails = !showDetails },
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                Icon(
                    imageVector = if (showDetails) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                    contentDescription = if (showDetails) stringResource(R.string.hide_details) else stringResource(R.string.show_details),
                    tint = Color.White
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCardDialog(
    onDismiss: () -> Unit,
    onAddCard: (String, String) -> Unit
) {
    var bank by remember { mutableStateOf("") }
    var lastFourDigits by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.add_new_card)) },
        text = {
            Column {
                TextField(
                    value = bank,
                    onValueChange = { bank = it },
                    label = { Text(stringResource(R.string.bank)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = lastFourDigits,
                    onValueChange = { if (it.length <= 4) lastFourDigits = it },
                    label = { Text(stringResource(R.string.last_four_digits)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (bank.isNotBlank() && lastFourDigits.length == 4) {
                        onAddCard(bank, lastFourDigits)
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

fun Color.Companion.random(): Color {
    return Color(
        red = (0..255).random(),
        green = (0..255).random(),
        blue = (0..255).random()
    )
}