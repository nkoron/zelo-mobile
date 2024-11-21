package com.example.zelo.transference

import android.content.Context
import android.provider.ContactsContract
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import com.example.zelo.R
import com.google.accompanist.permissions.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransferScreen(
    onNavigateBack: () -> Unit = {},
    onNavigateToForm: () -> Unit = {},
    onNavigateToContacts: () -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    val context = LocalContext.current
    var contacts by remember { mutableStateOf(emptyList<String>()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Action Buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = onNavigateToForm,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6B6B7B))
            ) {
                Text("EMAIL", textAlign = TextAlign.Center, fontSize = 12.sp)
            }
            Button(
                onClick = onNavigateToContacts,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6B6B7B))
            ) {
                Text(
                    stringResource(R.string.contacts),
                    textAlign = TextAlign.Center,
                    fontSize = 12.sp
                )
            }
        }

        // Search Bar
        TextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.medium),
            placeholder = { Text(stringResource(R.string.search)) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            trailingIcon = { Icon(Icons.Default.FilterList, contentDescription = "Filter") },
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color(0xFFF3F0F7)
            ),
            singleLine = true
        )

    }
}