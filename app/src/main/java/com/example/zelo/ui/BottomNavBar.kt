import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController


@Composable
fun BottomNavBar(
    navController: NavController,
    modifier: Modifier = Modifier,
    currentRoute: String
) {
    Box(modifier = modifier.fillMaxWidth()) {
        FloatingActionButton(
            onClick = { navController.navigate("qr-scanner") },
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = (-16).dp, x = (5).dp)
                .zIndex(1f),
            containerColor = Color(0xFF6C63FF),
            shape = CircleShape
        ) {
            Box(modifier = Modifier.size(76.dp).padding(16.dp)) {
                Icon(
                    imageVector = Icons.Filled.QrCode2,
                    contentDescription = "QR Code",
                    tint = Color.White,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        Surface(
            modifier = Modifier.fillMaxWidth().height(80.dp),
            color = Color(0xFF1A1B25),
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                NavBarItem(
                    icon = Icons.Outlined.Home,
                    label = "Home",
                    isSelected = currentRoute == "home" || currentRoute.startsWith("transference"),
                    onClick = { navController.navigate("home") }
                )
                NavBarItem(
                    icon = Icons.Outlined.ShowChart,
                    label = "Actividad",
                    isSelected = currentRoute == "movements",
                    onClick = { navController.navigate("movements") }
                )
                Spacer(modifier = Modifier.width(56.dp))
                NavBarItem(
                    icon = Icons.Outlined.CreditCard,
                    label = "Tarjetas",
                    isSelected = currentRoute == "cards",
                    onClick = { navController.navigate("cards") }
                )
                NavBarItem(
                    icon = Icons.Outlined.Person,
                    label = "Perfil",
                    isSelected = currentRoute == "profile",
                    onClick = { navController.navigate("profile") }
                )
            }
        }
    }
}

@Composable
private fun NavBarItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) Color(0xFFE91E63) else Color.Gray,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = if (isSelected) Color(0xFFE91E63) else Color.Gray,
            modifier = Modifier.padding(top = 4.dp),
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}