import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.zelo.MyApplication
import com.example.zelo.R
import com.example.zelo.profile.ProfileUiState
import com.example.zelo.profile.ProfileViewModel
import com.example.zelo.ui.TopBarViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = viewModel(
        factory = ProfileViewModel.provideFactory(
            LocalContext.current.applicationContext as MyApplication
        )
    ),
    onNavigateBack: () -> Unit,
    onNavigateTo: (String) -> Unit,
    onLogout: () -> Unit
) {
    val uiState = viewModel.uiState
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val menuItems = listOf(
        MenuItemData(Icons.Outlined.Accessibility, stringResource(R.string.accessibility), stringResource(R.string.adjust_access_options), "profile/accessibility"),
        MenuItemData(Icons.Outlined.Security, stringResource(R.string.security), stringResource(R.string.admin_sec), "profile/security"),
        MenuItemData(Icons.Outlined.Person, stringResource(R.string.account_data), stringResource(R.string.validate_data), "profile/account_data"),
        MenuItemData(Icons.Outlined.Info, stringResource(R.string.personal_info), stringResource(R.string.personal_info_desc), "profile/personal_info"),
        MenuItemData(Icons.Outlined.Lock, stringResource(R.string.reset_password), stringResource(R.string.forgot_password) + stringResource(R.string.reset_it), "profile/reset_password"),
        MenuItemData(Icons.Outlined.PrivacyTip, stringResource(R.string.privacy), stringResource(R.string.data_preferences), "profile/privacy"),
        MenuItemData(Icons.Outlined.Message, stringResource(R.string.messages), stringResource(R.string.message_settings), "profile/messages"),
        MenuItemData(Icons.Outlined.Help, stringResource(R.string.help), stringResource(R.string.assistance), "profile/help")
    )
    LaunchedEffect(Unit) {
        viewModel.checkAuthenticationStatus()
    }
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(bottom = 16.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            item { ProfileCard(onLogout, uiState) }
            items(menuItems) { item ->
                MenuItem(item, onNavigateTo)
            }
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
@Composable
fun ProfileCard(onLogout: () -> Unit, uiState: ProfileUiState) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "JR",
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = buildString {
                    uiState.user?.firstName.let { append(it) }
                    uiState.user?.lastName.let { append(" $it") }
                },
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = uiState.user?.email ?: "",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onLogout,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text(stringResource(R.string.close_session), color = MaterialTheme.colorScheme.onError)
            }
        }
    }
}

data class MenuItemData(
    val icon: ImageVector,
    val title: String,
    val description: String,
    val path: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuItem(item: MenuItemData, onNavigateTo: (String) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .animateContentSize(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(12.dp),
        onClick = { onNavigateTo(item.path) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.title,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = item.description,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        fontSize = 14.sp,
                        maxLines = 1
                    )
                }
                Icon(
                    imageVector = Icons.Outlined.ChevronRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}