package com.trevisol.buscajogo.presentation.library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
class LibraryFragment : Fragment() {

    private val viewModel: LibraryViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                val wishlist by viewModel.wishlist.collectAsStateWithLifecycle()
                val collection by viewModel.collection.collectAsStateWithLifecycle()

                LibraryScreen(
                    wishlist = wishlist,
                    collection = collection,
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
