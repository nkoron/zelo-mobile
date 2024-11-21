package com.example.zelo.network.repository

import android.util.Log
import com.example.zelo.network.dataSources.WalletRemoteDataSource
import com.example.zelo.network.model.Balance
import com.example.zelo.network.model.Card
import com.example.zelo.network.model.WalletDetails
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class WalletRepository(
    private val walletRemoteDataSource: WalletRemoteDataSource
) {
    private val cardsMutex = Mutex()

    private var cards: List<Card> = emptyList()

    suspend fun getCards(refresh: Boolean = false): List<Card> {
     if(refresh || cards.isEmpty()){
        val result =  walletRemoteDataSource.getCards()

        cardsMutex.withLock {
            Log.d("WalletRepository", "Cards: $result")
         this.cards = result
        }
     }
        return cardsMutex.withLock { this.cards }
    }
    suspend fun addCard(card: Card): Card{
        val newCard =  walletRemoteDataSource.addCard(card)
        cardsMutex.withLock {
            this.cards = emptyList()
        }
        return newCard
    }
    suspend fun deleteCard(cardId: Int) {
        walletRemoteDataSource.deleteCard(cardId)
        cardsMutex.withLock {
            this.cards = emptyList()
        }
    }
    suspend fun getBalance(): Balance {
        return walletRemoteDataSource.getBalance()
    }

    val walletDetailStream: Flow<WalletDetails> =
        walletRemoteDataSource.walletDetailStream
}