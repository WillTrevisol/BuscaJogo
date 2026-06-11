package com.trevisol.buscajogo.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trevisol.buscajogo.domain.model.Game
import com.trevisol.buscajogo.domain.repository.GameRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class SearchUiState {
    object Idle : SearchUiState()
    object Loading : SearchUiState()
    data class Success(val games: List<Game>) : SearchUiState()
    data class Error(val message: String) : SearchUiState()
}

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: GameRepository
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query

    private val _uiState = MutableStateFlow<SearchUiState>(SearchUiState.Idle)
    val uiState: StateFlow<SearchUiState> = _uiState

    private val _trendingGames = MutableStateFlow<List<Game>>(emptyList())
    val trendingGames: StateFlow<List<Game>> = _trendingGames

    init {
        loadTrendingGames()
        setupSearchDebounce()
    }

    private fun loadTrendingGames() {
        viewModelScope.launch {
            repository.getPopularGames()
                .onSuccess { games ->
                    _trendingGames.value = games
                }
        }
    }

    @OptIn(FlowPreview::class)
    private fun setupSearchDebounce() {
        viewModelScope.launch {
            _query
                .debounce(500)
                .distinctUntilChanged()
                .collect { q ->
                    if (q.isBlank()) {
                        _uiState.value = SearchUiState.Idle
                    } else {
                        performSearch(q)
                    }
                }
        }
    }

    fun onQueryChanged(newQuery: String) {
        _query.value = newQuery
    }

    private suspend fun performSearch(q: String) {
        _uiState.value = SearchUiState.Loading
        repository.searchGames(q)
            .onSuccess { games ->
                _uiState.value = SearchUiState.Success(games)
            }
            .onFailure { error ->
                _uiState.value = SearchUiState.Error(error.message ?: "Erro desconhecido")
            }
    }
}
