package com.example.zelo.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.zelo.MyApplication
import com.example.zelo.R

@Composable
fun AppBar(
    viewModel: TopBarViewModel = viewModel(
        factory = TopBarViewModel.provideFactory(
            LocalContext.current.applicationContext as MyApplication
        )
    ),
    onBackClick: (() -> Unit)? = null,
    subtitle: String? = null,
    onNotificationsClick: () -> Unit
) {
    val uiState = viewModel.uiState
    val configuration = LocalConfiguration.current
    val isTablet = configuration.screenWidthDp >= 600
    val horizontalPadding = if (isTablet) 32.dp else 16.dp
    val avatarSize = if (isTablet) 56.dp else 48.dp
    val startPadding: Dp = if (isTablet) 0.dp else horizontalPadding
    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = startPadding,
                    end = horizontalPadding,
                    top = 12.dp,
                    bottom = 12.dp
                ),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                if (onBackClick != null) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }

                // Removed: if (isTablet) { Spacer(modifier = Modifier.width(24.dp)) }

                if (onBackClick == null && isTablet) {
                    Spacer(modifier = Modifier.width(16.dp))
                }

                Surface(
                    shape = CircleShape,
                    color = Color(0xFF6C63FF),
                    modifier = Modifier.size(avatarSize)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        val initials = buildString {
                            uiState.user?.firstName?.firstOrNull()?.let { append(it.uppercaseChar()) }
                            uiState.user?.lastName?.firstOrNull()?.let { append(it.uppercaseChar()) }
                        }

                        Text(
                            text = initials,
                            color = Color.White,
                            style = if (isTablet) {
                                MaterialTheme.typography.titleLarge
                            } else {
                                MaterialTheme.typography.bodyLarge
                            },
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = buildString {
                            append(stringResource(R.string.hello))
                            append(", ")
                            append(uiState.user?.firstName ?: "")
                            append(" ")
                            append(uiState.user?.lastName ?: "")
                            append("!")
                        },
                        style = if (isTablet) {
                            MaterialTheme.typography.titleLarge
                        } else {
                            MaterialTheme.typography.titleMedium
                        },
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (subtitle != null) {
                        Text(
                            text = subtitle,
                            style = if (isTablet) {
                                MaterialTheme.typography.bodyMedium
                            } else {
                                MaterialTheme.typography.bodySmall
                            },
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            IconButton(
                onClick = onNotificationsClick,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = stringResource(R.string.notifications),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}