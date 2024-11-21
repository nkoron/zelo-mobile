package com.example.zelo.profile

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.example.zelo.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalInfoScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    // Cargar el valor inicial desde SharedPreferences
    var idNumber by remember { mutableStateOf(sharedPreferences.getString("idNumber", "") ?: "")}
    var taxId by remember { mutableStateOf(sharedPreferences.getString("taxId", "") ?: "")}
    var address by remember { mutableStateOf(sharedPreferences.getString("address", "") ?: "")}

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.your_info)) },
                navigationIcon = {
                    IconButton(onClick = { onBack() }) {
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

            Button(onClick = {  sharedPreferences.edit()
                .putString("idNumber", idNumber)
                .putString("taxId", taxId)
                .putString("address", address)
                .apply() // Usa commit() si necesitas asegurarte de que los datos estén guardados inmediatamente
                onBack()  }) {
                Text(stringResource(R.string.update_info))
            }

            Spacer(modifier = Modifier.height(16.dp))

//            OutlinedButton(onClick = {}) {
//                Text(stringResource(R.string.see_fiscal))
//            }
        }
    }
}