package com.trevisol.buscajogo.presentation.search

import android.os.Bundle
import android.view.View
import com.trevisol.buscajogo.databinding.FragmentPlaceholderBinding
import com.trevisol.buscajogo.presentation.BaseFragment

class SearchFragment : BaseFragment<FragmentPlaceholderBinding>(FragmentPlaceholderBinding::inflate) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.textView.text = "Search Screen"
    }
}
