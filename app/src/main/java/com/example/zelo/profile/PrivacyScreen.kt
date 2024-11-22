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

@Composable
fun PrivacyScreen(onBack: () -> Unit) {
    var dataSharing by remember { mutableStateOf(false) }
    var locationTracking by remember { mutableStateOf(false) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(stringResource(R.string.control_data))
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(stringResource(R.string.data_third_party))
                Switch(
                    checked = dataSharing,
                    onCheckedChange = { dataSharing = it }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(stringResource(R.string.location))
                Switch(
                    checked = locationTracking,
                    onCheckedChange = { locationTracking = it }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = { /* View data usage */ }) {
                Text(stringResource(R.string.data_usage))
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(onClick = { /* Request data deletion */ }) {
                Text(stringResource(R.string.remove_data))
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = { /* View privacy policy */ }) {
                Text(stringResource(R.string.privacy_policy))
            }
        }
    }
