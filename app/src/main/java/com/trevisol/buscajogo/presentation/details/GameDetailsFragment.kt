package com.trevisol.buscajogo.presentation.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GameDetailsFragment : Fragment() {

    private val viewModel: GameDetailsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                val isWishlisted by viewModel.isWishlisted.collectAsStateWithLifecycle()
                val isOwned by viewModel.isOwned.collectAsStateWithLifecycle()

                GameDetailsScreen(
                    state = uiState,
                    isWishlisted = isWishlisted,
                    isOwned = isOwned,
                    onBack = { findNavController().popBackStack() },
                    onWishlistClick = viewModel::toggleWishlist,
                    onCollectionClick = viewModel::toggleCollection
                )
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val gameId = arguments?.getInt("gameId") ?: -1
        viewModel.loadGameDetails(gameId)
    }
}
