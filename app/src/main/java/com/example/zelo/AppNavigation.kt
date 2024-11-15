package com.example.zelo

import BottomNavBar
import DashboardScreen
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.zelo.login_register.EmailVerificationScreen
import com.example.zelo.login_register.RegisterScreen
import com.example.zelo.login_register.ResetPasswordScreen
import com.example.zelo.login_register.SignInScreen
import com.example.zelo.ui.AuthViewModel

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.zelo.cards.CardsScreen
import com.example.zelo.transference.TransferConfirmationScreen
import com.example.zelo.transference.TransferDetailScreen
import com.example.zelo.transference.TransferScreen

@Composable
fun AppNavigation() {
    // Access the ViewModel to track login state
    val authViewModel: AuthViewModel = viewModel() // Use viewModel() here

    // Observing the login state from the ViewModel
    val isLoggedIn = authViewModel.isLoggedIn.collectAsState(initial = false).value // Use collectAsState to observe LiveData or StateFlow

    // Create a NavHostController to manage the navigation
    val navController = rememberNavController()

    // Get the current route from the navController
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route ?: ""

    // Wrap the navigation with Scaffold to add the bottom bar
    Scaffold(
        bottomBar = {
            // Show BottomNavBar if the user is logged in
            if (isLoggedIn) {
                BottomNavBar(
                    navController = navController,
                    currentRoute = currentRoute // Pass the current route here
                )
            }
        }
    ) { paddingValues ->
        // Set up navigation for different screens
        NavHost(
            navController = navController,
            startDestination = if (isLoggedIn) "home" else "login",
            modifier = Modifier.padding(paddingValues) // This will apply padding to the content area
        ) {
            // Screens for users who are not logged in
            composable("login") {
                SignInScreen(navController, authViewModel)
            }
            composable("register") { RegisterScreen(navController) }
            composable("reset_password") { EmailVerificationScreen(navController) }
            composable("reset_password_form") { ResetPasswordScreen(navController) }

            // Screens for logged-in users
            composable("home") { DashboardScreen(navController) }
            composable("transference") {TransferScreen(navController)}
            composable("transference/form") { TransferDetailScreen(navController)  }
            composable("transference/confirm") { TransferConfirmationScreen()  }
            // You can uncomment these screens as needed
            // composable("movements") { MovementsScreen(navController) }
             composable("cards") { CardsScreen(navController) }
            // composable("profile") { ProfileScreen(navController) }
        }
    }
}
