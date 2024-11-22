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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.zelo.MyApplication
import com.example.zelo.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountDataScreen(onBack: () -> Unit) {
    val viewModel: ProfileViewModel = viewModel( factory = ProfileViewModel.provideFactory( LocalContext.current.applicationContext as MyApplication)
    )
    val uiState = viewModel.uiState
    val name = buildString {  uiState.user?.firstName.let { append(it) }
                            uiState.user?.lastName.let { append(" $it") }}
    val email = uiState.user?.email ?: ""
    val birth = uiState.user?.birthDate ?: ""
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    // Cargar el valor inicial desde SharedPreferences
    var phone by remember {
        mutableStateOf(sharedPreferences.getString("phone", "+1234567890") ?: "+1234567890")}


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.account_data)) },
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
            Text(stringResource(R.string.account_changes))
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = name,
                onValueChange = {},
                label = { Text(stringResource(R.string.full_name)) },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = email,
                onValueChange = {},
                label = { Text(stringResource(R.string.email)) },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = birth,
                onValueChange = {},
                label = { Text(stringResource(R.string.birth_date)) },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text(stringResource(R.string.phone)) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                sharedPreferences.edit().putString("phone", phone).apply()
                onBack()}) {
                Text(stringResource(R.string.save_changes), color = Color.White)
            }

        }
    }
}