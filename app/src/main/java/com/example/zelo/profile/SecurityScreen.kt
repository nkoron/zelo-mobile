package com.example.zelo.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.zelo.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecurityScreen(onBack: () -> Unit) {
    var twoFactorAuth by remember { mutableStateOf(false) }
    var biometricLogin by remember { mutableStateOf(false) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(stringResource(R.string.security_config))
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(stringResource(R.string.two_factor_auth))
                Switch(
                    checked = twoFactorAuth,
                    onCheckedChange = { twoFactorAuth = it }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(stringResource(R.string.bio_auth))
                Switch(
                    checked = biometricLogin,
                    onCheckedChange = { biometricLogin = it }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = { /* Change password */ }) {
                Text(stringResource(R.string.chnge_pass), color = Color.White)
            }
        }
    }
