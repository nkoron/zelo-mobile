package com.example.zelo.network.repository

import android.util.Log
import com.example.zelo.network.dataSources.UserRemoteDataSource
import com.example.zelo.network.model.RegisterUser
import com.example.zelo.network.model.User
import com.example.zelo.network.model.VerificationCodeRequest
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class UserRepository(
    private val userRemoteDataSource: UserRemoteDataSource,
) {
    private val currentUserMutex = Mutex()
    private var currentUser: User? = null

    suspend fun login(username: String, password: String) {
        Log.d("UserRepository", "Login with username: $username and password: $password")
        userRemoteDataSource.login(username, password)
        currentUserMutex.withLock {
            this.currentUser = null
        }
    }

    suspend fun logout() {
        userRemoteDataSource.logout()
        currentUserMutex.withLock {
            this.currentUser = null
        }
    }

    suspend fun getCurrentUser(refresh: Boolean = false): User? {
        return currentUserMutex.withLock {
            if (refresh || currentUser == null) {
                try {
                    currentUser = userRemoteDataSource.getCurrentUser()
                } catch (e: Exception) {
                    Log.e("UserRepository", "Error fetching current user", e)
                    currentUser = null
                }
            }
            currentUser
        }
    }

    suspend fun registerUser(user: RegisterUser): User {
        return userRemoteDataSource.registerUser(user)
    }

    suspend fun verifyUser(token: String): User {
        return userRemoteDataSource.verifyUser(VerificationCodeRequest(token))
    }

    suspend fun recoverPassword(email: String) {
        userRemoteDataSource.recoverPassword(email)
    }

    suspend fun resetPassword(token: String, newPassword: String) {
        userRemoteDataSource.resetPassword(token, newPassword)
    }

    fun isAuthenticated(): Boolean {
        return this.currentUser != null
    }
}

