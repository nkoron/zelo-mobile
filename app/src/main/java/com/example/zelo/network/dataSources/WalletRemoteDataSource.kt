package com.example.zelo.network.dataSources

import com.example.zelo.network.SessionManager
import com.example.zelo.network.WalletApiService
import com.example.zelo.network.model.Card
import com.example.zelo.network.model.WalletDetails
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class WalletRemoteDataSource(
    private val walletApiService: WalletApiService,
): RemoteDataSource() {
    suspend fun getCards(): List<Card> {
        return handleApiResponse{
            walletApiService.getCards()
        }
    }
    suspend fun addCard(card : Card): Card {
        return handleApiResponse{
            walletApiService.addCard(card)
        }
    }
    suspend fun deleteCard(cardId: Int){
        return handleApiResponse{
            walletApiService.deleteCard(cardId)
        }
    }
    val walletDetailStream: Flow<WalletDetails> = flow {
        while (true) {
            val walletDetail = handleApiResponse {
                walletApiService.getWalletDetails()
            }
            emit(walletDetail)
            delay(DELAY)
        }
    }

    companion object {
        const val DELAY: Long = 3000
    }
}