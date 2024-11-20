package com.example.zelo.network.dataSources

import android.util.Log
import com.example.zelo.network.SessionManager
import com.example.zelo.network.WalletApiService
import com.example.zelo.network.model.LoginRequest
import com.example.zelo.network.model.LoginResponse
import com.example.zelo.network.model.User

class UserRemoteDataSource(
    private val userService: WalletApiService,
    private val sessionManager: SessionManager
) : RemoteDataSource() {
    suspend fun login(email: String, password: String) {
        val loginResponse = handleApiResponse {
            Log.d("UserRemoteDataSource", "Login with username: $email and password: $password")
            userService.loginUser(LoginRequest(email, password))
        }
        sessionManager.saveAuthToken(loginResponse.token)
    }
    suspend fun logout(){
        handleApiResponse {
            userService.logoutUser()
        }
        sessionManager.removeAuthToken()

    }
    suspend fun getCurrentUser(): User {
        return handleApiResponse {
            userService.getUser()
        }

    }
}