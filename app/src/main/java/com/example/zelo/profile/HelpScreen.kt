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
fun HelpScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.help)) },
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
            Text(stringResource(R.string.how_can_we_help))
            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = { /* Open FAQ */ }) {
                Text(stringResource(R.string.faq))
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = { /* Open chat support */ }) {
                Text(stringResource(R.string.chat))
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = { /* Make a call */ }) {
                Text(stringResource(R.string.call_att))
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(onClick = { /* Report a problem */ }) {
                Text(stringResource(R.string.report_issue))
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = { /* View terms and conditions */ }) {
                Text(stringResource(R.string.terms_and_conditions))
            }
        }
    }
}