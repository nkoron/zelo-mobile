package com.example.zelo.contacts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactsScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {navController.navigate("transference")},
    onTransfer: (Contact) -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    var showFilters by remember { mutableStateOf(false) }

    val contacts = remember {
        listOf(
            Contact("Jose", "Rodriguez", "Amigo", true),
            Contact("Martin", "Garcia", "Familia", true),
            Contact("Miguel", "Lopez", "Trabajo", true),
            Contact("Juan", "Martinez", "Amigo", true),
            Contact("Ana", "Gonzalez", "Familia", false),
            Contact("Carlos", "Fernandez", "Trabajo", false),
            Contact("Laura", "Diaz", "Amigo", false),
            Contact("Pedro", "Sanchez", "Familia", false)
        )
    }

    val filteredContacts = contacts.filter {
        it.firstName.contains(searchQuery, ignoreCase = true) ||
                it.lastName.contains(searchQuery, ignoreCase = true)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Contactos") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black,
                    navigationIconContentColor = Color.Black
                )
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            // Search Bar
            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                placeholder = { Text("Buscar") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Search")
                },
                trailingIcon = {
                    IconButton(onClick = { showFilters = !showFilters }) {
                        Icon(Icons.Default.FilterList, contentDescription = "Filter")
                    }
                },
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color(0xFFF5F5F5),
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(24.dp)
            )

            if (showFilters) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = true,
                        onClick = { /* Handle filter */ },
                        label = { Text("Todos") }
                    )
                    FilterChip(
                        selected = false,
                        onClick = { /* Handle filter */ },
                        label = { Text("Familia") }
                    )
                    FilterChip(
                        selected = false,
                        onClick = { /* Handle filter */ },
                        label = { Text("Amigos") }
                    )
                    FilterChip(
                        selected = false,
                        onClick = { /* Handle filter */ },
                        label = { Text("Trabajo") }
                    )
                }
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredContacts) { contact ->
                    ContactItem(
                        contact = contact,
                        onTransfer = { onTransfer(contact) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ContactItem(
    contact: Contact,
    onTransfer: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (contact.hasCustomAvatar) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF6C63FF)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = contact.firstName.first().toString(),
                        color = Color.White,
                        fontSize = 18.sp
                    )
                }
            } else {
                Icon(
                    Icons.Default.AccountCircle,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = Color.Gray
                )
            }

            Column {
                Text(
                    text = "${contact.firstName} ${contact.lastName}",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = contact.group,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }

        Button(
            onClick = onTransfer,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF6C63FF)
            ),
            contentPadding = PaddingValues(horizontal = 16.dp),
            modifier = Modifier.height(36.dp)
        ) {
            Text("Transferir")
        }
    }
}

data class Contact(
    val firstName: String,
    val lastName: String,
    val group: String,
    val hasCustomAvatar: Boolean
)
