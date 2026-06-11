package com.trevisol.buscajogo.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trevisol.buscajogo.domain.model.Deal
import com.trevisol.buscajogo.domain.model.Game
import com.trevisol.buscajogo.domain.repository.GameRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: GameRepository
) : ViewModel() {

    private val _popularGames = MutableStateFlow<List<Game>>(emptyList())
    val popularGames: StateFlow<List<Game>> = _popularGames

    private val _bestDeals = MutableStateFlow<List<Deal>>(emptyList())
    val bestDeals: StateFlow<List<Deal>> = _bestDeals

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _navigateToDetails = Channel<Int>()
    val navigateToDetails = _navigateToDetails.receiveAsFlow()

    private val _errorEvent = Channel<String>()
    val errorEvent = _errorEvent.receiveAsFlow()

    init {
        fetchHomeData()
    }

    fun onDealClicked(deal: Deal) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getGameIdByTitle(deal.title)
                .onSuccess { id ->
                    _navigateToDetails.send(id)
                }
                .onFailure { error ->
                    _errorEvent.send(error.message ?: "Não foi possível encontrar este jogo")
                }
            _isLoading.value = false
        }
    }

    private fun fetchHomeData() {
        viewModelScope.launch {
            _isLoading.value = true
            
            val popularResult = repository.getPopularGames()
            val dealsResult = repository.getBestDeals()

            _popularGames.value = popularResult.getOrDefault(emptyList())
            _bestDeals.value = dealsResult.getOrDefault(emptyList())
            
            _isLoading.value = false
        }
    }
}
