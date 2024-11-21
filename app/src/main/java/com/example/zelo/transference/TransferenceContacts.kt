package com.example.zelo.transference

import android.content.Context
import android.provider.ContactsContract
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.example.zelo.R
import com.google.accompanist.permissions.*
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransferenceContactsScreen(
    onBack: () -> Unit = {},
    onNavigateToTransferenceCBU: (String) -> Unit = {} // Callback to navigate with email
) {
    val context = LocalContext.current
    val contactsViewModel: ContactsViewModel = viewModel()

    // Requesting permission and fetching contacts
    RequestContactPermission {
        contactsViewModel.fetchContacts(context) // Fetch contacts when permission is granted
    }

    var searchQuery by remember { mutableStateOf("") }

    // Observe the contacts state from the ViewModel
    val contacts = contactsViewModel.contacts.value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Search Bar
        TextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.medium),
            placeholder = { Text(stringResource(R.string.search)) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            trailingIcon = { Icon(Icons.Default.FilterList, contentDescription = "Filter") },
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color(0xFFF3F0F7)
            ),
            singleLine = true
        )

        // Frequent Contacts Section
        Text(
            text = stringResource(R.string.frequent_contacts),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        // Display Contacts
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Filter contacts based on the search query
            items(contacts.filter { it.name.contains(searchQuery, ignoreCase = true) }) { contact ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.AccountCircle,
                            contentDescription = null,
                            modifier = Modifier.size(40.dp)
                        )
                        Text(contact.name, fontSize = 16.sp)
                    }
                    Button(
                        onClick = {

                            Log.d("TransferenceContacts", "Transfer button clicked for contact: ${contact.name} with email: ${contact.email}")
                            contact.email?.let { email ->
                                onNavigateToTransferenceCBU(email)  // Pass email to the navigation callback
                            }
                        },
                        enabled = contact.email != null, // Disable if email is null
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF854EF9))
                    ) {
                        Text(stringResource(R.string.transfer))
                    }

                }
            }
        }
    }
}


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RequestContactPermission(onPermissionGranted: () -> Unit) {
    val permissionState =
        rememberPermissionState(permission = android.Manifest.permission.READ_CONTACTS)

    // When the permission status changes, call the onPermissionGranted function if permission is granted
    LaunchedEffect(permissionState.status) {
        if (permissionState.status is PermissionStatus.Granted) {
            Log.d("TransferenceContacts", "Permission granted")
            onPermissionGranted()
        } else {
            Log.d("TransferenceContacts", "Permission not granted, requesting...")
            permissionState.launchPermissionRequest() // Request permission if not granted
        }
    }
}
