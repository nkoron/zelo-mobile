package com.example.zelo.network.repository

import android.util.Log
import com.example.zelo.network.dataSources.UserRemoteDataSource
import com.example.zelo.network.model.User
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
    }
    suspend fun logout(){
        userRemoteDataSource.logout()
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
}