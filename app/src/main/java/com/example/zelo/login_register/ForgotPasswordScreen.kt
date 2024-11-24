package com.example.zelo.login_register

import android.util.Patterns
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.zelo.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmailVerificationScreen(
    viewModel: AuthViewModel,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    var email by remember { mutableStateOf("") }
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(color = Color(0xFF1A1B25))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.recover_password),
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White,
            modifier = Modifier.padding(vertical = 32.dp)
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text(stringResource(R.string.email), color = Color.White) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(32.dp))

        if(!uiState.isResetLinkSent){
            Button(
                onClick = {
                    viewModel.recoverPassword(email)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = email.isNotEmpty() && !uiState.isFetching
            ) {
                if (uiState.isFetching) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text(stringResource(R.string.send_reset_link))
                }
            }
        }


        if (uiState.error != null) {
            Text(
                text = uiState.error!!.message,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 16.dp)
            )
        }

        if (uiState.isResetLinkSent) {
            Text(
                text = stringResource(R.string.reset_link_sent),
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    navController.navigate("reset_password_form")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(stringResource(R.string.proceed_to_reset_password))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(
            onClick = { navController.navigate("login") }
        ) {
            Text(stringResource(R.string.back_to_login))
        }
    }
}




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResetPasswordScreen(
    viewModel: AuthViewModel,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    var token by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    val uiState by viewModel.uiState.collectAsState()

    val passwordRequirements = remember { PasswordRequirements() }
    val isPasswordValid = passwordRequirements.validatePassword(password)
    val doPasswordsMatch = password == confirmPassword

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(color = Color(0xFF1A1B25))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.reset_password),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(vertical = 32.dp)
        )

        OutlinedTextField(
            value = token,
            onValueChange = { token = it },
            label = { Text(stringResource(R.string.reset_token), color = Color.White) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        PasswordField(
            password = password,
            onPasswordChange = { password = it },
            passwordVisible = passwordVisible,
            onPasswordVisibilityChange = { passwordVisible = it },
            label = stringResource(R.string.new_password)
        )

        Spacer(modifier = Modifier.height(16.dp))

        PasswordField(
            password = confirmPassword,
            onPasswordChange = { confirmPassword = it },
            passwordVisible = confirmPasswordVisible,
            onPasswordVisibilityChange = { confirmPasswordVisible = it },
            label = stringResource(R.string.confirm_password)
        )

        Spacer(modifier = Modifier.height(24.dp))

        PasswordRequirementsDisplay(passwordRequirements, password)

        if (!doPasswordsMatch && confirmPassword.isNotEmpty()) {
            Text(
                text = stringResource(R.string.passwords_do_not_match),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                viewModel.resetPassword(token, password)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = token.isNotEmpty() && isPasswordValid && doPasswordsMatch && !uiState.isFetching
        ) {
            if (uiState.isFetching) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary)
            } else {
                Text(stringResource(R.string.reset_password))
            }
        }

        if (uiState.error != null) {
            Text(
                text = uiState.error!!.message,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 16.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(
            onClick = { navController.navigate("login") }
        ) {
            Text(stringResource(R.string.back_to_login))
        }
    }

    LaunchedEffect(uiState.isAuthenticated) {
        if (uiState.isAuthenticated) {

            navController.navigate("login")
        }
    }
}





@Composable
fun PasswordField(
    password: String,
    onPasswordChange: (String) -> Unit,
    passwordVisible: Boolean,
    onPasswordVisibilityChange: (Boolean) -> Unit,
    label: String
) {
    OutlinedTextField(
        value = password,
        onValueChange = onPasswordChange,
        label = { Text(label) },
        singleLine = true,
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        trailingIcon = {
            IconButton(onClick = { onPasswordVisibilityChange(!passwordVisible) }) {
                Icon(
                    imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                    contentDescription = if (passwordVisible) stringResource(R.string.hide_password) else stringResource(R.string.show_password)
                )
            }
        },
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun PasswordRequirementsDisplay(requirements: PasswordRequirements, password: String) {
    Column {
        requirements.getRequirements().forEach { requirement ->
            ValidationMessage(
                text = requirement.description,
                isValid = requirement.isValid(password)
            )
        }
    }
}

class PasswordRequirements {
    private val minLength = 8
    private val requirements = listOf(
        PasswordRequirement("At least $minLength characters") { it.length >= minLength },
        PasswordRequirement("At least one uppercase letter") { it.any { c -> c.isUpperCase() } },
        PasswordRequirement("At least one lowercase letter") { it.any { c -> c.isLowerCase() } },
        PasswordRequirement("At least one number") { it.any { c -> c.isDigit() } },
        PasswordRequirement("Only alphanumeric characters") { it.all { c -> c.isLetterOrDigit() } }
    )

    fun getRequirements() = requirements

    fun validatePassword(password: String): Boolean =
        requirements.all { it.isValid(password) }
}

data class PasswordRequirement(
    val description: String,
    val isValid: (String) -> Boolean
)

@Composable
private fun ValidationMessage(text: String, isValid: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (isValid) Icons.Default.CheckCircle else Icons.Default.Cancel,
            contentDescription = if (isValid) stringResource(R.string.valid) else stringResource(R.string.invalid),
            tint = if (isValid) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
            modifier = Modifier.padding(end = 8.dp)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = if (isValid) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
        )
    }
}


