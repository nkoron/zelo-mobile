package com.example.zelo

import android.app.Application
import com.example.zelo.network.RetrofitClient
import com.example.zelo.network.SessionManager
import com.example.zelo.network.dataSources.UserRemoteDataSource
import com.example.zelo.network.dataSources.WalletRemoteDataSource
import com.example.zelo.network.repository.UserRepository
import com.example.zelo.network.repository.WalletRepository

class MyApplication: Application() {

    private val userRemoteDataSource: UserRemoteDataSource
        get() = UserRemoteDataSource(RetrofitClient.getApiService(this),sessionManager)

    private val walletRemoteDataSource: WalletRemoteDataSource
        get() = WalletRemoteDataSource(RetrofitClient.getApiService(this))

    val walletRepository: WalletRepository
        get() = WalletRepository(walletRemoteDataSource)

    val sessionManager: SessionManager
        get() = SessionManager(this)

    val userRepository: UserRepository
        get() = UserRepository(userRemoteDataSource)

}