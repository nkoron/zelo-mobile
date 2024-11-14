import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.zelo.login_register.EmailVerificationScreen
import com.example.zelo.login_register.SignInScreen
import com.example.zelo.login_register.RegisterScreen
import com.example.zelo.login_register.ResetPasswordScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "login") {
        composable("login") { SignInScreen(navController) }
        composable("register") { RegisterScreen(navController) }
        composable("reset_password") { EmailVerificationScreen(navController) }
        composable("reset_password_form") {ResetPasswordScreen(navController)}
    }

}
