package com.example.presentation.audiolist

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.presentation.R
import com.example.presentation.databinding.FragmentAudioListBinding

class AudioListFragment : Fragment() {

    private var _binding: FragmentAudioListBinding? = null
    private val binding: FragmentAudioListBinding
    get() = _binding ?: throw RuntimeException("AudioListBinding is null")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAudioListBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}