package com.example.zelo

import BottomNavBar
import DashboardScreen
import MovementsScreen
import ProfileScreen
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.zelo.login_register.EmailVerificationScreen
import com.example.zelo.login_register.RegisterScreen
import com.example.zelo.login_register.ResetPasswordScreen
import com.example.zelo.profile.ResetPassScreen
import com.example.zelo.login_register.SignInScreen
import com.example.zelo.ui.AuthViewModel

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.zelo.cards.CardsScreen
import com.example.zelo.contacts.ContactsScreen
import com.example.zelo.enter_money.DepositScreen
import com.example.zelo.transference.TransferConfirmationScreen
import com.example.zelo.transference.TransferDetailScreen
import com.example.zelo.transference.TransferScreen
import com.example.zelo.ui.AppBar
import com.example.zelo.activity.*
import com.example.zelo.profile.AccessibilityScreen
import com.example.zelo.profile.AccountDataScreen
import com.example.zelo.profile.HelpScreen
import com.example.zelo.profile.MessagesScreen
import com.example.zelo.profile.PersonalInfoScreen
import com.example.zelo.profile.PrivacyScreen
import com.example.zelo.profile.SecurityScreen
import com.example.zelo.qr.QRScannerScreen
import com.example.zelo.transference.TransactionConfirmedScreen
import com.example.zelo.ui.ZeloNavigationRail

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
    val configuration = LocalConfiguration.current
    val isTablet = configuration.screenWidthDp >= 600
        // Wrap the navigation with Scaffold to add the bottom bar
    Scaffold(
            topBar = {
                if (isLoggedIn) {
                    AppBar(
                        userName = "Fer Galan",
                        onNotificationsClick = {},
                    )
                }
            },

            bottomBar = {
                if (isLoggedIn && !isTablet) {
                    BottomNavBar(
                        navController = navController,
                        currentRoute = currentRoute // Pass the current route here
                    )
                }
            }

        ) { paddingValues ->
            Row(modifier = Modifier.fillMaxSize()) {
                if (isTablet && isLoggedIn) {
                    ZeloNavigationRail(
                        navController = navController,
                        currentRoute = currentRoute,
                        modifier = Modifier.padding(paddingValues)
                    )
                }
                MyNavHost(
                    navController = navController,
                    isLoggedIn = isLoggedIn,
                    paddingValues = paddingValues,
                    authViewModel = authViewModel
                )
            }
        }
    }


@Composable
fun MyNavHost(navController: NavHostController, isLoggedIn: Boolean, paddingValues: PaddingValues, authViewModel: AuthViewModel) {
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
        composable("movements") { MovementsScreen(navController) }
        composable("movements/incomes") { IncomeScreen(navController) }
        composable("movements/expenses") { ExpensesScreen(navController) }
        composable("transference") { TransferScreen(navController) }
        composable("transference/form") { TransferDetailScreen(onBack = {navController.popBackStack()}, onConfirm = {navController.navigate("transference/confirmation")}) }
        composable("transference/confirmed") { TransactionConfirmedScreen( onReturnHome = {navController.navigate("home")}) }
        composable("transference/contacts") { ContactsScreen(navController) }
        composable("transference/confirmation") { TransferConfirmationScreen(onBack = {navController.popBackStack()}, onConfirm = {navController.navigate("transference/confirmed")}) }
        // You can uncomment these screens as needed
        composable("cards") { CardsScreen(navController) }
        composable("profile") {
            ProfileScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateTo = { route -> navController.navigate(route) },
                onLogout = {
                    // Handle logout logic here
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            )
        }
        composable("profile/accessibility") { AccessibilityScreen(onBack = {navController.popBackStack()}) }
        composable("profile/security") { SecurityScreen(onBack = {navController.popBackStack()}) }
        composable("profile/account_data") { AccountDataScreen(onBack = {navController.popBackStack()}) }
        composable("profile/personal_info") { PersonalInfoScreen(onBack = {navController.popBackStack()}) }
        composable("profile/reset_password") { ResetPassScreen(onBack = {navController.popBackStack()}) }
        composable("profile/privacy") { PrivacyScreen(onBack = {navController.popBackStack()}) }
        composable("profile/messages") { MessagesScreen(onBack = {navController.popBackStack()}) }
        composable("profile/help") { HelpScreen(onBack = {navController.popBackStack()}) }
        composable("home/deposit") { DepositScreen(onBack = { navController.navigate("home") },) }
        composable("qr") { QRScannerScreen() }
    }
}