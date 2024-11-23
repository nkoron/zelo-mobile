@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.zelo.login_register

import android.app.DatePickerDialog
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.zelo.MyApplication
import com.example.zelo.R
import com.example.zelo.network.model.RegisterUser
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun RegisterScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel = viewModel(factory = AuthViewModel.provideFactory(LocalContext.current.applicationContext as MyApplication)),
    onSignUp: (user: RegisterUser) -> Unit = { authViewModel.registerUser(it) }
) {
    val uiState by authViewModel.uiState.collectAsState()
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp
    val isLandscape = screenWidth > screenHeight
    val isTablet = screenWidth > 600.dp

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var dni by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var surname by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }
    var phoneError by remember { mutableStateOf<String?>(null) }
    var dniError by remember { mutableStateOf<String?>(null) }
    var nameError by remember { mutableStateOf<String?>(null) }
    var surnameError by remember { mutableStateOf<String?>(null) }
    var ageError by remember { mutableStateOf<String?>(null) }

    val calendar = Calendar.getInstance()
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    fun openDatePicker() {
        val datePickerDialog = DatePickerDialog(
            navController.context,
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                birthDate = dateFormat.format(calendar.time)
                val currentDate = Calendar.getInstance()
                val age = currentDate.get(Calendar.YEAR) - year
                if (age < 15 || (age == 15 && (currentDate.get(Calendar.MONTH) < month ||
                            (currentDate.get(Calendar.MONTH) == month && currentDate.get(Calendar.DAY_OF_MONTH) < dayOfMonth)))) {
                    ageError = "You must be at least 15 years old to register"
                } else {
                    ageError = null
                }
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    fun validateEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})"
        return email.matches(emailRegex.toRegex())
    }

    fun validatePassword(password: String): Boolean {
        val passwordRegex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$"
        return password.matches(passwordRegex.toRegex())
    }

    fun validatePhone(phone: String): Boolean {
        val phoneRegex = "^[+]?[0-9]{10,13}$"
        return phone.matches(phoneRegex.toRegex())
    }

    fun validateDNI(dni: String): Boolean {
        val dniRegex = "^[0-9]{8}[A-Za-z]$"
        return dni.matches(dniRegex.toRegex())
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF1A1B25))
    ) {
        if (isLandscape || isTablet) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Image(
                    painter = painterResource(id = R.drawable.card),
                    contentDescription = null,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    contentScale = ContentScale.Fit
                )
                RegisterContent(
                    email = email,
                    password = password,
                    confirmPassword = confirmPassword,
                    phone = phone,
                    dni = dni,
                    name = name,
                    surname = surname,
                    birthDate = birthDate,
                    passwordVisible = passwordVisible,
                    confirmPasswordVisible = confirmPasswordVisible,
                    emailError = emailError,
                    passwordError = passwordError,
                    confirmPasswordError = confirmPasswordError,
                    phoneError = phoneError,
                    dniError = dniError,
                    nameError = nameError,
                    surnameError = surnameError,
                    ageError = ageError,
                    onEmailChange = {
                        email = it
                        emailError = if (validateEmail(it)) null else "Invalid email format"
                    },
                    onPasswordChange = {
                        password = it
                        passwordError = if (validatePassword(it)) null else "Password must be at least 8 characters long, contain at least one digit, one lowercase, one uppercase letter, and one special character"
                    },
                    onConfirmPasswordChange = {
                        confirmPassword = it
                        confirmPasswordError = if (it == password) null else "Passwords do not match"
                    },
                    onPhoneChange = {
                        phone = it
                        phoneError = if (validatePhone(it)) null else "Invalid phone number format"
                    },
                    onDniChange = {
                        dni = it
                        dniError = if (validateDNI(it)) null else "Invalid DNI format"
                    },
                    onNameChange = {
                        name = it
                        nameError = if (it.isNotBlank()) null else "Name cannot be empty"
                    },
                    onSurnameChange = {
                        surname = it
                        surnameError = if (it.isNotBlank()) null else "Surname cannot be empty"
                    },
                    onBirthDateChange = { birthDate = it },
                    onPasswordVisibilityChange = { passwordVisible = it },
                    onConfirmPasswordVisibilityChange = { confirmPasswordVisible = it },
                    onDatePickerClick = { openDatePicker() },
                    authViewModel = authViewModel,
                    navController = navController,
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(24.dp)
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.card),
                    contentDescription = null,
                    modifier = Modifier.size(200.dp),
                    contentScale = ContentScale.Fit
                )
                RegisterContent(
                    email = email,
                    password = password,
                    confirmPassword = confirmPassword,
                    phone = phone,
                    dni = dni,
                    name = name,
                    surname = surname,
                    birthDate = birthDate,
                    passwordVisible = passwordVisible,
                    confirmPasswordVisible = confirmPasswordVisible,
                    emailError = emailError,
                    passwordError = passwordError,
                    confirmPasswordError = confirmPasswordError,
                    phoneError = phoneError,
                    dniError = dniError,
                    nameError = nameError,
                    surnameError = surnameError,
                    ageError = ageError,
                    onEmailChange = {
                        email = it
                        emailError = if (validateEmail(it)) null else "Invalid email format"
                    },
                    onPasswordChange = {
                        password = it
                        passwordError = if (validatePassword(it)) null else "Password must be at least 8 characters long, contain at least one digit, one lowercase, one uppercase letter, and one special character"
                    },
                    onConfirmPasswordChange = {
                        confirmPassword = it
                        confirmPasswordError = if (it == password) null else "Passwords do not match"
                    },
                    onPhoneChange = {
                        phone = it
                        phoneError = if (validatePhone(it)) null else "Invalid phone number format"
                    },
                    onDniChange = {
                        dni = it
                        dniError = if (validateDNI(it)) null else "Invalid DNI format"
                    },
                    onNameChange = {
                        name = it
                        nameError = if (it.isNotBlank()) null else "Name cannot be empty"
                    },
                    onSurnameChange = {
                        surname = it
                        surnameError = if (it.isNotBlank()) null else "Surname cannot be empty"
                    },
                    onBirthDateChange = { birthDate = it },
                    onPasswordVisibilityChange = { passwordVisible = it },
                    onConfirmPasswordVisibilityChange = { confirmPasswordVisible = it },
                    onDatePickerClick = { openDatePicker() },
                    authViewModel = authViewModel,
                    navController = navController
                )
            }
        }
    }
}

@Composable
fun RegisterContent(
    email: String,
    password: String,
    confirmPassword: String,
    phone: String,
    dni: String,
    name: String,
    surname: String,
    birthDate: String,
    passwordVisible: Boolean,
    confirmPasswordVisible: Boolean,
    emailError: String?,
    passwordError: String?,
    confirmPasswordError: String?,
    phoneError: String?,
    dniError: String?,
    nameError: String?,
    surnameError: String?,
    ageError: String?,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onDniChange: (String) -> Unit,
    onNameChange: (String) -> Unit,
    onSurnameChange: (String) -> Unit,
    onBirthDateChange: (String) -> Unit,
    onPasswordVisibilityChange: (Boolean) -> Unit,
    onConfirmPasswordVisibilityChange: (Boolean) -> Unit,
    onDatePickerClick: () -> Unit,
    authViewModel: AuthViewModel,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val uiState by authViewModel.uiState.collectAsState()
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.register),
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Bold,
            ),
            color = Color.White,
            modifier = Modifier.fillMaxWidth().align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            label = { Text(stringResource(R.string.email), color = Color.Gray) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            textStyle = TextStyle(color = Color.White),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = Color.Gray,
                cursorColor = Color.White
            ),
            modifier = Modifier.fillMaxWidth(),
            isError = emailError != null
        )
        if (emailError != null) {
            Text(text = emailError, color = Color.Red, modifier = Modifier.align(Alignment.Start))
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChange,
            label = { Text(stringResource(R.string.password), color = Color.Gray) },
            singleLine = true,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                IconButton(onClick = { onPasswordVisibilityChange(!passwordVisible) }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = if (passwordVisible) stringResource(R.string.hide_password) else stringResource(R.string.show_password),
                        tint = Color.Gray
                    )
                }
            },
            textStyle = TextStyle(color = Color.White),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = Color.Gray,
                cursorColor = Color.White
            ),
            modifier = Modifier.fillMaxWidth(),
            isError = passwordError != null
        )
        if (passwordError != null) {
            Text(text = passwordError, color = Color.Red, modifier = Modifier.align(Alignment.Start))
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = onConfirmPasswordChange,
            label = { Text(stringResource(R.string.repeat_password), color = Color.Gray) },
            singleLine = true,
            visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                IconButton(onClick = { onConfirmPasswordVisibilityChange(!confirmPasswordVisible) }) {
                    Icon(
                        imageVector = if (confirmPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = if (confirmPasswordVisible) stringResource(R.string.hide_password) else stringResource(R.string.show_password),
                        tint = Color.Gray
                    )
                }
            },
            textStyle = TextStyle(color = Color.White),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = Color.Gray,
                cursorColor = Color.White
            ),
            modifier = Modifier.fillMaxWidth(),
            isError = confirmPasswordError != null
        )
        if (confirmPasswordError != null) {
            Text(text = confirmPasswordError, color = Color.Red, modifier = Modifier.align(Alignment.Start))
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            label = { Text(stringResource(R.string.name), color = Color.Gray) },
            singleLine = true,
            textStyle = TextStyle(color = Color.White),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = Color.Gray,
                cursorColor = Color.White
            ),
            modifier = Modifier.fillMaxWidth(),
            isError = nameError != null
        )
        if (nameError != null) {
            Text(text = nameError, color = Color.Red, modifier = Modifier.align(Alignment.Start))
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = surname,
            onValueChange = onSurnameChange,
            label = { Text(stringResource(R.string.surname), color = Color.Gray) },
            singleLine = true,
            textStyle = TextStyle(color = Color.White),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = Color.Gray,
                cursorColor = Color.White
            ),
            modifier = Modifier.fillMaxWidth(),
            isError = surnameError != null
        )
        if (surnameError != null) {
            Text(text = surnameError, color = Color.Red, modifier = Modifier.align(Alignment.Start))
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = dni,
            onValueChange = onDniChange,
            label = { Text(stringResource(R.string.id), color = Color.Gray) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            textStyle = TextStyle(color = Color.White),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = Color.Gray,
                cursorColor = Color.White
            ),
            modifier = Modifier.fillMaxWidth(),
            isError = dniError != null
        )
        if (dniError != null) {
            Text(text = dniError, color = Color.Red, modifier = Modifier.align(Alignment.Start))
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = phone,
            onValueChange = onPhoneChange,
            label = { Text(stringResource(R.string.phone), color = Color.Gray) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            textStyle = TextStyle(color = Color.White),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = Color.Gray,
                cursorColor = Color.White
            ),
            modifier = Modifier.fillMaxWidth(),
            isError = phoneError != null
        )
        if (phoneError != null) {
            Text(text = phoneError, color = Color.Red, modifier = Modifier.align(Alignment.Start))
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = birthDate,
            onValueChange = onBirthDateChange,
            label = { Text(stringResource(R.string.birth_date), color = Color.Gray) },
            singleLine = true,
            trailingIcon = {
                IconButton(onClick = onDatePickerClick) {
                    Icon(
                        imageVector = Icons.Filled.DateRange,
                        contentDescription = stringResource(R.string.select_date),
                        tint = Color.Gray
                    )
                }
            },
            textStyle = TextStyle(color = Color.White),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = Color.Gray,
                cursorColor = Color.White
            ),
            modifier = Modifier.fillMaxWidth(),
            isError = ageError != null
        )
        if (ageError != null) {
            Text(text = ageError, color = Color.Red, modifier = Modifier.align(Alignment.Start))
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(R.string.already_have_account),
                color = Color.Gray
            )
            Text(
                text = stringResource(R.string.login),
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable {
                    navController.navigate("login")
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty() &&
                    name.isNotEmpty() && surname.isNotEmpty() && birthDate.isNotEmpty() &&
                    emailError == null && passwordError == null && confirmPasswordError == null &&
                    nameError == null && surnameError == null && ageError == null &&
                    phoneError == null && dniError == null) {
                    val user = RegisterUser(name, surname, email, birthDate, password)
                    authViewModel.registerUser(user)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            enabled = email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty() &&
                    name.isNotEmpty() && surname.isNotEmpty() && birthDate.isNotEmpty() &&
                    emailError == null && passwordError == null && confirmPasswordError == null &&
                    nameError == null && surnameError == null && ageError == null &&
                    phoneError == null && dniError == null
        ) {
            if (uiState.isFetching) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                Text(text = stringResource(R.string.register), color = Color.White)
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
                navController.navigate("verify_account")
            }
        }
    }
}