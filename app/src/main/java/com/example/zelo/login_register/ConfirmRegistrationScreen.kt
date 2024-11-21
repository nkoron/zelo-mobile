package com.example.zelo.login_register
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.zelo.MyApplication
import com.example.zelo.R
import com.example.zelo.network.model.RegisterUser

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerificationScreen(
    navController: NavController,
    authViewModel: AuthViewModel = viewModel(factory = AuthViewModel.provideFactory(LocalContext.current.applicationContext as MyApplication)),
    onVerify: (String) -> Unit = { authViewModel.verifyUser(it)}
) {
    var verificationCode by remember { mutableStateOf("") }
    val uiState by authViewModel.uiState.collectAsState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1A1B25))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            (stringResource(R.string.verify_account)),
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            (stringResource(R.string.verify_account_email)),
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = verificationCode,
            onValueChange = { verificationCode = it },
            label = { Text(stringResource(R.string.verification_code), color = Color.Gray) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            textStyle = TextStyle(color = Color.White),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = Color.Gray,
                cursorColor = Color.White
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                if(verificationCode.isNotEmpty()) {
                    onVerify(verificationCode)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            if (uiState.isFetching) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                Text((stringResource(R.string.verify)), color = Color.White)
            }
        }

        if (uiState.error != null) {
            Text(
                text = uiState.error?.message ?: "An error occurred",
                color = Color.Red,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
        LaunchedEffect(uiState.user) {
            if (uiState.user != null && !uiState.isFetching && uiState.error == null) {
                navController.navigate("login")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(
            onClick = { /* Handle resend code logic */ }
        ) {
            Text(stringResource(R.string.resend_code), color = MaterialTheme.colorScheme.primary)
        }
    }
}

// Usage in your app
@Preview
@Composable
fun Component() {
    val navController = rememberNavController()
    VerificationScreen(
        navController = navController,
        onVerify = { code ->

        }
    )
}