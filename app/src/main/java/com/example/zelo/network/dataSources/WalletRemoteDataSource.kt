package com.example.zelo.network.dataSources

import com.example.zelo.network.SessionManager
import com.example.zelo.network.WalletApiService
import com.example.zelo.network.model.Balance
import com.example.zelo.network.model.BalanceRequest
import com.example.zelo.network.model.BalanceResponse
import com.example.zelo.network.model.Card
import com.example.zelo.network.model.WalletDetails
import com.example.zelo.network.model.getCardsResponse
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
    suspend fun getBalance(): Balance {
        return handleApiResponse {
            walletApiService.getBalance()
        }
    }
    suspend fun rechargeWallet(amount: Double): BalanceResponse {
        return handleApiResponse {
            walletApiService.rechargeWallet(BalanceRequest(amount))
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

    val cardsStream: Flow<List<Card>> = flow {
        while (true) {
            val cards = handleApiResponse {
                walletApiService.getCards()
            }
            emit(cards)
            delay(DELAY)
        }
    }

    companion object {
        const val DELAY: Long = 3000
    }
}