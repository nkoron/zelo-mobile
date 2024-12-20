package com.example.zelo.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.zelo.MyApplication
import com.example.zelo.R
import android.content.res.Configuration

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(
    viewModel: TopBarViewModel = viewModel(
        factory = TopBarViewModel.provideFactory(
            LocalContext.current.applicationContext as MyApplication
        )
    ),
    currentRoute: String,
    onBackClick: () -> Unit,
    onHome: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    val configuration = LocalConfiguration.current
    val isTablet = configuration.screenWidthDp >= 600
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    LaunchedEffect(Unit) {
        viewModel.checkAuthenticationStatus()
    }

    val showBackButton = currentRoute.count { it == '/' } > 0

    val appBarHeight = if (isLandscape) 48.dp else 64.dp

    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        modifier = Modifier.height(appBarHeight)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            // Back Button (if it's a second-level route)
            if (showBackButton) {
                if (currentRoute == "home/transference/confirmed") {
                    IconButton(
                        onClick = onHome,
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .size(if (isLandscape) 40.dp else 48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(R.string.close),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                } else {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .size(if (isLandscape) 40.dp else 48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // Section Title
            Text(
                text = uiState.currentSection,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(horizontal = if (showBackButton) 48.dp else 0.dp)
            )

            // User Avatar
            if (uiState.isAuthenticated && uiState.user != null) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .size(if (isLandscape) 32.dp else 40.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        val initials = buildString {
                            uiState.user!!.firstName.firstOrNull()?.let { append(it.uppercaseChar()) }
                            uiState.user!!.lastName.firstOrNull()?.let { append(it.uppercaseChar()) }
                        }

                        Text(
                            text = initials,
                            color = Color.White,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

