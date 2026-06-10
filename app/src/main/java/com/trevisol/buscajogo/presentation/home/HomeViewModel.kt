package com.trevisol.buscajogo.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trevisol.buscajogo.domain.model.Deal
import com.trevisol.buscajogo.domain.model.Game
import com.trevisol.buscajogo.domain.repository.GameRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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

    init {
        fetchHomeData()
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
