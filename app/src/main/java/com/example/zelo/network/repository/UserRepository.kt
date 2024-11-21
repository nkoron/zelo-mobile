package com.example.zelo.network.repository

import android.util.Log
import com.example.zelo.network.dataSources.UserRemoteDataSource
import com.example.zelo.network.model.Payment
import com.example.zelo.network.model.RegisterUser
import com.example.zelo.network.model.User
import com.example.zelo.network.model.VerificationCodeRequest
import kotlinx.coroutines.flow.Flow
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
    suspend fun logout(){
        userRemoteDataSource.logout()
        currentUserMutex.withLock {
            this.currentUser = null
        }
    }
    suspend fun getCurrentUser(refresh: Boolean = false): User? {
        if (refresh || currentUser == null) {
            val result = userRemoteDataSource.getCurrentUser()
            currentUserMutex.withLock {
                this.currentUser = result
            }
        }
        return currentUserMutex.withLock { this.currentUser }
    }
    suspend fun registerUser(user: RegisterUser): User? {
        return userRemoteDataSource.registerUser(user).user
    }
    suspend fun verifyUser(token: String): User? {
        return userRemoteDataSource.verifyUser(VerificationCodeRequest( token))
    }


    fun isAuthenticated(): Boolean {
        return this.currentUser != null
    }
}