package com.trevisol.buscajogo.presentation.home

import android.os.Bundle
import android.view.View
import com.trevisol.buscajogo.databinding.FragmentPlaceholderBinding
import com.trevisol.buscajogo.presentation.BaseFragment

class HomeFragment : BaseFragment<FragmentPlaceholderBinding>(FragmentPlaceholderBinding::inflate) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.textView.text = "Home Screen"
    }
}
