package com.trevisol.buscajogo.presentation.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trevisol.buscajogo.domain.model.GameDetails
import com.trevisol.buscajogo.domain.repository.GameRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class GameDetailsUiState {
    object Loading : GameDetailsUiState()
    data class Success(val details: GameDetails) : GameDetailsUiState()
    data class Error(val message: String) : GameDetailsUiState()
}

@HiltViewModel
class GameDetailsViewModel @Inject constructor(
    private val repository: GameRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<GameDetailsUiState>(GameDetailsUiState.Loading)
    val uiState: StateFlow<GameDetailsUiState> = _uiState

    private val _gameId = MutableStateFlow<Int?>(null)

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val isWishlisted: StateFlow<Boolean> = _gameId
        .filterNotNull()
        .flatMapLatest { id -> repository.isGameInWishlist(id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val isOwned: StateFlow<Boolean> = _gameId
        .filterNotNull()
        .flatMapLatest { id -> repository.isGameInCollection(id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    fun loadGameDetails(id: Int) {
        _gameId.value = id
        viewModelScope.launch {
            _uiState.value = GameDetailsUiState.Loading
            repository.getGameDetails(id)
                .onSuccess { details ->
                    _uiState.value = GameDetailsUiState.Success(details)
                }
                .onFailure { error ->
                    _uiState.value = GameDetailsUiState.Error(error.message ?: "Erro desconhecido")
                }
        }
    }

    fun toggleWishlist() {
        val state = uiState.value
        if (state is GameDetailsUiState.Success) {
            viewModelScope.launch {
                repository.toggleWishlist(state.details)
            }
        }
    }

    fun toggleCollection() {
        val state = uiState.value
        if (state is GameDetailsUiState.Success) {
            viewModelScope.launch {
                if (isOwned.value) {
                    repository.removeFromCollection(state.details.id)
                } else {
                    repository.addToCollection(state.details)
                }
            }
        }
    }
}
