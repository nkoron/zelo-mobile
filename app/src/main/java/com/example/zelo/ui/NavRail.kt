package com.example.zelo.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.zelo.R

@Composable
fun ZeloNavigationRail(
    navController: NavController,
    currentRoute: String,
    modifier: Modifier = Modifier
) {
    val configuration = LocalConfiguration.current
    val isTablet = configuration.screenWidthDp >= 600 && configuration.screenHeightDp >= 600
    val isLandscape = configuration.screenWidthDp > configuration.screenHeightDp && !isTablet

    val verticalPadding = if (isLandscape) 4.dp else 8.dp

    NavigationRail(
        modifier = modifier,
        containerColor = Color(0xFF1A1B25)
    ) {
        Column(
            modifier = Modifier.fillMaxHeight().verticalScroll(rememberScrollState())
        ) {
            NavigationRailItem(
                selected = currentRoute == "home" || currentRoute.startsWith("transference"),
                onClick = { navController.navigate("home") },
                icon = { Icon(Icons.Outlined.Home, contentDescription = stringResource(R.string.home)) },
                label = { Text(stringResource(R.string.home)) },
                modifier = Modifier.padding(vertical = verticalPadding),
                colors = NavigationRailItemDefaults.colors(
                    selectedIconColor = Color(0xFFE91E63),
                    selectedTextColor = Color(0xFFE91E63),
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray
                )
            )

            NavigationRailItem(
                selected = currentRoute == "movements",
                onClick = { navController.navigate("movements") },
                icon = { Icon(Icons.Outlined.ShowChart, contentDescription = stringResource(R.string.activity)) },
                modifier = Modifier.padding(vertical = verticalPadding),
                label = { Text(stringResource(R.string.activity)) },
                colors = NavigationRailItemDefaults.colors(
                    selectedIconColor = Color(0xFFE91E63),
                    selectedTextColor = Color(0xFFE91E63),
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray
                )
            )

            if (isLandscape && !isTablet) {
                NavigationRailItem(
                    selected = currentRoute == "qr",
                    onClick = { navController.navigate("qr") },
                    icon = { Icon(Icons.Filled.QrCode2, contentDescription = stringResource(R.string.qr_code)) },
                    label = { Text(stringResource(R.string.qr_code)) },
                    modifier = Modifier.padding(vertical = verticalPadding),
                    colors = NavigationRailItemDefaults.colors(
                        selectedIconColor = Color(0xFFE91E63),
                        selectedTextColor = Color(0xFFE91E63),
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray
                    )
                )
            }

            NavigationRailItem(
                selected = currentRoute == "cards",
                onClick = { navController.navigate("cards") },
                icon = { Icon(Icons.Outlined.CreditCard, contentDescription = stringResource(R.string.cards)) },
                label = { Text(stringResource(R.string.cards)) },
                modifier = Modifier.padding(vertical = verticalPadding),
                colors = NavigationRailItemDefaults.colors(
                    selectedIconColor = Color(0xFFE91E63),
                    selectedTextColor = Color(0xFFE91E63),
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray
                )
            )

            NavigationRailItem(
                selected = currentRoute == "profile",
                onClick = { navController.navigate("profile") },
                modifier = Modifier.padding(vertical = verticalPadding),
                icon = { Icon(Icons.Outlined.Person, contentDescription = stringResource(R.string.profile)) },
                label = { Text(stringResource(R.string.profile)) },
                colors = NavigationRailItemDefaults.colors(
                    selectedIconColor = Color(0xFFE91E63),
                    selectedTextColor = Color(0xFFE91E63),
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray
                )
            )
        }
    }
}
