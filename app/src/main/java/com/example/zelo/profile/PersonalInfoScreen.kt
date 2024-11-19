package com.example.zelo.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.res.stringResource
import com.example.zelo.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalInfoScreen(navController: NavController) {
    var idNumber by remember { mutableStateOf("") }
    var taxId by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.your_info)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text(stringResource(R.string.id_info))
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = idNumber,
                onValueChange = { idNumber = it },
                label = { Text(stringResource(R.string.id_number)) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = taxId,
                onValueChange = { taxId = it },
                label = { Text(stringResource(R.string.fiscal_number)) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text(stringResource(R.string.address)) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = { /* Update personal information */ }) {
                Text(stringResource(R.string.update_info))
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(onClick = { /* View fiscal activity */ }) {
                Text(stringResource(R.string.see_fiscal))
            }
        }
    }
}