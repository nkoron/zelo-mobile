package com.example.zelo

import BottomNavBar
import DashboardScreen
import MovementsScreen
import ProfileScreen
import com.example.zelo.transference.TransferDetailScreen
import android.annotation.SuppressLint
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.zelo.login_register.EmailVerificationScreen
import com.example.zelo.login_register.RegisterScreen
import com.example.zelo.login_register.ResetPasswordScreen
import com.example.zelo.login_register.VerificationScreen
import com.example.zelo.profile.ResetPassScreen
import com.example.zelo.login_register.SignInScreen

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
import com.example.zelo.login_register.AuthViewModel
import com.example.zelo.profile.AccessibilityScreen
import com.example.zelo.profile.AccountDataScreen
import com.example.zelo.profile.HelpScreen
import com.example.zelo.profile.MessagesScreen
import com.example.zelo.profile.PersonalInfoScreen
import com.example.zelo.profile.PrivacyScreen
import com.example.zelo.profile.SecurityScreen
import com.example.zelo.qr.QRScannerScreen
import com.example.zelo.transference.TransactionConfirmedScreen
import com.example.zelo.transference.TransferenceCBUViewModel
import com.example.zelo.ui.TopBarViewModel
import com.example.zelo.ui.ZeloNavigationRail

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun AppNavigation() {
    val profile = stringResource(R.string.profile)
    val cards = stringResource(R.string.cards)
    val transference = stringResource(R.string.transfer)
    val movements = stringResource(R.string.activity)
    val home = stringResource(R.string.home)

    val authViewModel: AuthViewModel = viewModel(factory = AuthViewModel.provideFactory(LocalContext.current.applicationContext as MyApplication))
    val uiState by authViewModel.uiState.collectAsState()

    val navController = rememberNavController()
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route ?: ""
    val configuration = LocalConfiguration.current
    val isTablet = configuration.screenWidthDp >= 600

    // Create a TopBarViewModel instance
    val topBarViewModel: TopBarViewModel = viewModel(
        factory = TopBarViewModel.provideFactory(LocalContext.current.applicationContext as MyApplication)
    )

    // Update the current section based on the route
    LaunchedEffect(currentRoute) {
        val section = when {
            currentRoute.startsWith("home") -> home
            currentRoute.startsWith("movements") -> movements
            currentRoute.startsWith("transference") -> transference
            currentRoute.startsWith("cards") -> cards
            currentRoute.startsWith("profile") -> profile
            else -> ""
        }
        topBarViewModel.updateCurrentSection(section)
    }

    Scaffold(
        topBar = {
            if (uiState.isAuthenticated) {
                AppBar(
                    viewModel = topBarViewModel,
                    onNotificationsClick = {},
                )
            }
        },
        bottomBar = {
            if (uiState.isAuthenticated && !isTablet) {
                BottomNavBar(
                    navController = navController,
                    currentRoute = currentRoute
                )
            }
        }
    ) { paddingValues ->
        Row(modifier = Modifier.fillMaxSize()) {
            if (isTablet && uiState.isAuthenticated) {
                ZeloNavigationRail(
                    navController = navController,
                    currentRoute = currentRoute,
                    modifier = Modifier.padding(paddingValues)
                )
            }
            MyNavHost(
                navController = navController,
                isLoggedIn = uiState.isAuthenticated,
                paddingValues = paddingValues,
                authViewModel = authViewModel
            )
        }
    }
}

// Keep the rest of the file unchanged


@Composable
fun MyNavHost(navController: NavHostController, isLoggedIn: Boolean, paddingValues: PaddingValues, authViewModel: AuthViewModel) {
    val transferenceCBUViewModel: TransferenceCBUViewModel = viewModel(factory = TransferenceCBUViewModel.provideFactory(
        LocalContext.current.applicationContext as MyApplication
    ))
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
        composable("verify_account") { VerificationScreen(navController) }

        // Screens for logged-in users
        composable("home") { DashboardScreen(navController) }
        composable("movements") {
            MovementsScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToIncomes = { navController.navigate("movements/incomes") },
                onNavigateToExpenses = { navController.navigate("movements/expenses") }
            )
        }
        composable("movements/incomes") { IncomeScreen(navController) }
        composable("movements/expenses") { ExpensesScreen(navController) }
        composable("transference") {
            TransferScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToForm = { navController.navigate("transference/form") },
                onNavigateToContacts = { navController.navigate("transference/contacts") }
            )
        }
        composable("transference/form") { TransferDetailScreen(onBack = {navController.popBackStack()}, onConfirm = {navController.navigate("transference/confirmation")}, viewModel = transferenceCBUViewModel) }
        composable("transference/confirmed") { TransactionConfirmedScreen( onReturnHome = {navController.navigate("home")}, viewModel = transferenceCBUViewModel) }
        composable("transference/contacts") { ContactsScreen(navController) }
        composable("transference/confirmation") { TransferConfirmationScreen(onBack = {navController.popBackStack()}, onConfirm = {navController.navigate("transference/confirmed")}, viewModel = transferenceCBUViewModel) }
        // You can uncomment these screens as needed
        composable("cards") { CardsScreen(
            onBack = { navController.popBackStack() }
        ) }
        composable("profile") {
            ProfileScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateTo = { route -> navController.navigate(route) },
                onLogout = {
                    // Handle logout logic here
                    authViewModel.logout()
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