package com.example.zelo.ui
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun ZeloNavigationRail(
    navController: NavController,
    currentRoute: String,
    modifier: Modifier = Modifier
) {
        NavigationRail(
            modifier = modifier,
            containerColor = Color(0xFF1A1B25)
        ) {
            NavigationRailItem(
                selected = currentRoute == "home" || currentRoute.startsWith("transference"),
                onClick = { navController.navigate("home") },
                icon = { Icon(Icons.Outlined.Home, contentDescription = "Home") },
                label = { Text("Home") },
                modifier = Modifier.padding(vertical = 8.dp),
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
                icon = { Icon(Icons.Outlined.ShowChart, contentDescription = "Actividad") },
                modifier = Modifier.padding(vertical = 8.dp),
                label = { Text("Actividad") },
                colors = NavigationRailItemDefaults.colors(
                    selectedIconColor = Color(0xFFE91E63),
                    selectedTextColor = Color(0xFFE91E63),
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray
                )
            )
            NavigationRailItem(
                selected = false,
                onClick = { navController.navigate("qr-scanner") },
                icon = {
                    Icon(
                        Icons.Filled.QrCode2,
                        contentDescription = "QR Code",
                        modifier = Modifier.size(32.dp)
                    )
                },
                label = { Text("QR") },
                modifier = Modifier.padding(vertical = 8.dp),
                colors = NavigationRailItemDefaults.colors(
                    selectedIconColor = Color(0xFFE91E63),
                    selectedTextColor = Color(0xFFE91E63),
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray
                )
            )
            NavigationRailItem(
                selected = currentRoute == "cards",
                onClick = { navController.navigate("cards") },
                icon = { Icon(Icons.Outlined.CreditCard, contentDescription = "Tarjetas") },
                label = { Text("Tarjetas") },
                modifier = Modifier.padding(vertical = 8.dp),
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
                modifier = Modifier.padding(vertical = 8.dp),
                icon = { Icon(Icons.Outlined.Person, contentDescription = "Perfil") },
                label = { Text("Perfil") },
                colors = NavigationRailItemDefaults.colors(
                    selectedIconColor = Color(0xFFE91E63),
                    selectedTextColor = Color(0xFFE91E63),
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray
                )
            )
        }
    }
