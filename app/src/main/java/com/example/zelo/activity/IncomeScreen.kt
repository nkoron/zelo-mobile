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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.zelo.MyApplication
import com.example.zelo.R
import com.example.zelo.components.ZeloSearchBar


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IncomeScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: IncomeViewModel = viewModel(factory = IncomeViewModel.provideFactory(
        LocalContext.current.applicationContext as MyApplication
    ))
) {
    val uiState by viewModel.uiState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    val contacts = listOf("Jose", "Martin", "Miguel", "Juan")

    Scaffold(
        topBar = {
            TopAppBar(modifier= Modifier.padding(5.dp), title = { Text(stringResource( R.string.incomes)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack()}) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription =stringResource( R.string.back) )
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
                stringResource( R.string.recent_incomes),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 16.dp)
            )
            ZeloSearchBar(searchQuery= searchQuery, valueChange = { searchQuery = it })

            Spacer(modifier = Modifier.height(24.dp))

            LazyColumn {
                items(uiState.movements.size) {
                    val payment = uiState.movements[it]
                    TransactionItem(
                        name = "${payment.receiver.firstName} ${payment.receiver.lastName}",
                        description = "${stringResource(R.string.transferred)}: ${payment.amount}",
                        time = payment.createdAt,
                        showAvatar = true
                    )
                }
            }
        }
    }

}
