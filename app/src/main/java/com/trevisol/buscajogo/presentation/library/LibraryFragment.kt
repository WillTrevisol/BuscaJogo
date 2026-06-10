package com.trevisol.buscajogo.presentation.library

import android.os.Bundle
import android.view.View
import com.trevisol.buscajogo.databinding.FragmentPlaceholderBinding
import com.trevisol.buscajogo.presentation.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LibraryFragment : BaseFragment<FragmentPlaceholderBinding>(FragmentPlaceholderBinding::inflate) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.textView.text = "Library Screen"
    }
}
