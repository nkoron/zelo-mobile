package com.example.zelo.activity

import TransactionItem
import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpensesScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    var searchQuery by remember { mutableStateOf("") }
    val contacts = listOf("Jose", "Martin", "Miguel", "Juan")

    Scaffold(
        topBar = {
            TopAppBar(modifier= Modifier.padding(5.dp), title = { Text("Gastado") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack()}) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                "Tus gastos recientes",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 16.dp)
            )


            // Search Bar
            val containerColor = Color(0xFFF3F0F7)
            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp)),
                placeholder = { Text("Buscar") },
                leadingIcon = { Icon(Icons.Default.Search, "Search") },
                trailingIcon = { Icon(Icons.Default.FilterList, "Filter") },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = containerColor,
                    unfocusedContainerColor = containerColor,
                    disabledContainerColor = containerColor,
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            LazyColumn {
                items(4) { index ->
                    when (index) {
                        0 -> TransactionItem(
                            name = "Jose",
                            description = "Enviaste $10,000",
                            time = "Ahora",
                            showAvatar = true
                        )
                        1 -> TransactionItem(
                            name = "Fer Galan",
                            description = "Enviaste $3,000",
                            time = "6h",
                            showAvatar = true
                        )
                        2 -> TransactionItem(
                            name = "Carlos GPT",
                            description = "Enviaste $3,000",
                            time = "2h",
                            showAvatar = true
                        )
                        3 -> TransactionItem(
                            name = "Miguel Cero",
                            description = "Enviaste $3,000",
                            time = "Ahora",
                            showAvatar = true
                        )
                    }
                }
            }
        }
    }

}
