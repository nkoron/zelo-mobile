package com.example.zelo

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.zelo.ui.theme.ZeloTheme

class MainActivity : ComponentActivity() {
    private lateinit var navController: NavHostController
    private lateinit var handleDeepLink: (String) -> Unit

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ZeloTheme(dynamicColor = false) {
                AppContent()
            }
        }
        handleIntent(intent)
    }

    @Composable
    fun AppContent() {
        navController = rememberNavController()
        AppNavigation(
            navController = navController,
            onDeepLinkReceived = { linkHandler ->
                handleDeepLink = linkHandler
            }
        )
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        when (intent.action) {
            Intent.ACTION_VIEW -> {
                val uri = intent.data
                if (uri != null) {
                    val linkUuid = uri.lastPathSegment
                    if (linkUuid != null) {
                        handleDeepLink(linkUuid)
                    }
                }
            }
        }
    }
}
