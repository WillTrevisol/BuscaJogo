package com.trevisol.buscajogo.presentation.home

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.trevisol.buscajogo.R
import com.trevisol.buscajogo.databinding.FragmentHomeBinding
import com.trevisol.buscajogo.presentation.BaseFragment
import com.trevisol.buscajogo.presentation.home.adapter.DealAdapter
import com.trevisol.buscajogo.presentation.home.adapter.GameAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {

    private val viewModel: HomeViewModel by viewModels()
    private val dealAdapter = DealAdapter { deal ->
        viewModel.onDealClicked(deal)
    }
    private val gameAdapter = GameAdapter { game ->
        findNavController().navigate(
            R.id.details_dest,
            bundleOf("gameId" to game.id)
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerViews()
        observeViewModel()
    }

    private fun setupRecyclerViews() {
        binding.rvDeals.adapter = dealAdapter
        binding.rvPopular.adapter = gameAdapter
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.bestDeals.collect { deals ->
                        dealAdapter.submitList(deals)
                    }
                }
                launch {
                    viewModel.popularGames.collect { games ->
                        gameAdapter.submitList(games)
                    }
                }
                launch {
                    viewModel.isLoading.collect { isLoading ->
                        binding.progressBar.isVisible = isLoading
                    }
                }
                launch {
                    viewModel.navigateToDetails.collect { gameId ->
                        findNavController().navigate(
                            R.id.details_dest,
                            bundleOf("gameId" to gameId)
                        )
                    }
                }
                launch {
                    viewModel.errorEvent.collect { message ->
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}
