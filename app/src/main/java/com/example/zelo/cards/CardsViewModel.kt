package com.example.zelo.cards

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.zelo.MyApplication
import com.example.zelo.network.SessionManager
import com.example.zelo.network.dataSources.DataSourceException
import com.example.zelo.network.model.Card
import com.example.zelo.network.model.Error
import com.example.zelo.network.repository.WalletRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CardsUiState(
    val isLoading: Boolean = false,
    val cards: List<Card> = emptyList(),
    val error: Error? = null,
    val cardToDelete: Card? = null
)

class CardsViewModel(
    private val walletRepository: WalletRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(CardsUiState())
    val uiState: StateFlow<CardsUiState> = _uiState.asStateFlow()

    init {
        loadCards()
    }

    fun showDeleteConfirmation(card: Card) {
        _uiState.update { it.copy(cardToDelete = card) }
    }

    fun dismissDeleteConfirmation() {
        _uiState.update { it.copy(cardToDelete = null) }
    }

    fun loadCards() = runOnViewModelScope(
        { walletRepository.getCards(refresh = true) },
        { state, cards -> state.copy(cards = cards) }
    )

    fun addCard(card: Card) = runOnViewModelScope(
        { walletRepository.addCard(card) },
        { state, newCard -> state.copy(cards = state.cards + newCard) }
    )

    fun deleteCard(cardId: Int) = runOnViewModelScope(
        { walletRepository.deleteCard(cardId) },
        { state, _ -> state.copy(cards = state.cards.filter { it.id != cardId }, cardToDelete = null) }
    )

    private fun <R> runOnViewModelScope(
        block: suspend () -> R,
        updateState: (CardsUiState, R) -> CardsUiState
    ) = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true, error = null) }
        runCatching {
            block()
        }.onSuccess { response ->
            _uiState.update { currentState -> updateState(currentState, response).copy(isLoading = false) }
        }.onFailure { e ->
            _uiState.update { it.copy(isLoading = false, error = handleError(e)) }
        }
    }

    private fun handleError(e: Throwable): Error {
        return if (e is DataSourceException) {
            Error(e.code, e.message ?: "")
        } else {
            Error(null, e.message ?: "")
        }
    }

    companion object {
        fun provideFactory(
            application: MyApplication
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return CardsViewModel(
                    application.walletRepository,
                    application.sessionManager
                ) as T
            }
        }
    }
}