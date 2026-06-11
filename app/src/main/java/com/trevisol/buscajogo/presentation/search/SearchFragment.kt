package com.trevisol.buscajogo.presentation.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.fragment.findNavController
import com.trevisol.buscajogo.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private val viewModel: SearchViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                val query by viewModel.query.collectAsStateWithLifecycle()
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                val trendingGames by viewModel.trendingGames.collectAsStateWithLifecycle()

                SearchScreen(
                    query = query,
                    uiState = uiState,
                    trendingGames = trendingGames,
                    onQueryChanged = viewModel::onQueryChanged,
                    onGameClick = { gameId ->
                        findNavController().navigate(
                            R.id.details_dest,
                            bundleOf("gameId" to gameId)
                        )
                    }
                )
            }
        }
    }
}
