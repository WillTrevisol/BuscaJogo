package com.trevisol.buscajogo.presentation.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trevisol.buscajogo.domain.model.GameDetails
import com.trevisol.buscajogo.domain.repository.GameRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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

    fun loadGameDetails(id: Int) {
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
}
