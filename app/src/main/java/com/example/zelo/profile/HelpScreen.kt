package com.example.zelo.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.example.zelo.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpScreen(onBack: () -> Unit) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(stringResource(R.string.how_can_we_help))
            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = { /* Open FAQ */ }) {
                Text(stringResource(R.string.faq), color = Color.White)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = { /* Open chat support */ }) {
                Text(stringResource(R.string.chat), color = Color.White)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = { /* Make a call */ }) {
                Text(stringResource(R.string.call_att),color = Color.White)
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(onClick = { /* Report a problem */ }) {
                Text(stringResource(R.string.report_issue), color = MaterialTheme.colorScheme.primary)
            }

            OutlinedButton (onClick = { /* View terms and conditions */ }) {
                Text(stringResource(R.string.terms_and_conditions), color = MaterialTheme.colorScheme.primary)
            }
        }

}