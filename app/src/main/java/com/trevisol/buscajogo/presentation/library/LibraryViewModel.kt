package com.trevisol.buscajogo.presentation.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trevisol.buscajogo.domain.model.Game
import com.trevisol.buscajogo.domain.repository.GameRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val repository: GameRepository
) : ViewModel() {

    val wishlist: StateFlow<List<Game>> = repository.getWishlist()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val collection: StateFlow<List<Game>> = repository.getCollection()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
