@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.zelo.login_register

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import com.example.zelo.R

@Composable
fun SignInScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    onSignIn: (email: String, password: String) -> Unit = { _, _ -> }
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var useBiometric by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(color = Color(0xFF1A1B25))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        // Card Icon
        Image(
            painter = painterResource(id = R.drawable.card), // Usa tu propio nombre de imagen
            contentDescription = null,
            modifier = Modifier.size(256.dp),
            alignment = Alignment.Center)


        Spacer(modifier = Modifier.height(24.dp))


        Text(
            text = "Iniciar sesión",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Bold,
            ),
            color = Color.White,
            modifier = Modifier.fillMaxWidth().align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Email Field
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo Electronico", color = Color.Gray) },
            singleLine = true,
            textStyle = TextStyle(color = Color.White), // Usa textStyle en lugar de textColor
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color(0xFF6C63FF),
                unfocusedBorderColor = Color.Gray,
                cursorColor = Color.White // Define también el color del cursor si es necesario
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Password Field
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña", color = Color.Gray) },
            singleLine = true,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña",
                        tint = Color.Gray
                    )
                }
            },
            textStyle = TextStyle(color = Color.White), // Usa textStyle en lugar de textColor
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color(0xFF6C63FF),
                unfocusedBorderColor = Color.Gray,
                cursorColor = Color.White
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Biometric Checkbox
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = useBiometric,
                onCheckedChange = { useBiometric = it },
                colors = CheckboxDefaults.colors(
                    checkedColor = Color(0xFF6C63FF),
                    uncheckedColor = Color.Gray
                )
            )
            Text(
                text = "Utilizar huella digital la próxima vez que inices sesion",
                color = Color.White,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Login Button
        Button(
            onClick = { onSignIn(email, password) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6C63FF)),
            shape = MaterialTheme.shapes.large
        ) {
            Text("Login", fontSize = 18.sp)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Register Link
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "No tienes una cuenta? ",
                color = Color.Gray
            )
            Text(
                text = "Registrarse",
                color = Color(0xFF6C63FF),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable {
                    navController.navigate("register") // Navegar a la pantalla de registro
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Forgot Password
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Olvidaste tu contraseña? ",
                color = Color.Gray
            )
            Text(
                text = "Restablecer",
                color = Color(0xFF6C63FF),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable {
                    navController.navigate("reset_password") // Navegar a la pantalla de restablecer contraseña
                }
            )
        }
    }
}
